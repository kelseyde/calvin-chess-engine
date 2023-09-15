package com.kelseyde.calvin.service.game.drawcalculator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.DrawType;
import com.kelseyde.calvin.model.Game;
import com.kelseyde.calvin.model.result.DrawResult;
import com.kelseyde.calvin.model.result.GameResult;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByInsufficientMaterialTest {

    @Test
    public void testKingVersusKing() {

        Board board = BoardUtils.emptyBoard();
        board.setPiece(28, new Piece(Colour.WHITE, PieceType.KING));

        board.setPiece(44, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(27, new Piece(Colour.BLACK, PieceType.QUEEN));

        Game game = new Game(board);
        GameResult result = game.playMove(move("e4", "d4"));

        // king captures queen -> K vs K
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        DrawResult drawResult = (DrawResult) result;
        Assertions.assertEquals(DrawType.INSUFFICIENT_MATERIAL, drawResult.getDrawType());

    }

    @Test
    public void testKingVersusKingBishop() {

        Board board = BoardUtils.emptyBoard();
        board.setPiece(28, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(25, new Piece(Colour.WHITE, PieceType.BISHOP));

        board.setPiece(44, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(43, new Piece(Colour.BLACK, PieceType.QUEEN));

        Game game = new Game(board);
        GameResult result = game.playMove(move("b4", "d6"));

        // bishop captures queen -> K vs KB
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        DrawResult drawResult = (DrawResult) result;
        Assertions.assertEquals(DrawType.INSUFFICIENT_MATERIAL, drawResult.getDrawType());

    }

    @Test
    public void testKingVersusKingKnight() {

        Board board = BoardUtils.emptyBoard();
        board.setPiece(28, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(26, new Piece(Colour.WHITE, PieceType.KNIGHT));

        board.setPiece(44, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(43, new Piece(Colour.BLACK, PieceType.QUEEN));

        Game game = new Game(board);
        GameResult result = game.playMove(move("c4", "d6"));

        // knight captures queen -> K vs KN
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        DrawResult drawResult = (DrawResult) result;
        Assertions.assertEquals(DrawType.INSUFFICIENT_MATERIAL, drawResult.getDrawType());

    }

    @Test
    public void testKingBishopVersusKingBishop() {

        Board board = BoardUtils.emptyBoard();
        board.setPiece(28, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(25, new Piece(Colour.WHITE, PieceType.BISHOP));

        board.setPiece(44, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(43, new Piece(Colour.BLACK, PieceType.QUEEN));
        board.setPiece(52, new Piece(Colour.BLACK, PieceType.BISHOP));

        Game game = new Game(board);
        GameResult result = game.playMove(move("b4", "d6"));

        // bishop captures queen -> KB vs KB
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        DrawResult drawResult = (DrawResult) result;
        Assertions.assertEquals(DrawType.INSUFFICIENT_MATERIAL, drawResult.getDrawType());

    }

    @Test
    public void testKingKnightVersusKingKnight() {

        Board board = BoardUtils.emptyBoard();
        board.setPiece(28, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(26, new Piece(Colour.WHITE, PieceType.KNIGHT));

        board.setPiece(44, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(43, new Piece(Colour.BLACK, PieceType.QUEEN));
        board.setPiece(52, new Piece(Colour.BLACK, PieceType.KNIGHT));

        Game game = new Game(board);
        GameResult result = game.playMove(move("c4", "d6"));

        // knight captures queen -> KN vs KN
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        DrawResult drawResult = (DrawResult) result;
        Assertions.assertEquals(DrawType.INSUFFICIENT_MATERIAL, drawResult.getDrawType());

    }

    @Test
    public void testKingKnightKnightVersusKingKnightIsNotInsufficientMaterial() {

        Board board = BoardUtils.emptyBoard();
        board.setPiece(28, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(26, new Piece(Colour.WHITE, PieceType.KNIGHT));

        board.setPiece(44, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(43, new Piece(Colour.BLACK, PieceType.QUEEN));
        board.setPiece(52, new Piece(Colour.BLACK, PieceType.KNIGHT));
        board.setPiece(0, new Piece(Colour.BLACK, PieceType.KNIGHT));

        Game game = new Game(board);
        GameResult result = game.playMove(move("c4", "d6"));

        // knight captures queen -> KNN vs KN
        Assertions.assertNotEquals(GameResult.ResultType.DRAW, result.getResultType());

    }

    private Move move(String startSquare, String endSquare) {
        return MoveUtils.fromNotation(startSquare, endSquare);
    }

}
