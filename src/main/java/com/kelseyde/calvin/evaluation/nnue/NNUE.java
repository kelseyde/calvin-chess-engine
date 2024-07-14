package com.kelseyde.calvin.evaluation.nnue;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.utils.BoardUtils;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.util.ArrayDeque;
import java.util.Deque;

public class NNUE implements Evaluation {

    private static final int COLOUR_STRIDE = 64 * 6;
    private static final int PIECE_STRIDE = 64;

    private static final int SCALE = 400;
    private static final int QA = 255;
    private static final int QB = 64;
    private static final int QAB = QA * QB;
    private static final VectorSpecies<Short> SPECIES = ShortVector.SPECIES_PREFERRED;


    public Accumulator accumulator;
    private Deque<Accumulator> accumulatorHistory = new ArrayDeque<>();

    public NNUE() {
        this.accumulator = new Accumulator(Network.L1_SIZE);
    }

    public NNUE(Board board) {
        this.accumulator = new Accumulator(Network.L1_SIZE);
        activateAll(board);
    }

    @Override
    public int evaluate(Board board) {
        boolean white = board.isWhiteToMove();
        short[] us = white ? accumulator.whiteFeatures : accumulator.blackFeatures;
        short[] them = white ? accumulator.blackFeatures : accumulator.whiteFeatures;
        int eval = Network.DEFAULT.l1bias;
        eval += forward(us, 0);
        eval += forward(them, Network.L1_SIZE);
        eval *= SCALE;
        eval /= QAB;
        return eval;
    }

    private int forward(short[] features, int weightOffset) {
        short[] weights = Network.DEFAULT.l1weights;
        short floor = 0;
        short ceil = QA;
        int sum = 0;
        int i = 0;

        for (; i < SPECIES.loopBound(features.length); i += SPECIES.length()) {
            var vFeatures = ShortVector.fromArray(SPECIES, features, i);
            var vWeights = ShortVector.fromArray(SPECIES, weights, i + weightOffset);

            var vClipped = vFeatures.min(ceil).max(floor);
            var vResult = vClipped.mul(vWeights);

            sum += vResult.reduceLanes(VectorOperators.ADD);
        }

        for (; i < features.length; i++) {
            short clipped = (short) Math.max(Math.min(features[i], ceil), floor);
            sum += clipped * weights[i + weightOffset];
        }

        return sum;
    }

    public int evaluate2(Board board) {
        boolean white = board.isWhiteToMove();
        int output = white ?
                forward(accumulator.whiteFeatures, accumulator.blackFeatures, Network.DEFAULT.l1weights) :
                forward(accumulator.blackFeatures, accumulator.whiteFeatures, Network.DEFAULT.l1weights);
        return (output + Network.DEFAULT.l1bias) * SCALE / QAB;
    }

    private int forward(short[] us, short[] them, short[] weights) {
        return forward(us, 0) + forward(them, Network.L1_SIZE);
    }

    public void activateAll(Board board) {

        for (int i = 0; i < Network.L1_SIZE; i++) {
            accumulator.whiteFeatures[i] = Network.DEFAULT.l0biases[i];
            accumulator.blackFeatures[i] = Network.DEFAULT.l0biases[i];
        }

        activateSide(board, board.getWhitePieces(), true);
        activateSide(board, board.getBlackPieces(), false);

    }

    private static short crelu(short i) {
        return (short) Math.max(0, Math.min(i, QA));
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

    @Override
    public void makeMove(Board board, Move move) {
        accumulatorHistory.push(accumulator.copy());
        boolean white = board.isWhiteToMove();
        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();
        Piece piece = board.pieceAt(startSquare);
        Piece newPiece = move.isPromotion() ? move.getPromotionPiece() : piece;
        Piece capturedPiece = move.isEnPassant() ? Piece.PAWN : board.pieceAt(endSquare);

        int oldPieceWix = featureIndex(piece, startSquare, white, true);
        int oldPieceBix = featureIndex(piece, startSquare, white, false);

        int newPieceWix = featureIndex(newPiece, endSquare, white, true);
        int newPieceBix = featureIndex(newPiece, endSquare, white, false);

        if (move.isCastling()) {
            handleCastleMove(white, endSquare, oldPieceWix, oldPieceBix, newPieceWix, newPieceBix);
        } else if (capturedPiece != null) {
            handleCapture(move, capturedPiece, white, newPieceWix, newPieceBix, oldPieceWix, oldPieceBix);
        } else {
            accumulator.addSub(newPieceWix, newPieceBix, oldPieceWix, oldPieceBix);
        }
    }

    private void handleCastleMove(boolean white, int endSquare, int oldPieceWix, int oldPieceBix, int newPieceWix, int newPieceBix) {
        boolean isKingside = BoardUtils.getFile(endSquare) == 6;
        int rookStart = isKingside ? white ? 7 : 63 : white ? 0 : 56;
        int rookEnd = isKingside ? white ? 5 : 61 : white ? 3 : 59;
        int rookStartWix = featureIndex(Piece.ROOK, rookStart, white, true);
        int rookStartBix = featureIndex(Piece.ROOK, rookStart, white, false);
        int rookEndWix = featureIndex(Piece.ROOK, rookEnd, white, true);
        int rookEndBix = featureIndex(Piece.ROOK, rookEnd, white, false);
        accumulator.addAddSubSub(newPieceWix, newPieceBix, rookEndWix, rookEndBix, oldPieceWix, oldPieceBix, rookStartWix, rookStartBix);
    }

    private void handleCapture(Move move, Piece capturedPiece, boolean white, int newPieceWix, int newPieceBix, int oldPieceWix, int oldPieceBix) {
        int captureSquare = move.getEndSquare();
        if (move.isEnPassant()) captureSquare = white ? move.getEndSquare() - 8 : move.getEndSquare() + 8;
        int capturedPieceWix = featureIndex(capturedPiece, captureSquare, !white, true);
        int capturedPieceBix = featureIndex(capturedPiece, captureSquare, !white, false);
        accumulator.addSubSub(newPieceWix, newPieceBix, oldPieceWix, oldPieceBix, capturedPieceWix, capturedPieceBix);
    }

    @Override
    public void unmakeMove() {
        this.accumulator = accumulatorHistory.pop();
    }

    private static int featureIndex(Piece piece, int square, boolean white, boolean whiteIndex) {
        int squareIndex = whiteIndex ? square : square ^ 56;
        int pieceIndex = piece.getIndex();
        int pieceOffset = pieceIndex * PIECE_STRIDE;
        boolean isSideToMoveFeature = white == whiteIndex;
        int colourOffset = isSideToMoveFeature ? 0 : COLOUR_STRIDE;
        return colourOffset + pieceOffset + squareIndex;
    }

    public static int featureIndex2(Piece piece, int square, boolean white, boolean whiteIndex) {
        int pieceIndex = piece.getIndex();
        int colourIndex = colourIndex(white);
        if (whiteIndex) {
            return (colourIndex ^ 1) * COLOUR_STRIDE + pieceIndex * PIECE_STRIDE + square;
        } else {
            return colourIndex * COLOUR_STRIDE + pieceIndex * PIECE_STRIDE + square ^ 0x38;
        }
    }

    public static int colourIndex(boolean white) {
        return white ? 1 : 0;
    }

    @Override
    public void clearHistory() {
        this.accumulator = new Accumulator(Network.L1_SIZE);
        this.accumulatorHistory = new ArrayDeque<>();
    }

    public static void main(String[] args) {
        Board board = new Board();
        NNUE nnue = new NNUE(board);
        nnue.evaluate2(board);
    }

}
