package com.kelseyde.calvin.search.moveordering;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.search.moveordering.tables.HistoryTable;
import com.kelseyde.calvin.search.moveordering.tables.KillerTable;

public interface MovePicker {

    Move pickNextMove();

    default int scoreMove(Board board, KillerTable killerTable, HistoryTable historyTable, Move move, Move ttMove, int ply) {

        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();
        int moveScore = 0;

        // The previous best move from the transposition table is searched first.
        if (move.equals(ttMove)) {
            moveScore += MoveBonus.TT_MOVE_BIAS;
        }

        // Then any pawn promotions
        Piece promotionPiece = move.getPromotionPiece();
        if (promotionPiece != null) {
            moveScore += scorePromotion(promotionPiece);
        }

        // Then captures, sorted by MVV-LVA
        Piece capturedPiece = board.pieceAt(endSquare);
        boolean isCapture = capturedPiece != null;
        if (isCapture) {
            moveScore += scoreCapture(board, startSquare, capturedPiece);
        }
        // Non-captures are sorted using killer score + history score
        else {
            int killerScore = killerTable.getScore(move, ply);
            int historyScore = historyTable.getScore(board, startSquare, endSquare, killerScore);
            moveScore += killerScore + historyScore;
        }

        if (move.isCastling()) {
            moveScore += MoveBonus.CASTLING_BIAS;
        }

        return moveScore;

    }

    default int scorePromotion(Piece promotionPiece) {
        return Piece.QUEEN == promotionPiece ? MoveBonus.QUEEN_PROMOTION_BIAS : MoveBonus.UNDER_PROMOTION_BIAS;
    }

    default int scoreCapture(Board board, int startSquare, Piece capturedPiece) {
        Piece piece = board.pieceAt(startSquare);
        int captureScore = 0;
        captureScore += MvvLva.MVV_LVA_TABLE[capturedPiece.getIndex()][piece.getIndex()];
        int materialDelta = capturedPiece.getValue() - piece.getValue();
        if (materialDelta > 0) {
            captureScore += MoveBonus.WINNING_CAPTURE_BIAS;
        } else if (materialDelta == 0) {
            captureScore += MoveBonus.EQUAL_CAPTURE_BIAS;
        } else {
            captureScore += MoveBonus.LOSING_CAPTURE_BIAS;
        }
        return captureScore;
    }

}
