package com.kelseyde.calvin.movegeneration.drawcalculator;

import com.kelseyde.calvin.board.*;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.Piece;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.board.result.DrawResult;
import com.kelseyde.calvin.board.result.DrawType;
import com.kelseyde.calvin.board.result.GameResult;
import com.kelseyde.calvin.utils.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByInsufficientMaterialTest {

    @Test
    public void testKingVersusKing() {

        Board board = Board.emptyBoard();
        board.setPiece(28, Piece.getPieceCode(true, PieceType.KING));

        board.setPiece(44, Piece.getPieceCode(false, PieceType.KING));
        board.setPiece(27, Piece.getPieceCode(false, PieceType.QUEEN));

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
        board.setPiece(28, Piece.getPieceCode(true, PieceType.KING));
        board.setPiece(25, Piece.getPieceCode(true, PieceType.BISHOP));

        board.setPiece(44, Piece.getPieceCode(false, PieceType.KING));
        board.setPiece(43, Piece.getPieceCode(false, PieceType.QUEEN));

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
        board.setPiece(28, Piece.getPieceCode(true, PieceType.KING));
        board.setPiece(26, Piece.getPieceCode(true, PieceType.KNIGHT));

        board.setPiece(44, Piece.getPieceCode(false, PieceType.KING));
        board.setPiece(43, Piece.getPieceCode(false, PieceType.QUEEN));

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
        board.setPiece(28, Piece.getPieceCode(true, PieceType.KING));
        board.setPiece(25, Piece.getPieceCode(true, PieceType.BISHOP));

        board.setPiece(44, Piece.getPieceCode(false, PieceType.KING));
        board.setPiece(43, Piece.getPieceCode(false, PieceType.QUEEN));
        board.setPiece(52, Piece.getPieceCode(false, PieceType.BISHOP));

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
        board.setPiece(28, Piece.getPieceCode(true, PieceType.KING));
        board.setPiece(26, Piece.getPieceCode(true, PieceType.KNIGHT));

        board.setPiece(44, Piece.getPieceCode(false, PieceType.KING));
        board.setPiece(43, Piece.getPieceCode(false, PieceType.QUEEN));
        board.setPiece(52, Piece.getPieceCode(false, PieceType.KNIGHT));

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
        board.setPiece(28, Piece.getPieceCode(true, PieceType.KING));
        board.setPiece(26, Piece.getPieceCode(true, PieceType.KNIGHT));

        board.setPiece(44, Piece.getPieceCode(false, PieceType.KING));
        board.setPiece(43, Piece.getPieceCode(false, PieceType.QUEEN));
        board.setPiece(52, Piece.getPieceCode(false, PieceType.KNIGHT));
        board.setPiece(0, Piece.getPieceCode(false, PieceType.KNIGHT));

        Game game = new Game(board);
        GameResult result = game.makeMove(move("c4", "d6"));

        // knight captures queen -> KNN vs KN
        Assertions.assertNotEquals(GameResult.ResultType.DRAW, result.getResultType());

    }

    private Move move(String startSquare, String endSquare) {
        return MoveUtils.fromNotation(startSquare, endSquare);
    }

}
