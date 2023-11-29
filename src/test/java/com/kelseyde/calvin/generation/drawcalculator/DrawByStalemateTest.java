package com.kelseyde.calvin.generation.drawcalculator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.evaluation.Arbiter;
import com.kelseyde.calvin.evaluation.result.Result;
import com.kelseyde.calvin.evaluation.result.ResultCalculator;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByStalemateTest {
    
    private final ResultCalculator resultEvaluator = new ResultCalculator();

    @Test
    public void testSimpleQueenStalemate() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(Piece.KING, false, 56);
        board.toggleSquare(Piece.KING, true, 42);
        board.toggleSquare(Piece.QUEEN, true, 1);
        Assertions.assertFalse(Arbiter.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "b1", "b6"));

        // king stalemated in the corner
        Result result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(Result.DRAW_BY_STALEMATE, result);

    }

    @Test
    public void testSimpleKingAndPawnStalemate() {

        Board board = FEN.toBoard("4k3/4P3/3K4/8/8/8/8/8 w - - 0 1");
        Assertions.assertFalse(Arbiter.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "d6", "e6"));

        // king stalemated by king and pawn
        Result result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(Result.DRAW_BY_STALEMATE, result);

    }

    @Test
    public void testSimpleKingAndBishopStalemate() {

        Board board = FEN.toBoard("7k/8/6KP/5B2/8/8/8/8 w - - 0 1");

        board.makeMove(TestUtils.getLegalMove(board, "f5", "e6"));

        // king stalemated in the corner
        Result result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(Result.DRAW_BY_STALEMATE, result);

    }

    @Test
    public void testStalemateWithPinnedPawn() {

        Board board = FEN.toBoard("7k/6p1/7P/4BBK1/8/8/1Q6/8 w - - 0 1");
        Assertions.assertFalse(Arbiter.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "b2", "a2"));

        // even though pawn could pseudo-legally capture on h6 with check, it is pinned, therefore stalemate
        Result result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(Result.DRAW_BY_STALEMATE, result);

    }

}
