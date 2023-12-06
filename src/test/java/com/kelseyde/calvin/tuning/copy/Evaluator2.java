package com.kelseyde.calvin.tuning.copy;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.evaluation.score.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Evaluates the current board position. Uses various heuristics to calculate a numeric value, in centipawns, estimating
 * how good the current position is for the side to move. This means that a positive score indicates the position is better
 * for the side to move, regardless of whether they are white or black.
 * <p>
 * Also includes logic for incrementally updating the evaluation during make/unmake move, which saves some time during the
 * search procedure.
 * @see <a href="https://www.chessprogramming.org/Evaluation">Chess Programming Wiki</a>
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Evaluator2 implements Evaluation {

    final EngineConfig config;
    Score score;

    public Evaluator2(EngineConfig config) {
        this.config = config;
    }

    @Override
    public int evaluate(Board board) {

        Material whiteMaterial = Material.fromBoard(board, true);
        Material blackMaterial = Material.fromBoard(board, false);

        float phase = Phase.fromMaterial(whiteMaterial, blackMaterial);

        int whiteMaterialScore = whiteMaterial.sum(config, phase);
        int blackMaterialScore = blackMaterial.sum(config, phase);

        int whitePiecePlacementScore = PiecePlacement.score(config, board, phase, true);
        int blackPiecePlacementScore = PiecePlacement.score(config, board, phase, false);

        int whiteMobilityScore = Mobility.score(config, board, true, phase);
        int blackMobilityScore = Mobility.score(config, board, false, phase);

        int whitePawnStructureScore = PawnEvaluation.score(config, board.getPawns(true), board.getPawns(false), phase, true);
        int blackPawnStructureScore = PawnEvaluation.score(config, board.getPawns(false), board.getPawns(true), phase, false);

        int whiteKingSafetyScore = KingSafety.score(config, board, blackMaterial, phase, true);
        int blackKingSafetyScore = KingSafety.score(config, board, whiteMaterial, phase, false);

        int whiteRookScore = RookEvaluation.score(config, board, phase, true);
        int blackRookScore = RookEvaluation.score(config, board, phase, false);

        int whiteMopUpScore = MopUp.score(config, board, whiteMaterialScore, blackMaterialScore, phase, true);
        int blackMopUpScore = MopUp.score(config, board, whiteMaterialScore, blackMaterialScore, phase, false);

        int whiteTempoBonus = board.isWhiteToMove() ? config.getTempoBonus() : 0;
        int blackTempoBonus = board.isWhiteToMove() ? 0 : config.getTempoBonus();

        score = Score.builder()
                .whiteMaterial(whiteMaterial)
                .whiteMaterialScore(whiteMaterialScore)
                .whitePiecePlacementScore(whitePiecePlacementScore)
                .whiteMobilityScore(whiteMobilityScore)
                .whitePawnStructureScore(whitePawnStructureScore)
                .whiteKingSafetyScore(whiteKingSafetyScore)
                .whiteRookScore(whiteRookScore)
                .whiteMopUpScore(whiteMopUpScore)
                .blackMaterial(blackMaterial)
                .blackMaterialScore(blackMaterialScore)
                .blackPiecePlacementScore(blackPiecePlacementScore)
                .blackMobilityScore(blackMobilityScore)
                .blackPawnStructureScore(blackPawnStructureScore)
                .blackKingSafetyScore(blackKingSafetyScore)
                .blackRookScore(blackRookScore)
                .blackMopUpScore(blackMopUpScore)
                .whiteTempoBonus(whiteTempoBonus)
                .blackTempoBonus(blackTempoBonus)
                .build();

        return score.sum(board.isWhiteToMove());

    }

    public Material getMaterial(boolean isWhite) {
        return isWhite ? score.getWhiteMaterial() : score.getBlackMaterial();
    }

}
