package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.score.Phase;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Test;

public class EvaluatorTest {

    private final Evaluator evaluator = new Evaluator(TestUtils.PRD_CONFIG);
    private final EvaluatorCopy evaluatorCopy = new EvaluatorCopy(TestUtils.PRD_CONFIG);

    @Test
    public void test() {

        String fen = "r2k3r/p1pp1pb1/Bn2pqp1/3PN3/4P3/1pN4p/PPPB1PPP/R2K3R w - - 0 4";
        Board board = FEN.toBoard(fen);
        int mg = 107 + 18;
        int eg = 345 + 23;
        System.out.printf("%s, %s", Phase.taperedEval(mg, eg, 0.6f), Phase.taperedEval(107, 345, 0.6f) + Phase.taperedEval(18, 23, 0.6f));


    }

}
