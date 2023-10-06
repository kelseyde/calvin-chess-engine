package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.material.MaterialEvaluator;
import com.kelseyde.calvin.evaluation.pawnstructure.PawnStructureEvaluator;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementEvaluator;

public class Evaluator {

    private final GamePhaseCalculator gamePhaseCalculator = new GamePhaseCalculator();

    private final MaterialEvaluator materialEvaluator = new MaterialEvaluator();

    private final PiecePlacementEvaluator piecePlacementEvaluator = new PiecePlacementEvaluator();

    private final PawnStructureEvaluator pawnStructureEvaluator = new PawnStructureEvaluator();

    public int evaluate(Board board) {

        int score = 0;

        float gamePhase = gamePhaseCalculator.calculate(board);

        score += materialEvaluator.evaluate(board, gamePhase);

        score += piecePlacementEvaluator.evaluate(board, gamePhase);

        score += pawnStructureEvaluator.evaluate(board, gamePhase);

        return score;

    }

}
