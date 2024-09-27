package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.File;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.evaluation.Accumulator.FeatureUpdate;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.Vector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static jdk.incubator.vector.VectorOperators.S2I;

/**
 * Implementation of {@link Evaluation} using an NNUE (Efficiently Updatable Neural Network) evaluation function.
 * <p>
 * The network has an input layer of 768 neurons, each representing the presence of a piece of each colour on a square
 * (64 squares * 6 pieces * 2 colours). Two versions of the hidden layer are accumulated: one from white's perspective
 * and one from black's. It is 'efficiently updatable' due to the fact that, on each move, only the features of the
 * relevant pieces need to be re-calculated, not the features of the entire board; this is a significant speed boost.
 * <p>
 * The network was trained on positions taken from a dataset of Leela Chess Zero, which were then re-scored with
 * Calvin's own search and hand-crafted evaluation.
 *
 * @see <a href="https://www.chessprogramming.org/UCI">Chess Programming Wiki</a>
 */
public class NNUE implements Evaluation {

    public record Network(short[] inputWeights, short[] inputBiases, short[] outputWeights, short outputBias) {

        public static final String FILE = "sol.nnue";
        public static final int INPUT_SIZE = 768;
        public static final int HIDDEN_SIZE = 384;

        public static final Network NETWORK = loadNetwork(FILE, INPUT_SIZE, HIDDEN_SIZE);

    }

    static final int COLOUR_OFFSET = Square.COUNT * Piece.COUNT;
    static final int PIECE_OFFSET = Square.COUNT;
    static final int SCALE = 400;

    static final int QA = 255;
    static final int QB = 64;
    static final int QAB = QA * QB;

    static final int MATERIAL_BASE = 22400;
    static final int MATERIAL_FACTOR = 32768;

    static final VectorSpecies<Short> SPECIES = ShortVector.SPECIES_PREFERRED;
    static final int UPPER_BOUND = SPECIES.loopBound(Network.HIDDEN_SIZE);
    static final int LOOP_LENGTH = SPECIES.length();

    static final ShortVector FLOOR = ShortVector.broadcast(SPECIES, 0);
    static final ShortVector CEIL = ShortVector.broadcast(SPECIES, QA);

    final Accumulator[] accumulators = new Accumulator[256];
    int current = 0;
    Board board;

    public NNUE() {
        this.accumulators[current] = new Accumulator(Network.HIDDEN_SIZE);
    }

    public NNUE(Board board) {
        this.accumulators[current] = new Accumulator(Network.HIDDEN_SIZE);
        this.board = board;
        activateAll(board);
    }

    @Override
    public int evaluate() {

        applyLazyUpdates();
        Accumulator acc = this.accumulators[current];
        boolean white = board.isWhite();
        short[] weights = Network.NETWORK.outputWeights;

        // Get the 'us-perspective' and 'them-perspective' feature sets, based on the side to move.
        short[] us = white ? acc.whiteFeatures : acc.blackFeatures;
        short[] them = white ? acc.blackFeatures : acc.whiteFeatures;

        int eval = 0;

        // Forward-pass through the network, using the squared clipped ReLU activation function.
        // Implementation uses the Java Vector API to perform SIMD operations on multiple features at once.
        for (int i = 0; i < UPPER_BOUND; i += LOOP_LENGTH) {

            ShortVector usInputs = ShortVector.fromArray(SPECIES, us, i);
            ShortVector themInputs = ShortVector.fromArray(SPECIES, them, i);
            ShortVector usWeights = ShortVector.fromArray(SPECIES, weights, i);
            ShortVector themWeights = ShortVector.fromArray(SPECIES, weights, i + Network.HIDDEN_SIZE);

            // Clip the inputs to the range [0, 255].
            usInputs = usInputs.max(FLOOR).min(CEIL);
            themInputs = themInputs.max(FLOOR).min(CEIL);

            // Multiply the inputs by the weights.
            ShortVector usTerms = usInputs.mul(usWeights);
            ShortVector themTerms = themInputs.mul(themWeights);

            // Split the inputs and weighted terms into low and high parts, to enable 32-bit multiplication.
            Vector<Integer> usInputsLo = usInputs.convert(S2I, 0);
            Vector<Integer> usInputsHi = usInputs.convert(S2I, 1);
            Vector<Integer> themInputsLo = themInputs.convert(S2I, 0);
            Vector<Integer> themInputsHi = themInputs.convert(S2I, 1);

            Vector<Integer> usTermsLo = usTerms.convert(S2I, 0);
            Vector<Integer> usTermsHi = usTerms.convert(S2I, 1);
            Vector<Integer> themTermsLo = themTerms.convert(S2I, 0);
            Vector<Integer> themTermsHi = themTerms.convert(S2I, 1);

            // Multiply the inputs by the weighted terms, and add the results to the running sum.
            eval += (int) usInputsLo.mul(usTermsLo)
                    .add(usInputsHi.mul(usTermsHi))
                    .add(themInputsLo.mul(themTermsLo))
                    .add(themInputsHi.mul(themTermsHi))
                    .reduceLanesToLong(VectorOperators.ADD);

        }

        // Since squaring the inputs also squares quantisation, we need to divide that out.
        eval /= QA;

        // Add the output bias, scale the result, and divide by the quantisation factor.
        eval += Network.NETWORK.outputBias;
        eval *= SCALE;
        eval /= QAB;

        // Scale the evaluation based on the material and proximity to 50-move rule draw.
        eval = scaleEval(board, eval);

        return eval;

    }

