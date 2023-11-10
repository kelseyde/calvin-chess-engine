package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.Bitwise;

public class BishopEvaluation {

    private static final int BISHOP_PAIR_MG_BONUS = 22;
    private static final int BISHOP_PAIR_EG_BONUS = 65;

    public static int score(Board board, float phase, boolean isWhite) {
        int score = 0;
        score += Mobility.bishopScore(board, isWhite, phase);
        long bishops = board.getBishops(isWhite);
        if (Bitwise.countBits(bishops) == 2) {
            score += GamePhase.taperedEval(BISHOP_PAIR_MG_BONUS, BISHOP_PAIR_EG_BONUS, phase);
        }
        return score;
    }

}
