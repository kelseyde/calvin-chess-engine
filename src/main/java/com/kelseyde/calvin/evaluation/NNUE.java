package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineInitializer;
import com.kelseyde.calvin.evaluation.Accumulator.AccumulatorUpdate;
import com.kelseyde.calvin.evaluation.Accumulator.FeatureUpdate;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NNUE implements Evaluation {

    public record Network(short[] inputWeights, short[] inputBiases, short[] outputWeights, short outputBias) {

        public static final String FILE = "tactician.nnue";
        public static final int INPUT_SIZE = 768;
        public static final int HIDDEN_SIZE = 256;

        public static final Network NETWORK = EngineInitializer.loadNetwork(FILE, INPUT_SIZE, HIDDEN_SIZE);

    }

    static final int COLOUR_OFFSET = 64 * 6;
    static final int PIECE_OFFSET = 64;
    static final int SCALE = 400;

    static final int QA = 255;
    static final int QB = 64;
    static final int QAB = QA * QB;

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
        boolean white = board.isWhiteToMove();
        short[] us = white ? acc.whiteFeatures : acc.blackFeatures;
        short[] them = white ? acc.blackFeatures : acc.whiteFeatures;
        int eval = Network.NETWORK.outputBias();
        eval += forward(us, 0);
        eval += forward(them, Network.HIDDEN_SIZE);
        eval *= SCALE;
        eval /= QAB;
        return eval;

    }

    /**
     * Forward pass through the network, using the clipped ReLU activation function.
     * Implementation uses the Java Vector API to perform SIMD operations on multiple features at once.
     */
    private int forward(short[] features, int weightOffset) {
        short[] weights = Network.NETWORK.outputWeights;
        short floor = 0;
        short ceil = QA;
        int sum = 0;

        VectorSpecies<Short> species = ShortVector.SPECIES_PREFERRED;
        for (int i = 0; i < species.loopBound(features.length); i += species.length()) {
            var featuresVector = ShortVector.fromArray(species, features, i);
            var weightsVector = ShortVector.fromArray(species, weights, i + weightOffset);

            var clippedVector = featuresVector.min(ceil).max(floor);
            var resultVector = clippedVector.mul(weightsVector);

            sum += resultVector.reduceLanes(VectorOperators.ADD);
        }

        return sum;
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
            int square = Bitwise.getNextBit(pieces);
            Piece piece = board.pieceAt(square);
            int whiteIndex = featureIndex(piece, square, white, true);
            int blackIndex = featureIndex(piece, square, white, false);
            acc.add(whiteIndex, blackIndex);
            pieces = Bitwise.popBit(pieces);
        }
    }

    /**
     * Efficiently update only the relevant features of the network after a move has been made.
     */
    @Override
    public void makeMove(Board board, Move move) {
        boolean white = board.isWhiteToMove();
        int startSquare = move.getFrom();
        int endSquare = move.getTo();
        Piece piece = board.pieceAt(startSquare);
        if (piece == null) return;
        Piece newPiece = move.isPromotion() ? move.getPromotionPiece() : piece;
        Piece capturedPiece = move.isEnPassant() ? Piece.PAWN : board.pieceAt(endSquare);

        Accumulator acc = accumulators[current].copy();
        if (move.isCastling()) {
            handleCastleMove(acc, move, white);
        } else if (capturedPiece != null) {
            handleCapture(acc, move, piece, newPiece, capturedPiece, white);
        } else {
            handleStandardMove(acc, move, piece, newPiece, white);
        }
        acc.dirty = true;
        this.accumulators[++current] = acc;
    }

    private void handleStandardMove(Accumulator acc, Move move, Piece piece, Piece newPiece, boolean white) {
        FeatureUpdate add = new FeatureUpdate(move.getTo(), newPiece, white);
        FeatureUpdate sub = new FeatureUpdate(move.getFrom(), piece, white);
        acc.update.pushAddSub(add, sub);
    }

    private void handleCastleMove(Accumulator acc, Move move, boolean white) {
        boolean kingside = Board.file(move.getTo()) == 6;
        int rookStart = kingside ? (white ? 7 : 63) : (white ? 0 : 56);
        int rookEnd = kingside ? (white ? 5 : 61) : (white ? 3 : 59);
        FeatureUpdate kingAdd = new FeatureUpdate(move.getTo(), Piece.KING, white);
        FeatureUpdate kingSub = new FeatureUpdate(move.getFrom(), Piece.KING, white);
        FeatureUpdate rookAdd = new FeatureUpdate(rookEnd, Piece.ROOK, white);
        FeatureUpdate rookSub = new FeatureUpdate(rookStart, Piece.ROOK, white);
        acc.update.pushAddAddSubSub(kingAdd, rookAdd, kingSub, rookSub);
    }

    private void handleCapture(Accumulator acc, Move move, Piece piece, Piece newPiece, Piece capturedPiece, boolean white) {
        int captureSquare = move.getTo();
        if (move.isEnPassant()) captureSquare = white ? move.getTo() - 8 : move.getTo() + 8;
        FeatureUpdate add = new FeatureUpdate(move.getTo(), newPiece, white);
        FeatureUpdate sub1 = new FeatureUpdate(captureSquare, capturedPiece, !white);
        FeatureUpdate sub2 = new FeatureUpdate(move.getFrom(), piece, white);
        acc.update.pushAddSubSub(add, sub1, sub2);
    }

    private void applyLazyUpdates() {

        // Scan back to the last non-dirty accumulator.
        if (!accumulators[current].dirty) return;
        int i = current - 1;
        while (i >= 0 && accumulators[i].dirty) i--;

        while (i < current) {
            if (i + 1 >= accumulators.length) break;
            Accumulator prev = accumulators[i];
            Accumulator curr = accumulators[i + 1];
            AccumulatorUpdate update = curr.update;
            if (update.addCount == 1 && update.subCount == 1) {
                lazyUpdateAddSub(prev, curr);
            } else if (update.addCount == 1 && update.subCount == 2) {
                lazyUpdateAddSubSub(prev, curr);
            } else if (update.addCount == 2 && update.subCount == 2) {
                lazyUpdateAddAddSubSub(prev, curr);
            }
            curr.dirty = false;
            i++;
        }

    }

    private void lazyUpdateAddSub(Accumulator prev, Accumulator curr) {
        AccumulatorUpdate update = curr.update;
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
        AccumulatorUpdate update = curr.update;
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
        AccumulatorUpdate update = curr.update;
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

    /**
     * Compute the index of the feature vector for a given piece, colour and square. Features from black's perspective
     * are mirrored (the square index is vertically flipped) in order to preserve symmetry.
     */
    private static int featureIndex(Piece piece, int square, boolean whitePiece, boolean whitePerspective) {
        int squareIndex = whitePerspective ? square : square ^ 56;
        int pieceIndex = piece.getIndex();
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

}
