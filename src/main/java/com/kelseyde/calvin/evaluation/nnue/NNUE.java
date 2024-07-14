package com.kelseyde.calvin.evaluation.nnue;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.utils.BoardUtils;

import java.util.ArrayDeque;
import java.util.Deque;

public class NNUE implements Evaluation {

    private static final int COLOUR_STRIDE = 64 * 6;
    private static final int PIECE_STRIDE = 64;

    private static final int SCALE = 400;
    private static final int QA = 255;
    private static final int QB = 64;
    private static final int QAB = QA * QB;

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
        short[] weights = Network.DEFAULT.l1weights;

        int eval = Network.DEFAULT.l1bias;
        for (int i = 0; i < Network.L1_SIZE; i++) {
//            short activationUs = us[i];
//            short clippedUs = crelu(activationUs);
//            short weightUs = weights[i];
//
//            short activationThem = them[i];
//            short clippedThem = crelu(activationThem);
//            short weightThem = weights[i + Network.L1_SIZE];
//
//            System.out.printf("aUs: %s cUs: %s wUs: %s aThem: %s cThem: %s wThem: %s%n", activationUs, clippedUs, weightUs, activationThem, clippedThem, weightThem);
//            System.out.printf("us = %s * %s = %s --- them = %s * %s = %s%n", clippedUs, weightUs, clippedUs * weightUs, clippedThem, weightThem, clippedThem * weightThem);
//            System.out.printf("eval = %s + %s + %s = %s%n", eval, clippedUs * weightUs, clippedThem * weightThem, eval + clippedUs * weightUs + clippedThem * weightThem);

            eval += crelu(us[i]) * weights[i] + crelu(them[i]) * weights[i + Network.L1_SIZE];
//            System.out.printf("                                                                 eval: %s%n", eval);
//            System.out.println("eval: " + eval);
        }

//        System.out.println("eval1: " + eval);
//        eval /= QA;
        eval *= SCALE;
//        System.out.println("eval2: " + eval);
        eval /= QAB;
//        System.out.println("eval3: " + eval);

        return eval;
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

    @Override
    public void clearHistory() {
        this.accumulator = new Accumulator(Network.L1_SIZE);
        this.accumulatorHistory = new ArrayDeque<>();
    }

}
