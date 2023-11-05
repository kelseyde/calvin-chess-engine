package com.kelseyde.calvin.movegeneration.drawcalculator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.NotationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByInsufficientMaterialTest {

    private final ResultCalculator resultEvaluator = new ResultCalculator();

    @Test
    public void testKingVersusKing() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(Piece.KING, true, 28);

        board.toggleSquare(Piece.KING, false, 44);
        board.toggleSquare(Piece.QUEEN, false, 27);
        Assertions.assertFalse(resultEvaluator.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "e4", "d4"));

        // king captures queen -> K vs K
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    @Test
    public void testKingVersusKingBishop() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(Piece.KING, true, 28);
        board.toggleSquare(Piece.BISHOP, true, 25);

        board.toggleSquare(Piece.KING, false, 44);
        board.toggleSquare(Piece.QUEEN, false, 43);
        Assertions.assertFalse(resultEvaluator.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "b4", "d6"));

        // bishop captures queen -> K vs KB
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    @Test
    public void testKingVersusKingKnight() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(Piece.KING, true, 28);
        board.toggleSquare(Piece.KNIGHT, true, 26);

        board.toggleSquare(Piece.KING, false, 44);
        board.toggleSquare(Piece.QUEEN, false, 43);
        Assertions.assertFalse(resultEvaluator.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "c4", "d6"));

        // knight captures queen -> K vs KN
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    @Test
    public void testKingBishopVersusKingBishop() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(Piece.KING, true, 28);
        board.toggleSquare(Piece.BISHOP, true, 25);

        board.toggleSquare(Piece.KING, false, 44);
        board.toggleSquare(Piece.QUEEN, false, 43);
        board.toggleSquare(Piece.BISHOP, false, 52);
        Assertions.assertFalse(resultEvaluator.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "b4", "d6"));

        // bishop captures queen -> KB vs KB
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    @Test
    public void testKingKnightVersusKingKnight() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(Piece.KING, true, 28);
        board.toggleSquare(Piece.KNIGHT, true, 26);

        board.toggleSquare(Piece.KING, false, 44);
        board.toggleSquare(Piece.QUEEN, false, 43);
        board.toggleSquare(Piece.KNIGHT, false, 52);
        Assertions.assertFalse(resultEvaluator.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "c4", "d6"));

        // knight captures queen -> KN vs KN
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    @Test
    public void testKingKnightKnightVersusKingKnightIsNotInsufficientMaterial() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(Piece.KING, true, 28);
        board.toggleSquare(Piece.KNIGHT, true, 26);

        board.toggleSquare(Piece.KING, false, 44);
        board.toggleSquare(Piece.QUEEN, false, 43);
        board.toggleSquare(Piece.KNIGHT, false, 52);
        board.toggleSquare(Piece.KNIGHT, false, 0);
        Assertions.assertFalse(resultEvaluator.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "c4", "d6"));

        // knight captures queen -> KNN vs KN
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertNotEquals(GameResult.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    private Move move(String startSquare, String endSquare) {
        return NotationUtils.fromNotation(startSquare, endSquare);
    }

}
