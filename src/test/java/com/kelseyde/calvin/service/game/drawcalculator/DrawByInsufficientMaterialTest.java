package com.kelseyde.calvin.service.game.drawcalculator;

import com.kelseyde.calvin.model.*;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.model.result.DrawResult;
import com.kelseyde.calvin.model.result.GameResult;
import com.kelseyde.calvin.utils.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByInsufficientMaterialTest {

    @Test
    public void testKingVersusKing() {

        Board board = Board.emptyBoard();
        board.setPiece(28, Piece.getPieceCode(Colour.WHITE, PieceType.KING));

        board.setPiece(44, Piece.getPieceCode(Colour.BLACK, PieceType.KING));
        board.setPiece(27, Piece.getPieceCode(Colour.BLACK, PieceType.QUEEN));

        Game game = new Game(board);
        GameResult result = game.makeMove(move("e4", "d4"));

        // king captures queen -> K vs K
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        DrawResult drawResult = (DrawResult) result;
        Assertions.assertEquals(DrawType.INSUFFICIENT_MATERIAL, drawResult.getDrawType());

    }

    @Test
    public void testKingVersusKingBishop() {

        Board board = Board.emptyBoard();
        board.setPiece(28, Piece.getPieceCode(Colour.WHITE, PieceType.KING));
        board.setPiece(25, Piece.getPieceCode(Colour.WHITE, PieceType.BISHOP));

        board.setPiece(44, Piece.getPieceCode(Colour.BLACK, PieceType.KING));
        board.setPiece(43, Piece.getPieceCode(Colour.BLACK, PieceType.QUEEN));

        Game game = new Game(board);
        GameResult result = game.makeMove(move("b4", "d6"));

        // bishop captures queen -> K vs KB
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        DrawResult drawResult = (DrawResult) result;
        Assertions.assertEquals(DrawType.INSUFFICIENT_MATERIAL, drawResult.getDrawType());

    }

    @Test
    public void testKingVersusKingKnight() {

        Board board = Board.emptyBoard();
        board.setPiece(28, Piece.getPieceCode(Colour.WHITE, PieceType.KING));
        board.setPiece(26, Piece.getPieceCode(Colour.WHITE, PieceType.KNIGHT));

        board.setPiece(44, Piece.getPieceCode(Colour.BLACK, PieceType.KING));
        board.setPiece(43, Piece.getPieceCode(Colour.BLACK, PieceType.QUEEN));

        Game game = new Game(board);
        GameResult result = game.makeMove(move("c4", "d6"));

        // knight captures queen -> K vs KN
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        DrawResult drawResult = (DrawResult) result;
        Assertions.assertEquals(DrawType.INSUFFICIENT_MATERIAL, drawResult.getDrawType());

    }

    @Test
    public void testKingBishopVersusKingBishop() {

        Board board = Board.emptyBoard();
        board.setPiece(28, Piece.getPieceCode(Colour.WHITE, PieceType.KING));
        board.setPiece(25, Piece.getPieceCode(Colour.WHITE, PieceType.BISHOP));

        board.setPiece(44, Piece.getPieceCode(Colour.BLACK, PieceType.KING));
        board.setPiece(43, Piece.getPieceCode(Colour.BLACK, PieceType.QUEEN));
        board.setPiece(52, Piece.getPieceCode(Colour.BLACK, PieceType.BISHOP));

        Game game = new Game(board);
        GameResult result = game.makeMove(move("b4", "d6"));

        // bishop captures queen -> KB vs KB
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        DrawResult drawResult = (DrawResult) result;
        Assertions.assertEquals(DrawType.INSUFFICIENT_MATERIAL, drawResult.getDrawType());

    }

    @Test
    public void testKingKnightVersusKingKnight() {

        Board board = Board.emptyBoard();
        board.setPiece(28, Piece.getPieceCode(Colour.WHITE, PieceType.KING));
        board.setPiece(26, Piece.getPieceCode(Colour.WHITE, PieceType.KNIGHT));

        board.setPiece(44, Piece.getPieceCode(Colour.BLACK, PieceType.KING));
        board.setPiece(43, Piece.getPieceCode(Colour.BLACK, PieceType.QUEEN));
        board.setPiece(52, Piece.getPieceCode(Colour.BLACK, PieceType.KNIGHT));

        Game game = new Game(board);
        GameResult result = game.makeMove(move("c4", "d6"));

        // knight captures queen -> KN vs KN
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        DrawResult drawResult = (DrawResult) result;
        Assertions.assertEquals(DrawType.INSUFFICIENT_MATERIAL, drawResult.getDrawType());

    }

    @Test
    public void testKingKnightKnightVersusKingKnightIsNotInsufficientMaterial() {

        Board board = Board.emptyBoard();
        board.setPiece(28, Piece.getPieceCode(Colour.WHITE, PieceType.KING));
        board.setPiece(26, Piece.getPieceCode(Colour.WHITE, PieceType.KNIGHT));

        board.setPiece(44, Piece.getPieceCode(Colour.BLACK, PieceType.KING));
        board.setPiece(43, Piece.getPieceCode(Colour.BLACK, PieceType.QUEEN));
        board.setPiece(52, Piece.getPieceCode(Colour.BLACK, PieceType.KNIGHT));
        board.setPiece(0, Piece.getPieceCode(Colour.BLACK, PieceType.KNIGHT));

        Game game = new Game(board);
        GameResult result = game.makeMove(move("c4", "d6"));

        // knight captures queen -> KNN vs KN
        Assertions.assertNotEquals(GameResult.ResultType.DRAW, result.getResultType());

    }

    private Move move(String startSquare, String endSquare) {
        return MoveUtils.fromNotation(startSquare, endSquare);
    }

}
