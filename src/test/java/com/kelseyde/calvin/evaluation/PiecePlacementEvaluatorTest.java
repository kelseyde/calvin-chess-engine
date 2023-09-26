package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementEvaluator;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PiecePlacementEvaluatorTest {

    private final PiecePlacementEvaluator evaluator = new PiecePlacementEvaluator();

    @Test
    public void testStartingPosition() {
        Board board = new Board();
        Assertions.assertEquals(0, evaluator.evaluate(board));
    }

    @Test
    public void bigPawnCentreGivesAdvantageToBlack() {

        Board board = FEN.fromFEN("r1b2rk1/pp4pp/1qnb1n2/2pppp2/8/3PPN2/PPP1BPPP/RNBQ1R1K b - - 7 10");
        Assertions.assertEquals(-100, evaluator.evaluate(board));
    }

}