    private void activateAll(Board board) {

        for (int i = 0; i < Network.HIDDEN_SIZE; i++) {
            this.accumulators[current].whiteFeatures[i] = Network.NETWORK.inputBiases()[i];
            this.accumulators[current].blackFeatures[i] = Network.NETWORK.inputBiases()[i];
        }

        activateSide(board, board.getWhitePieces(), true);
        activateSide(board, board.getBlackPieces(), false);

    }

    private void activateSide(Board board, long pieces, boolean white) {
        Accumulator acc = this.accumulators[current];
        while (pieces != 0) {
            int square = Bits.next(pieces);
            Piece piece = board.pieceAt(square);
            int whiteIndex = featureIndex(piece, square, white, true);
            int blackIndex = featureIndex(piece, square, white, false);
            acc.add(whiteIndex, blackIndex);
            pieces = Bits.pop(pieces);
        }
    }

    /**
     * Efficiently update only the relevant features of the network after a move has been made.
     */
    @Override
    public void makeMove(Board board, Move move) {
        boolean white = board.isWhite();
        int from = move.from();
        int to = move.to();
        Piece piece = board.pieceAt(from);
        if (piece == null) return;
        Piece newPiece = move.isPromotion() ? move.promoPiece() : piece;
        Piece captured = move.isEnPassant() ? Piece.PAWN : board.pieceAt(to);

        Accumulator acc = accumulators[current].copy();
        if (move.isCastling()) {
            handleCastleMove(acc, move, white);
        } else if (captured != null) {
            handleCapture(acc, move, piece, newPiece, captured, white);
        } else {
            handleStandardMove(acc, move, piece, newPiece, white);
        }
        acc.correct = false;
        this.accumulators[++current] = acc;
    }

    private void handleStandardMove(Accumulator acc, Move move, Piece piece, Piece newPiece, boolean white) {
        FeatureUpdate add = new FeatureUpdate(move.to(), newPiece, white);
        FeatureUpdate sub = new FeatureUpdate(move.from(), piece, white);
        acc.update.pushAddSub(add, sub);
    }

    private void handleCastleMove(Accumulator acc, Move move, boolean white) {
        boolean kingside = File.of(move.to()) == 6;
        int rookStart = kingside ? (white ? 7 : 63) : (white ? 0 : 56);
        int rookEnd = kingside ? (white ? 5 : 61) : (white ? 3 : 59);
        FeatureUpdate kingAdd = new FeatureUpdate(move.to(), Piece.KING, white);
        FeatureUpdate kingSub = new FeatureUpdate(move.from(), Piece.KING, white);
        FeatureUpdate rookAdd = new FeatureUpdate(rookEnd, Piece.ROOK, white);
        FeatureUpdate rookSub = new FeatureUpdate(rookStart, Piece.ROOK, white);
        acc.update.pushAddAddSubSub(kingAdd, rookAdd, kingSub, rookSub);
    }

