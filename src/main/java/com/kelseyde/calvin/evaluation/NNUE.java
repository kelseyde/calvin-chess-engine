package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.File;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import jdk.incubator.vector.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.Deque;

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

    static final ShortVector FLOOR = ShortVector.broadcast(SPECIES, 0);
    static final ShortVector CEIL = ShortVector.broadcast(SPECIES, QA);

    // TODO test using array with single allocation at startup
    final Deque<Accumulator> accumulatorHistory = new ArrayDeque<>();
    Accumulator accumulator;
    Board board;

    public NNUE() {
        this.accumulator = new Accumulator(Network.HIDDEN_SIZE);
    }

    public NNUE(Board board) {
        this.accumulator = new Accumulator(Network.HIDDEN_SIZE);
        this.board = board;
        activateAll(board);
    }

    @Override
    public int evaluate() {

        boolean white = board.isWhite();
        short[] us = white ? accumulator.whiteFeatures : accumulator.blackFeatures;
        short[] them = white ? accumulator.blackFeatures : accumulator.whiteFeatures;
        short[] weights = Network.NETWORK.outputWeights;

        int eval = 0;

        for (int i = 0; i < SPECIES.loopBound(Network.HIDDEN_SIZE); i += SPECIES.length())
        {
            ShortVector usInputs = ShortVector.fromArray(SPECIES, us, i);
            ShortVector themInputs = ShortVector.fromArray(SPECIES, them, i);
            ShortVector usWeights = ShortVector.fromArray(SPECIES, weights, i);
            ShortVector themWeights = ShortVector.fromArray(SPECIES, weights, i + Network.HIDDEN_SIZE);

            usInputs = usInputs.max(ShortVector.zero(SPECIES)).min(ShortVector.broadcast(SPECIES, NNUE.QA));
            themInputs = themInputs.max(ShortVector.zero(SPECIES)).min(ShortVector.broadcast(SPECIES, NNUE.QA));

            ShortVector usWeightedTerms = usInputs.mul(usWeights);
            ShortVector themWeightedTerms = themInputs.mul(themWeights);

            Vector<Integer> usInputsLo = usInputs.convert(S2I, 0);
            Vector<Integer> usInputsHi = usInputs.convert(S2I, 1);
            Vector<Integer> themInputsLo = themInputs.convert(S2I, 0);
            Vector<Integer> themInputsHi = themInputs.convert(S2I, 1);

            Vector<Integer> usWeightedTermsLo = usWeightedTerms.convert(S2I, 0);
            Vector<Integer> usWeightedTermsHi = usWeightedTerms.convert(S2I, 1);
            Vector<Integer> themWeightedTermsLo = themWeightedTerms.convert(S2I, 0);
            Vector<Integer> themWeightedTermsHi = themWeightedTerms.convert(S2I, 1);

            eval += (int) usInputsLo.mul(usWeightedTermsLo).add(usInputsHi.mul(usWeightedTermsHi))
                    .add(themInputsLo.mul(themWeightedTermsLo)).add(themInputsHi.mul(themWeightedTermsHi)).reduceLanesToLong(VectorOperators.ADD);
        }

        //int eval = sum.reduceLanes(VectorOperators.ADD);

        eval /= QA;
        eval += Network.NETWORK.outputBias;
        eval *= SCALE;
        eval /= QAB;
        eval = scaleEval(board, eval);
        return eval;

    }

    /**
     * Forward pass through the network, using the squared clipped ReLU activation function.
     * Implementation uses the Java Vector API to perform SIMD operations on multiple features at once.
     */
    private int forward(short[] features, int weightOffset) {
        short[] weights = Network.NETWORK.outputWeights;

        IntVector sum = IntVector.zero(SPECIES.vectorShape().withLanes(int.class));

        for (int i = 0; i < UPPER_BOUND; i += SPECIES.length()) {

            ShortVector inputsVector = ShortVector.fromArray(SPECIES, features, i);
            ShortVector weightsVector = ShortVector.fromArray(SPECIES, weights, i + weightOffset);

            inputsVector = inputsVector.min(CEIL).max(FLOOR);

            ShortVector weightedTermsVector = inputsVector.mul(weightsVector);

            Vector<Integer> inputsLo = inputsVector.convert(S2I, 0);
            Vector<Integer> inputsHi = inputsVector.convert(S2I, 1);

            Vector<Integer> weightedTermsLo = weightedTermsVector.convert(S2I, 0);
            Vector<Integer> weightedTermsHi = weightedTermsVector.convert(S2I, 1);

            sum = sum.add(inputsLo.mul(weightedTermsLo)).add(inputsHi.mul(weightedTermsHi));

        }

        return sum.reduceLanes(VectorOperators.ADD);
    }

    private void activateAll(Board board) {

        for (int i = 0; i < Network.HIDDEN_SIZE; i++) {
            accumulator.whiteFeatures[i] = Network.NETWORK.inputBiases()[i];
            accumulator.blackFeatures[i] = Network.NETWORK.inputBiases()[i];
        }

        activateSide(board, board.getWhitePieces(), true);
        activateSide(board, board.getBlackPieces(), false);

    }

    private void activateSide(Board board, long pieces, boolean white) {
        while (pieces != 0) {
            int square = Bits.next(pieces);
            Piece piece = board.pieceAt(square);
            int whiteIndex = featureIndex(piece, square, white, true);
            int blackIndex = featureIndex(piece, square, white, false);
            accumulator.add(whiteIndex, blackIndex);
            pieces = Bits.pop(pieces);
        }
    }

    /**
     * Efficiently update only the relevant features of the network after a move has been made.
     */
    @Override
    public void makeMove(Board board, Move move) {
        accumulatorHistory.push(accumulator.copy());
        boolean white = board.isWhite();
        int from = move.from();
        int to = move.to();
        Piece piece = board.pieceAt(from);
        if (piece == null) return;
        Piece newPiece = move.isPromotion() ? move.promoPiece() : piece;
        Piece captured = move.isEnPassant() ? Piece.PAWN : board.pieceAt(to);

        int oldWhiteIdx = featureIndex(piece, from, white, true);
        int oldBlackIdx = featureIndex(piece, from, white, false);

        int newWhiteIdx = featureIndex(newPiece, to, white, true);
        int newBlackIdx = featureIndex(newPiece, to, white, false);

        if (move.isCastling()) {
            handleCastleMove(white, to, oldWhiteIdx, oldBlackIdx, newWhiteIdx, newBlackIdx);
        } else if (captured != null) {
            handleCapture(move, captured, white, newWhiteIdx, newBlackIdx, oldWhiteIdx, oldBlackIdx);
        } else {
            accumulator.addSub(newWhiteIdx, newBlackIdx, oldWhiteIdx, oldBlackIdx);
        }
    }

    private void handleCastleMove(boolean white, int to, int oldWhiteIdx, int oldBlackIdx, int newWhiteIdx, int newBlackIdx) {
        boolean isKingside = File.of(to) == 6;
        int rookStart = isKingside ? white ? 7 : 63 : white ? 0 : 56;
        int rookEnd = isKingside ? white ? 5 : 61 : white ? 3 : 59;
        int rookStartWhiteIdx = featureIndex(Piece.ROOK, rookStart, white, true);
        int rookStartBlackIdx = featureIndex(Piece.ROOK, rookStart, white, false);
        int rookEndWhiteIdx = featureIndex(Piece.ROOK, rookEnd, white, true);
        int rookEndBlackIdx = featureIndex(Piece.ROOK, rookEnd, white, false);
        accumulator.addAddSubSub(newWhiteIdx, newBlackIdx, rookEndWhiteIdx, rookEndBlackIdx, oldWhiteIdx, oldBlackIdx, rookStartWhiteIdx, rookStartBlackIdx);
    }

    private void handleCapture(Move move, Piece captured, boolean white, int newWhiteIdx, int newBlackIdx, int oldWhiteIdx, int oldBlackIdx) {
        int captureSquare = move.to();
        if (move.isEnPassant()) captureSquare = white ? move.to() - 8 : move.to() + 8;
        int capturedWhiteIdx = featureIndex(captured, captureSquare, !white, true);
        int capturedBlackIdx = featureIndex(captured, captureSquare, !white, false);
        accumulator.addSubSub(newWhiteIdx, newBlackIdx, oldWhiteIdx, oldBlackIdx, capturedWhiteIdx, capturedBlackIdx);
    }

    @Override
    public void unmakeMove() {
        this.accumulator = accumulatorHistory.pop();
    }

    @Override
    public void setPosition(Board board) {
        this.board = board;
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
        this.accumulator = new Accumulator(Network.HIDDEN_SIZE);
        this.accumulatorHistory.clear();
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
