package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Test;

public class EvaluatorTest {

    private final Evaluator evaluator = (Evaluator) TestUtils.EVALUATOR;

    @Test
    public void test() {
        Board board = new Board();
        System.out.println(evaluator.evaluate(board));
    }

}