    private void handleCapture(Accumulator acc, Move move, Piece piece, Piece newPiece, Piece captured, boolean white) {
        int captureSquare = move.to();
        if (move.isEnPassant()) captureSquare = white ? move.to() - 8 : move.to() + 8;
        FeatureUpdate add = new FeatureUpdate(move.to(), newPiece, white);
        FeatureUpdate sub1 = new FeatureUpdate(captureSquare, captured, !white);
        FeatureUpdate sub2 = new FeatureUpdate(move.from(), piece, white);
        acc.update.pushAddSubSub(add, sub1, sub2);
    }

    private void applyLazyUpdates() {

        // Scan back to the last non-dirty accumulator.
        if (accumulators[current].correct) return;
        int i = current - 1;
        while (i >= 0 && !accumulators[i].correct) i--;

        while (i < current) {
            if (i + 1 >= accumulators.length) break;
            Accumulator prev = accumulators[i];
            Accumulator curr = accumulators[i + 1];
            Accumulator.AccumulatorUpdate update = curr.update;
            if (update.addCount == 1 && update.subCount == 1) {
                lazyUpdateAddSub(prev, curr);
            } else if (update.addCount == 1 && update.subCount == 2) {
                lazyUpdateAddSubSub(prev, curr);
            } else if (update.addCount == 2 && update.subCount == 2) {
                lazyUpdateAddAddSubSub(prev, curr);
            }
            curr.correct = true;
            i++;
        }

    }

    private void lazyUpdateAddSub(Accumulator prev, Accumulator curr) {
        Accumulator.AccumulatorUpdate update = curr.update;
        FeatureUpdate add = update.adds[0];
        FeatureUpdate sub = update.subs[0];
        int whiteAddIdx = featureIndex(add.piece(), add.square(), add.white(), true);
        int blackAddIdx = featureIndex(add.piece(), add.square(), add.white(), false);
        int whiteSubIdx = featureIndex(sub.piece(), sub.square(), sub.white(), true);
        int blackSubIdx = featureIndex(sub.piece(), sub.square(), sub.white(), false);
        curr.addSub(prev.whiteFeatures, prev.blackFeatures,
                whiteAddIdx, blackAddIdx, whiteSubIdx, blackSubIdx);
    }

    private void lazyUpdateAddSubSub(Accumulator prev, Accumulator curr) {
        Accumulator.AccumulatorUpdate update = curr.update;
        FeatureUpdate add1 = update.adds[0];
        FeatureUpdate sub1 = update.subs[0];
        FeatureUpdate sub2 = update.subs[1];
        int whiteAdd1Idx = featureIndex(add1.piece(), add1.square(), add1.white(), true);
        int blackAdd1Idx = featureIndex(add1.piece(), add1.square(), add1.white(), false);
        int whiteSub1Idx = featureIndex(sub1.piece(), sub1.square(), sub1.white(), true);
        int blackSub1Idx = featureIndex(sub1.piece(), sub1.square(), sub1.white(), false);
        int whiteSub2Idx = featureIndex(sub2.piece(), sub2.square(), sub2.white(), true);
        int blackSub2Idx = featureIndex(sub2.piece(), sub2.square(), sub2.white(), false);
        curr.addSubSub(prev.whiteFeatures, prev.blackFeatures,
                whiteAdd1Idx, blackAdd1Idx, whiteSub1Idx, blackSub1Idx, whiteSub2Idx, blackSub2Idx);
    }

