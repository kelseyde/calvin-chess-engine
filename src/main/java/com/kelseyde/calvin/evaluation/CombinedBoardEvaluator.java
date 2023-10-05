package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.material.MaterialEvaluator;
import com.kelseyde.calvin.evaluation.pawnstructure.PawnStructureEvaluator;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementEvaluator;

import java.util.List;

public class CombinedBoardEvaluator implements BoardEvaluator {

    private final List<BoardEvaluator> boardEvaluators = List.of(
            new MaterialEvaluator(),
            new PiecePlacementEvaluator(),
            new PawnStructureEvaluator()
    );

    @Override
    public int evaluate(Board board) {
        return boardEvaluators.stream()
                .map(evaluator -> evaluator.evaluate(board))
                .reduce(0, Integer::sum);
    }

}
