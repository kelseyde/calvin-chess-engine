package com.kelseyde.calvin.movegeneration.drawcalculator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByInsufficientMaterialTest {

    private final ResultCalculator resultEvaluator = new ResultCalculator();

    @Test
    public void testKingVersusKing() {

        Board board = TestUtils.emptyBoard();
        board.setPiece(28, PieceType.KING, true, true);

        board.setPiece(44, PieceType.KING, false, true);
        board.setPiece(27, PieceType.QUEEN, false, true);

        board.makeMove(TestUtils.getLegalMove(board, "e4", "d4"));

        // king captures queen -> K vs K
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    @Test
    public void testKingVersusKingBishop() {

        Board board = TestUtils.emptyBoard();
        board.setPiece(28, PieceType.KING, true, true);
        board.setPiece(25, PieceType.BISHOP, true, true);

        board.setPiece(44, PieceType.KING, false, true);
        board.setPiece(43, PieceType.QUEEN, false, true);

        board.makeMove(TestUtils.getLegalMove(board, "b4", "d6"));

        // bishop captures queen -> K vs KB
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    @Test
    public void testKingVersusKingKnight() {

        Board board = TestUtils.emptyBoard();
        board.setPiece(28, PieceType.KING, true, true);
        board.setPiece(26, PieceType.KNIGHT, true, true);

        board.setPiece(44, PieceType.KING, false, true);
        board.setPiece(43, PieceType.QUEEN, false, true);

        board.makeMove(TestUtils.getLegalMove(board, "c4", "d6"));

        // knight captures queen -> K vs KN
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    @Test
    public void testKingBishopVersusKingBishop() {

        Board board = TestUtils.emptyBoard();
        board.setPiece(28, PieceType.KING, true, true);
        board.setPiece(25, PieceType.BISHOP, true, true);

        board.setPiece(44, PieceType.KING, false, true);
        board.setPiece(43, PieceType.QUEEN, false, true);
        board.setPiece(52, PieceType.BISHOP, false, true);

        board.makeMove(TestUtils.getLegalMove(board, "b4", "d6"));

        // bishop captures queen -> KB vs KB
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    @Test
    public void testKingKnightVersusKingKnight() {

        Board board = TestUtils.emptyBoard();
        board.setPiece(28, PieceType.KING, true, true);
        board.setPiece(26, PieceType.KNIGHT, true, true);

        board.setPiece(44, PieceType.KING, false, true);
        board.setPiece(43, PieceType.QUEEN, false, true);
        board.setPiece(52, PieceType.KNIGHT, false, true);

        board.makeMove(TestUtils.getLegalMove(board, "c4", "d6"));

        // knight captures queen -> KN vs KN
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    @Test
    public void testKingKnightKnightVersusKingKnightIsNotInsufficientMaterial() {

        Board board = TestUtils.emptyBoard();
        board.setPiece(28, PieceType.KING, true, true);
        board.setPiece(26, PieceType.KNIGHT, true, true);

        board.setPiece(44, PieceType.KING, false, true);
        board.setPiece(43, PieceType.QUEEN, false, true);
        board.setPiece(52, PieceType.KNIGHT, false, true);
        board.setPiece(0, PieceType.KNIGHT, false, true);

        board.makeMove(TestUtils.getLegalMove(board, "c4", "d6"));

        // knight captures queen -> KNN vs KN
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertNotEquals(GameResult.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    private Move move(String startSquare, String endSquare) {
        return NotationUtils.fromNotation(startSquare, endSquare);
    }

}