    private void lazyUpdateAddAddSubSub(Accumulator prev, Accumulator curr) {
        Accumulator.AccumulatorUpdate update = curr.update;
        FeatureUpdate add1 = update.adds[0];
        FeatureUpdate add2 = update.adds[1];
        FeatureUpdate sub1 = update.subs[0];
        FeatureUpdate sub2 = update.subs[1];
        int whiteAdd1Idx = featureIndex(add1.piece(), add1.square(), add1.white(), true);
        int blackAdd1Idx = featureIndex(add1.piece(), add1.square(), add1.white(), false);
        int whiteAdd2Idx = featureIndex(add2.piece(), add2.square(), add2.white(), true);
        int blackAdd2Idx = featureIndex(add2.piece(), add2.square(), add2.white(), false);
        int whiteSub1Idx = featureIndex(sub1.piece(), sub1.square(), sub1.white(), true);
        int blackSub1Idx = featureIndex(sub1.piece(), sub1.square(), sub1.white(), false);
        int whiteSub2Idx = featureIndex(sub2.piece(), sub2.square(), sub2.white(), true);
        int blackSub2Idx = featureIndex(sub2.piece(), sub2.square(), sub2.white(), false);
        curr.addAddSubSub(prev.whiteFeatures, prev.blackFeatures,
                whiteAdd1Idx, blackAdd1Idx, whiteAdd2Idx, blackAdd2Idx, whiteSub1Idx, blackSub1Idx, whiteSub2Idx, blackSub2Idx);
    }

    @Override
    public void unmakeMove() {
        current--;
    }

    @Override
    public void setPosition(Board board) {
        this.board = board;
        this.current = 0;
        this.accumulators[current] = new Accumulator(Network.HIDDEN_SIZE);
        activateAll(board);
    }

    private int scaleEval(Board board, int eval) {
        int materialPhase = materialPhase(board);
        eval = eval * materialPhase / MATERIAL_FACTOR;
        eval = eval * (200 - board.getState().getHalfMoveClock()) / 200;
        return eval;
    }

    private int materialPhase(Board board) {
        long knights = Bits.count(board.getKnights());
        long bishops = Bits.count(board.getBishops());
        long rooks = Bits.count(board.getRooks());
        long queens = Bits.count(board.getQueens());
        return (int) (MATERIAL_BASE + 3 * knights + 3 * bishops + 5 * rooks + 10 * queens);
    }

    /**
     * Compute the index of the feature vector for a given piece, colour and square. Features from black's perspective
     * are mirrored (the square index is vertically flipped) in order to preserve symmetry.
     */
    private static int featureIndex(Piece piece, int square, boolean whitePiece, boolean whitePerspective) {
        int squareIndex = whitePerspective ? square : square ^ 56;
        int pieceIndex = piece.index();
        int pieceOffset = pieceIndex * PIECE_OFFSET;
        boolean ourPiece = whitePiece == whitePerspective;
        int colourOffset = ourPiece ? 0 : COLOUR_OFFSET;
        return colourOffset + pieceOffset + squareIndex;
    }

    @Override
    public void clearHistory() {
        Arrays.fill(this.accumulators, null);
        this.current = 0;
    }

    public static NNUE.Network loadNetwork(String file, int inputSize, int hiddenSize) {
        try {
            InputStream inputStream = NNUE.Network.class.getClassLoader().getResourceAsStream(file);
            if (inputStream == null) {
                throw new FileNotFoundException("NNUE file not found in resources");
            }

            byte[] fileBytes = inputStream.readAllBytes();
            inputStream.close();
            ByteBuffer buffer = ByteBuffer.wrap(fileBytes).order(ByteOrder.LITTLE_ENDIAN);

            int inputWeightsOffset = inputSize * hiddenSize;
            int inputBiasesOffset = hiddenSize;
            int outputWeightsOffset = hiddenSize * 2;

            short[] inputWeights = new short[inputWeightsOffset];
            short[] inputBiases = new short[inputBiasesOffset];
            short[] outputWeights = new short[outputWeightsOffset];

            for (int i = 0; i < inputWeightsOffset; i++) {
                inputWeights[i] = buffer.getShort();
            }

            for (int i = 0; i < inputBiasesOffset; i++) {
                inputBiases[i] = buffer.getShort();
            }

            for (int i = 0; i < outputWeightsOffset; i++) {
                outputWeights[i] = buffer.getShort();
            }

            short outputBias = buffer.getShort();

            while (buffer.hasRemaining()) {
                if (buffer.getShort() != 0) {
                    throw new RuntimeException("Failed to load NNUE network: invalid file format");
                }
            }

            return new NNUE.Network(inputWeights, inputBiases, outputWeights, outputBias);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load NNUE network", e);
        }
    }

}
