package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.material.PieceValues;

/**
 * Returns a float indicating the phase of the game - a tapered value starting at 1 (opening) and ending at 0 (endgame).
 * Calculated based on the ratio of remaining material value compared to the starting material value.
 */
public class GamePhaseCalculator {

    private static final int OPENING_MATERIAL =
            (PieceValues.KNIGHT * 4) + (PieceValues.BISHOP * 4) + (PieceValues.ROOK * 4) + (PieceValues.QUEEN * 2);

    public float calculate(Board board) {
        int currentMaterial =
                (PieceValues.KNIGHT * knightsCount(board)) +
                (PieceValues.BISHOP * bishopsCount(board)) +
                (PieceValues.ROOK * rooksCount(board)) +
                (PieceValues.QUEEN * queensCount(board));

        return 0 + (float) currentMaterial / OPENING_MATERIAL;
    }

    private int knightsCount(Board board) {
        return Long.bitCount(board.getWhiteKnights() | board.getBlackKnights());
    }

    private int bishopsCount(Board board) {
        return Long.bitCount(board.getWhiteBishops() | board.getBlackBishops());
    }

    private int rooksCount(Board board) {
        return Long.bitCount(board.getWhiteRooks() | board.getBlackRooks());
    }

    private int queensCount(Board board) {
        return Long.bitCount(board.getWhiteQueens() | board.getBlackQueens());
    }

}
