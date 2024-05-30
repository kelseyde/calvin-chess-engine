package com.kelseyde.calvin.evaluation.hce;

import com.kelseyde.calvin.engine.EngineConfig;

/**
 * Calculates a tapered evaluation value indicating what game 'phase' we are in. A value of 1 indicates we are in the opening/
 * early middlegame with all the pieces still on the board; a value of 0 indicates there are only kings and pawns remaining.
 * This is used during evaluation to taper the evaluation to reflect the differing values for opening/middlegame and endgame.
 *
 * @see <a href="https://www.chessprogramming.org/Tapered_Eval">Chess Programming Wiki</a>
 */
public class Phase {

    private static final int KNIGHT_PHASE = 10;
    private static final int BISHOP_PHASE = 10;
    private static final int ROOK_PHASE = 20;
    private static final int QUEEN_PHASE = 45;

    public static float fromMaterial(Material whiteMaterial, Material blackMaterial, EngineConfig config) {
        int currentMaterial =
            (whiteMaterial.knights() * KNIGHT_PHASE) +
            (blackMaterial.knights() * KNIGHT_PHASE) +
            (whiteMaterial.bishops() * BISHOP_PHASE) +
            (blackMaterial.bishops() * BISHOP_PHASE) +
            (whiteMaterial.rooks() * ROOK_PHASE) +
            (blackMaterial.rooks() * ROOK_PHASE) +
            (whiteMaterial.queens() * QUEEN_PHASE) +
            (blackMaterial.queens() * QUEEN_PHASE);
        return currentMaterial / config.getTotalPhase();
    }

    public static float fromMaterial(Material material, EngineConfig config) {
        int currentMaterial =
                (material.knights() * KNIGHT_PHASE) +
                (material.bishops() * BISHOP_PHASE) +
                (material.rooks() * ROOK_PHASE) +
                (material.queens() * QUEEN_PHASE);
        return currentMaterial / config.getTotalPhase();
    }

    public static int taperedEval(int middlegameScore, int endgameScore, float phase) {
        return (int) (phase * middlegameScore) + (int) ((1 - phase) * endgameScore);
    }

}
