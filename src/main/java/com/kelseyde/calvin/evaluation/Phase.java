package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

/**
 * Calculates a tapered evaluation value indicating what game 'phase' we are in. A value of 1 indicates we are in the opening/
 * early middlegame with all the pieces still on the board; a value of 0 indicates there are only kings and pawns remaining.
 * This is used during evaluation to taper the evaluation to reflect the differing values for opening/middlegame and endgame.
 *
 * @see <a href="https://www.chessprogramming.org/Tapered_Eval">Chess Programming Wiki</a>
 */
public class Phase {

    public static float fromMaterial(Material whiteMaterial, Material blackMaterial, EngineConfig config) {
        int knightPhase = config.getPiecePhases()[Piece.KNIGHT.getIndex()];
        int bishopPhase = config.getPiecePhases()[Piece.BISHOP.getIndex()];
        int rookPhase = config.getPiecePhases()[Piece.ROOK.getIndex()];
        int queenPhase = config.getPiecePhases()[Piece.QUEEN.getIndex()];
        int currentMaterial =
            (whiteMaterial.knights() * knightPhase) +
            (blackMaterial.knights() * knightPhase) +
            (whiteMaterial.bishops() * bishopPhase) +
            (blackMaterial.bishops() * bishopPhase) +
            (whiteMaterial.rooks() * rookPhase) +
            (blackMaterial.rooks() * rookPhase) +
            (whiteMaterial.queens() * queenPhase) +
            (blackMaterial.queens() * queenPhase);
        return currentMaterial / config.getTotalPhase();
    }

    public static float fromMaterial(Material material, EngineConfig config) {
        int knightPhase = config.getPiecePhases()[Piece.KNIGHT.getIndex()];
        int bishopPhase = config.getPiecePhases()[Piece.BISHOP.getIndex()];
        int rookPhase = config.getPiecePhases()[Piece.ROOK.getIndex()];
        int queenPhase = config.getPiecePhases()[Piece.QUEEN.getIndex()];
        int currentMaterial =
                (material.knights() * knightPhase) +
                (material.bishops() * bishopPhase) +
                (material.rooks() * rookPhase) +
                (material.queens() * queenPhase);
        return currentMaterial / config.getTotalPhase();
    }

    public static int taperedEval(int middlegameScore, int endgameScore, float phase) {
        return (int) (phase * middlegameScore) + (int) ((1 - phase) * endgameScore);
    }

}
