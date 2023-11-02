package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.material.MaterialEvaluator;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementEvaluator;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PiecePlacementEvaluatorTest {

    private final PiecePlacementEvaluator evaluator = new PiecePlacementEvaluator();
    private final MaterialEvaluator materialCalculator = new MaterialEvaluator();

    @Test
    public void testStartingPosition() {
        Board board = new Board();
        Assertions.assertEquals(0, score(board, 1));
    }

    @Test
    public void bigPawnCentreGivesAdvantageToBlack() {

        Board board = FEN.fromFEN("r1b2rk1/pp4pp/1qnb1n2/2pppp2/8/3PPN2/PPP1BPPP/RNBQ1R1K b - - 7 10");
        Assertions.assertEquals(76, score(board, 1));

        board.setWhiteToMove(true);
        Assertions.assertEquals(-76, score(board, 1));

    }

    @Test
    public void testKingActivityInEndgame() {

        String fen = "8/p7/8/8/8/3k4/P7/7K w - - 0 1";
        Board board = FEN.fromFEN(fen);

        float gamePhase = phase(board);
        Assertions.assertEquals(-58, score(board, gamePhase));

    }

    @Test
    public void testPawnActivityInEndgame() {

        String fen = "7k/8/8/8/1p6/p6P/6P1/7K w - - 0 1";

        Board board = FEN.fromFEN(fen);

        float gamePhase = phase(board);
        Assertions.assertEquals(-124, score(board, gamePhase));

    }

    @Test
    public void testEvaluationIsSymmetrical() {

        String ruyLopezFen = "r1bqkb1r/1ppp1ppp/p1n2n2/4p3/B3P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 1";
        String ruyLopezReverseFen = "rnbqk2r/pppp1ppp/5n2/b3p3/4P3/P1N2N2/1PPP1PPP/R1BQKB1R b KQkq - 0 1";

        Board board1 = FEN.fromFEN(ruyLopezFen);
        float gamePhase1 = phase(board1);
        int score1 = score(board1, gamePhase1);

        Board board2 = FEN.fromFEN(ruyLopezFen);
        float gamePhase2 = phase(board2);
        int score2 = score(board2, gamePhase2);

        Assertions.assertEquals(score1, score2);

    }

    private int score(Board board, float gamePhase) {
        int modifier = board.isWhiteToMove() ? 1 : -1;
        return modifier * (evaluator.evaluate(board, gamePhase, true).sum() - evaluator.evaluate(board, gamePhase, false).sum());
    }

    private float phase(Board board) {
        return (materialCalculator.evaluate(board, true).phase() + materialCalculator.evaluate(board, false).phase()) / 2;
    }

}
