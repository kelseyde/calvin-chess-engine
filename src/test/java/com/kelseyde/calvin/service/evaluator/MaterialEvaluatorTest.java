package com.kelseyde.calvin.service.evaluator;

import com.kelseyde.calvin.board.Game;
import com.kelseyde.calvin.evaluation.MaterialEvaluator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MaterialEvaluatorTest {

    private final MaterialEvaluator evaluator = new MaterialEvaluator();

    @Test
    public void testStartingPosition() {

        Game game = new Game();
        int score = evaluator.evaluate(game);
        Assertions.assertEquals(0, score);

    }

}