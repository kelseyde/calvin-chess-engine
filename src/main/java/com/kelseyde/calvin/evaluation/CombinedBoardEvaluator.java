package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.material.MaterialEvaluator;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementEvaluator;

import java.util.List;

public class CombinedBoardEvaluator implements BoardEvaluator {

    private final List<BoardEvaluator> boardEvaluators = List.of(
            new MaterialEvaluator(),
            new PiecePlacementEvaluator()
    );

    @Override
    public int evaluate(Board board) {
        return 0;
    }
}
