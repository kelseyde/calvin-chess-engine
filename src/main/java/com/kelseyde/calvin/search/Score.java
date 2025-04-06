package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.BoardState;
import com.kelseyde.calvin.board.Piece;

public class Score {

    public static final int MAX = 32767;
    public static final int MIN = -MAX;
    public static final int MATE = 32766;
    public static final int DRAW = 0;

    public static boolean isMate(int score) {
        return isDefined(score) && Math.abs(score) >= Score.MATE - Search.MAX_DEPTH;
    }

    public static boolean isDefined(int score) {
        return Math.abs(score) != Score.MAX;
    }

    /**
     * Check for an 'effective' draw, which treats a single repetition of the position as a draw.
     * This is used during {@link Search} to quickly check for a draw; it will lead to some errors in edge cases, but the
     * gamble is that the boost in search speed is worth the potential cost.
     */
    public static boolean isEffectiveDraw(Board board) {
        return isDoubleRepetition(board) || isFiftyMoveRule(board) || isInsufficientMaterial(board);
    }

    public static boolean isThreefoldRepetition(Board board) {

        int repetitionCount = 0;
        long zobrist = board.getState().getKey();
        BoardState[] states = board.getStates();
        for (int i = board.getPly() - 2; i >= 0; i-= 2) {
            final BoardState state = states[i];

            if (state.getKey() == zobrist)
                repetitionCount += 1;

            if (repetitionCount >= 2)
                return true;

            if (state.captured != null || Piece.PAWN == state.moved)
                break;
        }
        return false;

    }

    public static boolean isDoubleRepetition(Board board) {

        long zobrist = board.getState().getKey();
        BoardState[] states = board.getStates();
        for (int i = board.getPly() - 2; i >= 0; i-= 2) {
            final BoardState state = states[i];

            if (state.key == zobrist)
                return true;

            if (state.captured != null || Piece.PAWN == state.moved)
                break;
        }
        return false;

    }

    public static boolean isInsufficientMaterial(Board board) {
        if (board.getPawns() != 0 || board.getRooks() != 0 || board.getQueens() != 0) {
            return false;
        }
        long whitePieces = board.getKnights(true) | board.getBishops(true);
        long blackPieces = board.getKnights(false) |  board.getBishops(false);

        return (Bits.count(whitePieces) == 0 || Bits.count(whitePieces) == 1)
                && (Bits.count(blackPieces) == 0 || Bits.count(blackPieces) == 1);
    }

    public static boolean isFiftyMoveRule(Board board) {
        return board.getState().getHalfMoveClock() >= 100;
    }
}
