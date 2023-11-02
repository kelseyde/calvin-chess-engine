package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.evaluation.material.Material;

public class GamePhase {

    private static final int KNIGHT_PHASE = 1;
    private static final int BISHOP_PHASE = 1;
    private static final int ROOK_PHASE = 2;
    private static final int QUEEN_PHASE = 4;
    private static final float TOTAL_PHASE = (KNIGHT_PHASE * 4) + (BISHOP_PHASE * 4) + (ROOK_PHASE * 4) + (QUEEN_PHASE * 2);

    public static float fromMaterial(Material whiteMaterial, Material blackMaterial) {
        float phase = TOTAL_PHASE;
        phase -= (whiteMaterial.knights() * KNIGHT_PHASE);
        phase -= (blackMaterial.knights() * KNIGHT_PHASE);
        phase -= (whiteMaterial.bishops() * BISHOP_PHASE);
        phase -= (blackMaterial.bishops() * BISHOP_PHASE);
        phase -= (whiteMaterial.rooks() * ROOK_PHASE);
        phase -= (blackMaterial.rooks() * ROOK_PHASE);
        phase -= (whiteMaterial.queens() * QUEEN_PHASE);
        phase -= (blackMaterial.queens() * QUEEN_PHASE);
        return (phase * 256 + (TOTAL_PHASE / 2)) / TOTAL_PHASE;
    }

    public static int taperedEval(int middlegameScore, int endgameScore, float phase) {
        return (int) ((middlegameScore * (256 - phase)) + (endgameScore * phase)) / 256;
    }

}
