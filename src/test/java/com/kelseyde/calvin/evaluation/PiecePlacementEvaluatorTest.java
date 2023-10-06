package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementEvaluator;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PiecePlacementEvaluatorTest {

    private final PiecePlacementEvaluator evaluator = new PiecePlacementEvaluator();
    private final GamePhaseCalculator gamePhaseCalculator = new GamePhaseCalculator();

    @Test
    public void testStartingPosition() {
        Board board = new Board();
        Assertions.assertEquals(0, evaluator.evaluate(board, 1));
    }

    @Test
    public void bigPawnCentreGivesAdvantageToBlack() {

        Board board = FEN.fromFEN("r1b2rk1/pp4pp/1qnb1n2/2pppp2/8/3PPN2/PPP1BPPP/RNBQ1R1K b - - 7 10");
        Assertions.assertEquals(75, evaluator.evaluate(board, 1));

        board.setWhiteToMove(true);
        Assertions.assertEquals(-75, evaluator.evaluate(board, 1));

    }

    @Test
    public void testKingActivityInEndgame() {

        String fen = "8/p7/8/8/8/3k4/P7/7K w - - 0 1";
        Board board = FEN.fromFEN(fen);

        float gamePhase = gamePhaseCalculator.calculate(board);
        Assertions.assertEquals(-80, evaluator.evaluate(board, gamePhase));

    }

    @Test
    public void testPawnActivityInEndgame() {

        String fen = "7k/8/8/8/1p6/p6P/6P1/7K w - - 0 1";

        Board board = FEN.fromFEN(fen);

        float gamePhase = gamePhaseCalculator.calculate(board);
        Assertions.assertEquals(-60, evaluator.evaluate(board, gamePhase));

    }

}
