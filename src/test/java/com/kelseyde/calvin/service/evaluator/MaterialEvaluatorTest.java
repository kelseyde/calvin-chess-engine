package com.kelseyde.calvin.service.evaluator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.MaterialEvaluator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MaterialEvaluatorTest {

    private final MaterialEvaluator evaluator = new MaterialEvaluator();

    @Test
    public void testStartingPosition() {

        Board board = new Board();
        int score = evaluator.evaluate(board);
        Assertions.assertEquals(0, score);

    }

}