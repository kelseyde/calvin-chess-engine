package com.kelseyde.calvin.generation.drawcalculator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.evaluation.Arbiter;
import com.kelseyde.calvin.evaluation.result.Result;
import com.kelseyde.calvin.evaluation.result.ResultCalculator;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.Notation;
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
        Assertions.assertFalse(Arbiter.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "e4", "d4"));

        // king captures queen -> K vs K
        Result result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(Result.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    @Test
    public void testKingVersusKingBishop() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(Piece.KING, true, 28);
        board.toggleSquare(Piece.BISHOP, true, 25);

        board.toggleSquare(Piece.KING, false, 44);
        board.toggleSquare(Piece.QUEEN, false, 43);
        Assertions.assertFalse(Arbiter.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "b4", "d6"));

        // bishop captures queen -> K vs KB
        Result result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(Result.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    @Test
    public void testKingVersusKingKnight() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(Piece.KING, true, 28);
        board.toggleSquare(Piece.KNIGHT, true, 26);

        board.toggleSquare(Piece.KING, false, 44);
        board.toggleSquare(Piece.QUEEN, false, 43);
        Assertions.assertFalse(Arbiter.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "c4", "d6"));

        // knight captures queen -> K vs KN
        Result result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(Result.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    @Test
    public void testKingBishopVersusKingBishop() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(Piece.KING, true, 28);
        board.toggleSquare(Piece.BISHOP, true, 25);

        board.toggleSquare(Piece.KING, false, 44);
        board.toggleSquare(Piece.QUEEN, false, 43);
        board.toggleSquare(Piece.BISHOP, false, 52);
        Assertions.assertFalse(Arbiter.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "b4", "d6"));

        // bishop captures queen -> KB vs KB
        Result result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(Result.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    @Test
    public void testKingKnightVersusKingKnight() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(Piece.KING, true, 28);
        board.toggleSquare(Piece.KNIGHT, true, 26);

        board.toggleSquare(Piece.KING, false, 44);
        board.toggleSquare(Piece.QUEEN, false, 43);
        board.toggleSquare(Piece.KNIGHT, false, 52);
        Assertions.assertFalse(Arbiter.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "c4", "d6"));

        // knight captures queen -> KN vs KN
        Result result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(Result.DRAW_BY_INSUFFICIENT_MATERIAL, result);

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
        Assertions.assertFalse(Arbiter.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "c4", "d6"));

        // knight captures queen -> KNN vs KN
        Result result = resultEvaluator.calculateResult(board);
        Assertions.assertNotEquals(Result.DRAW_BY_INSUFFICIENT_MATERIAL, result);

    }

    private Move move(String startSquare, String endSquare) {
        return Notation.fromNotation(startSquare, endSquare);
    }

}
