package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.engine.EngineConfig;

/**
 * Calculates a tapered evaluation value indicating what game 'phase' we are in. A value of 1 indicates we are in the opening/
 * early middlegame with all the pieces still on the board; a value of 0 indicates there are only kings and pawns remaining.
 * This is used during evaluation to taper the evaluation to reflect the differing values for opening/middlegame and endgame.
 *
 * @see <a href="https://www.chessprogramming.org/Tapered_Eval">Chess Programming Wiki</a>
 */
public class Phase {

    public static final int KNIGHT_PHASE = 10;
    public static final int BISHOP_PHASE = 10;
    public static final int ROOK_PHASE = 20;
    public static final int QUEEN_PHASE = 45;

    public static float calculatePhase(EngineConfig config, int knights, int bishops, int rooks, int queens) {
        return ((knights * KNIGHT_PHASE) +
                (bishops * BISHOP_PHASE) +
                (rooks * ROOK_PHASE) +
                (queens * QUEEN_PHASE))
                / config.getTotalPhase();
    }

    public static int taperedEval(int middlegameScore, int endgameScore, float phase) {
        return (int) (phase * middlegameScore) + (int) ((1 - phase) * endgameScore);
    }

}
