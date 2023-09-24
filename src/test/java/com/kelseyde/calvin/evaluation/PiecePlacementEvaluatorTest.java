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
        /*
        White
        pawns = 5 + 10 + 10 + 0 + 0 + 10 + 10 + 5
        knights = 10 - 40
        bishops = 0 - 10
        rooks = 0
        queen = -5
        king = 20
        total = 50 - 30 - 10 - 5 + 20 = 25


        Black
        pawns = 5 + 10 + 0 + 20 + 20 + 0 + 10 + 5
        knights = 10 + 10
        bishops = 0
        rooks = 0
        queen = 5
        king = 30
            = 70 + 20 + 35 = 125

        diff = -100
        */
        Assertions.assertEquals(-100, evaluator.evaluate(board));
    }

}
