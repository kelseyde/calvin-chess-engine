package com.kelseyde.calvin.movegeneration.drawcalculator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByStalemateTest {
    
    private final ResultCalculator resultEvaluator = new ResultCalculator();

    @Test
    public void testSimpleQueenStalemate() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(PieceType.KING, false, 56);
        board.toggleSquare(PieceType.KING, true, 42);
        board.toggleSquare(PieceType.QUEEN, true, 1);
        Assertions.assertFalse(resultEvaluator.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "b1", "b6"));

        // king stalemated in the corner
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_STALEMATE, result);

    }

    @Test
    public void testSimpleKingAndPawnStalemate() {

        Board board = FEN.fromFEN("4k3/4P3/3K4/8/8/8/8/8 w - - 0 1");
        Assertions.assertFalse(resultEvaluator.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "d6", "e6"));

        // king stalemated by king and pawn
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_STALEMATE, result);

    }

    @Test
    public void testSimpleKingAndBishopStalemate() {

        Board board = FEN.fromFEN("7k/8/6KP/5B2/8/8/8/8 w - - 0 1");

        board.makeMove(TestUtils.getLegalMove(board, "f5", "e6"));

        // king stalemated in the corner
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_STALEMATE, result);

    }

    @Test
    public void testStalemateWithPinnedPawn() {

        Board board = FEN.fromFEN("7k/6p1/7P/4BBK1/8/8/1Q6/8 w - - 0 1");
        Assertions.assertFalse(resultEvaluator.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "b2", "a2"));

        // even though pawn could pseudo-legally capture on h6 with check, it is pinned, therefore stalemate
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_STALEMATE, result);

    }

}
