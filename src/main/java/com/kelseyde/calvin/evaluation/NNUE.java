package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineInitializer;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.util.*;

public class NNUE implements Evaluation {

    public record Network(short[] inputWeights, short[] inputBiases, short[] outputWeights, short outputBias) {

        public static final String FILE = "net010.bin";
        public static final int INPUT_SIZE = 768;
        public static final int HIDDEN_SIZE = 768;

        public static final Network NETWORK = EngineInitializer.loadNetwork(FILE, INPUT_SIZE, HIDDEN_SIZE);

    }

    private static final int COLOUR_OFFSET = 64 * 6;
    private static final int PIECE_OFFSET = 64;
    private static final int SCALE = 400;
    private static final int QA = 255;
    private static final int QB = 64;
    private static final int QAB = QA * QB;

    private final static int[] CRELU = new int[Short.MAX_VALUE - Short.MIN_VALUE + 1];

    static {
        for (int i = Short.MIN_VALUE; i <= Short.MAX_VALUE;i ++)
            CRELU[i - (int) Short.MIN_VALUE] = crelu((short) (i));
    }

    public Accumulator accumulator;
    private Deque<Accumulator> accumulatorHistory = new ArrayDeque<>();

    public NNUE() {
        this.accumulator = new Accumulator(Network.HIDDEN_SIZE);
    }

    public NNUE(Board board) {
        this.accumulator = new Accumulator(Network.HIDDEN_SIZE);
        activateAll(board);
    }

    @Override
    public int evaluate(Board board) {

        boolean white = board.isWhiteToMove();
        short[] us = white ? accumulator.whiteFeatures : accumulator.blackFeatures;
        short[] them = white ? accumulator.blackFeatures : accumulator.whiteFeatures;
        int eval = Network.NETWORK.outputBias();

        short[] weights = Network.NETWORK.outputWeights;
        for (int i = 0; i < Network.HIDDEN_SIZE; i++) {
            eval += CRELU[us[i] - (int) Short.MIN_VALUE] * (int) weights[i]
                  + CRELU[them[i] - (int) Short.MIN_VALUE] * (int) weights[i + Network.HIDDEN_SIZE];
        }

        eval *= SCALE;
        eval /= QAB;
        return eval;

    }

    /**
     * Forward pass through the network, using the clipped ReLU activation function.
     */
    private int forward(short[] features, int weightOffset) {
        short[] weights = Network.NETWORK.outputWeights;
        short floor = 0;
        short ceil = QA;
        int sum = 0;
        int i = 0;

        VectorSpecies<Short> species = ShortVector.SPECIES_PREFERRED;
        for (; i < species.loopBound(features.length); i += species.length()) {
            var featuresVector = ShortVector.fromArray(species, features, i);
            var weightsVector = ShortVector.fromArray(species, weights, i + weightOffset);

            var clippedVector = featuresVector.min(ceil).max(floor);
            var resultVector = clippedVector.mul(weightsVector);

            sum += resultVector.reduceLanes(VectorOperators.ADD);
        }

        for (; i < features.length; i++) {
            short clipped = (short) Math.max(Math.min(features[i], ceil), floor);
            sum += clipped * weights[i + weightOffset];
        }

        return sum;
    }

    public void activateAll(Board board) {

        for (int i = 0; i < Network.HIDDEN_SIZE; i++) {
            accumulator.whiteFeatures[i] = Network.NETWORK.inputBiases()[i];
            accumulator.blackFeatures[i] = Network.NETWORK.inputBiases()[i];
        }

        activateSide(board, board.getWhitePieces(), true);
        activateSide(board, board.getBlackPieces(), false);

    }

    private void activateSide(Board board, long pieces, boolean white) {
        while (pieces != 0) {
            int square = Bitwise.getNextBit(pieces);
            Piece piece = board.pieceAt(square);
            int whiteIndex = featureIndex(piece, square, white, true);
            int blackIndex = featureIndex(piece, square, white, false);
            accumulator.add(whiteIndex, blackIndex);
            pieces = Bitwise.popBit(pieces);
        }
    }

    private static int crelu(int x) {
        return Math.min(Math.max(x, 0), QA);
    }

