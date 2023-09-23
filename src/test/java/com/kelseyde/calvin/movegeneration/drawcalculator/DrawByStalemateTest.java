package com.kelseyde.calvin.movegeneration.drawcalculator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultEvaluator;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByStalemateTest {
    
    private final ResultEvaluator resultEvaluator = new ResultEvaluator();

    @Test
    public void testSimpleQueenStalemate() {

        Board board = TestUtils.emptyBoard();
        board.setPiece(56, PieceType.KING, false, true);
        board.setPiece(42, PieceType.KING, true, true);
        board.setPiece(1, PieceType.QUEEN, true, true);

        board.makeMove(TestUtils.getLegalMove(board, "b1", "b6"));

        // king stalemated in the corner
        GameResult result = resultEvaluator.getResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_STALEMATE, result);

    }

    @Test
    public void testSimpleKingAndPawnStalemate() {

        Board board = TestUtils.emptyBoard();
        board.setPiece(60, PieceType.KING, false, true);
        board.setPiece(43, PieceType.KING, true, true);
        board.setPiece(52, PieceType.PAWN, true, true);

        board.makeMove(TestUtils.getLegalMove(board, "d6", "e6"));

        // king stalemated by king and pawn
        GameResult result = resultEvaluator.getResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_STALEMATE, result);

    }

    @Test
    public void testSimpleKingAndBishopStalemate() {

        Board board = TestUtils.emptyBoard();
        board.setPiece(63, PieceType.KING, false, true);
        board.setPiece(46, PieceType.KING, true, true);
        board.setPiece(47, PieceType.PAWN, true, true);
        board.setPiece(37, PieceType.BISHOP, true, true);

        board.makeMove(TestUtils.getLegalMove(board, "f5", "e6"));

        // king stalemated in the corner
        GameResult result = resultEvaluator.getResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_STALEMATE, result);

    }

    @Test
    public void testStalemateWithPinnedPawn() {

        Board board = TestUtils.emptyBoard();
        board.setPiece(63, PieceType.KING, false, true);
        board.setPiece(54, PieceType.PAWN, false, true);
        board.setPiece(38, PieceType.KING, true, true);
        board.setPiece(47, PieceType.PAWN, true, true);
        board.setPiece(36, PieceType.BISHOP, true, true);
        board.setPiece(37, PieceType.BISHOP, true, true);
        board.setPiece(9, PieceType.QUEEN, true, true);

        board.makeMove(TestUtils.getLegalMove(board, "b2", "a2"));

        // even though pawn could pseudo-legally capture on h6 with check, it is pinned, therefore stalemate
        GameResult result = resultEvaluator.getResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_STALEMATE, result);

    }

}
