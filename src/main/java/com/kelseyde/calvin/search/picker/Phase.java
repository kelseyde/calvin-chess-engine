package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Board;

public class Phase {

    private static final int KNIGHT_PHASE = 10;
    private static final int BISHOP_PHASE = 10;
    private static final int ROOK_PHASE = 20;
    private static final int QUEEN_PHASE = 45;
    private static final int TOTAL_PHASE = (KNIGHT_PHASE * 4) + (BISHOP_PHASE * 4) + (ROOK_PHASE * 4) + (QUEEN_PHASE * 2);

    public static float fromBoard(Board board) {
        int currentMaterial =
                (Bits.count(board.getKnights()) * KNIGHT_PHASE) +
                        (Bits.count(board.getBishops()) * BISHOP_PHASE) +
                        (Bits.count(board.getRooks()) * ROOK_PHASE) +
                        (Bits.count(board.getQueens()) * QUEEN_PHASE);
        return (float) currentMaterial / TOTAL_PHASE;
    }

    public static int taperedEval(int middlegameScore, int endgameScore, float phase) {
        return (int) (phase * middlegameScore) + (int) ((1 - phase) * endgameScore);
    }

}