    @Override
    public void makeMove(Board board, Move move) {
        accumulatorHistory.push(accumulator.copy());
        boolean white = board.isWhiteToMove();
        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();
        Piece piece = board.pieceAt(startSquare);
        Piece newPiece = move.isPromotion() ? move.getPromotionPiece() : piece;
        Piece capturedPiece = move.isEnPassant() ? Piece.PAWN : board.pieceAt(endSquare);

        int oldWhiteIdx = featureIndex(piece, startSquare, white, true);
        int oldBlackIdx = featureIndex(piece, startSquare, white, false);

        int newWhiteIdx = featureIndex(newPiece, endSquare, white, true);
        int newBlackIdx = featureIndex(newPiece, endSquare, white, false);

        if (move.isCastling()) {
            handleCastleMove(white, endSquare, oldWhiteIdx, oldBlackIdx, newWhiteIdx, newBlackIdx);
        } else if (capturedPiece != null) {
            handleCapture(move, capturedPiece, white, newWhiteIdx, newBlackIdx, oldWhiteIdx, oldBlackIdx);
        } else {
            accumulator.addSub(newWhiteIdx, newBlackIdx, oldWhiteIdx, oldBlackIdx);
        }
    }

    private void handleCastleMove(boolean white, int endSquare, int oldWhiteIdx, int oldBlackIdx, int newWhiteIdx, int newBlackIdx) {
        boolean isKingside = Board.file(endSquare) == 6;
        int rookStart = isKingside ? white ? 7 : 63 : white ? 0 : 56;
        int rookEnd = isKingside ? white ? 5 : 61 : white ? 3 : 59;
        int rookStartWhiteIdx = featureIndex(Piece.ROOK, rookStart, white, true);
        int rookStartBlackIdx = featureIndex(Piece.ROOK, rookStart, white, false);
        int rookEndWhiteIdx = featureIndex(Piece.ROOK, rookEnd, white, true);
        int rookEndBlackIdx = featureIndex(Piece.ROOK, rookEnd, white, false);
        accumulator.addAddSubSub(newWhiteIdx, newBlackIdx, rookEndWhiteIdx, rookEndBlackIdx, oldWhiteIdx, oldBlackIdx, rookStartWhiteIdx, rookStartBlackIdx);
    }

    private void handleCapture(Move move, Piece capturedPiece, boolean white, int newWhiteIdx, int newBlackIdx, int oldWhiteIdx, int oldBlackIdx) {
        int captureSquare = move.getEndSquare();
        if (move.isEnPassant()) captureSquare = white ? move.getEndSquare() - 8 : move.getEndSquare() + 8;
        int capturedWhiteIdx = featureIndex(capturedPiece, captureSquare, !white, true);
        int capturedBlackIdx = featureIndex(capturedPiece, captureSquare, !white, false);
        accumulator.addSubSub(newWhiteIdx, newBlackIdx, oldWhiteIdx, oldBlackIdx, capturedWhiteIdx, capturedBlackIdx);
    }

    @Override
    public void unmakeMove() {
        this.accumulator = accumulatorHistory.pop();
    }

    private static int featureIndex(Piece piece, int square, boolean whitePiece, boolean whitePerspective) {
        int squareIndex = whitePerspective ? square : square ^ 56;
        int pieceIndex = piece.getIndex();
        int pieceOffset = pieceIndex * PIECE_OFFSET;
        boolean ourPiece = whitePiece == whitePerspective;
        int colourOffset = ourPiece ? 0 : COLOUR_OFFSET;
        return colourOffset + pieceOffset + squareIndex;
    }

    public static Set<Integer> getFeatureActivations(Board board, boolean whitePerspective) {
        Set<Integer> featureIndices = new HashSet<>();
        long pieces = board.getWhitePieces() | board.getBlackPieces();
        while (pieces != 0) {
            int square = Bitwise.getNextBit(pieces);
            Piece piece = board.pieceAt(square);
            boolean whitePiece = (board.getWhitePieces() & 1L << square) != 0;
            int index = featureIndex(piece, square, whitePiece, whitePerspective);
            featureIndices.add(index);
            pieces = Bitwise.popBit(pieces);
        }
        return featureIndices;
    }

    @Override
    public void clearHistory() {
        this.accumulator = new Accumulator(Network.HIDDEN_SIZE);
        this.accumulatorHistory.clear();
    }

}
