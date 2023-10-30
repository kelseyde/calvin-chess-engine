package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class EvaluatorSandboxTest {

    @Test
    public void testSandbox() {

        Evaluator evaluator1 = new Evaluator(FEN.fromFEN("rnb1kb1r/ppB1pppp/5n2/8/P2pp3/2P5/4NPPP/R2QK2R b KQkq - 0 12"));

        Evaluator evaluator2 = new Evaluator(FEN.fromFEN("Nnbk1b1r/pp2pppp/5n2/8/P1qppB2/2P5/4NPPP/R2QK2R b KQ - 0 12"));

        int eval1 = evaluator1.get();
        int eval2 = evaluator2.get();

        Assertions.assertTrue(eval2 > eval1);

    }

}
