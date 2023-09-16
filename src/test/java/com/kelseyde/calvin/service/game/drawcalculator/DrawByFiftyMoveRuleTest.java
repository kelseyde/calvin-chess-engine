package com.kelseyde.calvin.service.game.drawcalculator;

import com.kelseyde.calvin.model.*;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.model.result.DrawResult;
import com.kelseyde.calvin.model.result.GameResult;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByFiftyMoveRuleTest {

    @Test
    public void testDrawByFiftyMovesSinceWhiteCapture() {

        Board board = BoardUtils.emptyBoard();
        board.setPiece(0, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(28, new Piece(Colour.BLACK, PieceType.KNIGHT));
        board.setPiece(36, new Piece(Colour.BLACK, PieceType.KNIGHT));
        board.setPiece(35, new Piece(Colour.BLACK, PieceType.QUEEN));

        board.setPiece(63, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(18, new Piece(Colour.WHITE, PieceType.KNIGHT));

        Game game = new Game(board);
        game.setTurn(Colour.WHITE);

        // black knight captures white queen
        game.makeMove(move("c3", "d5"));

        // kings walk about until 50 move rule reached
        game.makeMove(move("a1", "a2"));
        game.makeMove(move("h8", "h7"));

        game.makeMove(move("a2", "a3"));
        game.makeMove(move("h7", "h6"));

        game.makeMove(move("a3", "a4"));
        game.makeMove(move("h6", "h5"));

        game.makeMove(move("a4", "a5"));
        game.makeMove(move("h5", "h4"));

        game.makeMove(move("a5", "a6"));
        game.makeMove(move("h4", "h3"));

        game.makeMove(move("a6", "a7"));
        game.makeMove(move("h3", "h2"));

        game.makeMove(move("a7", "a8"));
        game.makeMove(move("h2", "h1"));

        game.makeMove(move("a8", "b8"));
        game.makeMove(move("h1", "g1"));

        game.makeMove(move("b8", "c8"));
        game.makeMove(move("g1", "f1"));

        game.makeMove(move("c8", "d8"));
        game.makeMove(move("f1", "e1"));

        game.makeMove(move("d8", "e8"));
        game.makeMove(move("e1", "d1"));

        game.makeMove(move("e8", "f8"));
        game.makeMove(move("d1", "c1"));

        game.makeMove(move("f8", "g8"));
        game.makeMove(move("c1", "b1"));

        game.makeMove(move("g8", "h8"));
        game.makeMove(move("b1", "a1"));

        game.makeMove(move("h8", "h7"));
        game.makeMove(move("a1", "a2"));

        game.makeMove(move("h7", "h6"));
        game.makeMove(move("a2", "a3"));

        game.makeMove(move("h6", "h5"));
        game.makeMove(move("a3", "a4"));

        game.makeMove(move("h5", "h4"));
        game.makeMove(move("a4", "a5"));

        game.makeMove(move("h4", "h3"));
        game.makeMove(move("a5", "a6"));

        game.makeMove(move("h3", "h2"));
        game.makeMove(move("a6", "a7"));

        game.makeMove(move("h2", "h1"));
        game.makeMove(move("a7", "a8"));

        game.makeMove(move("h1", "g1"));
        game.makeMove(move("a8", "b8"));

        game.makeMove(move("g1", "f1"));
        game.makeMove(move("b8", "c8"));

        game.makeMove(move("f1", "e1"));
        game.makeMove(move("c8", "d8"));

        game.makeMove(move("e1", "d1"));
        game.makeMove(move("d8", "e8"));

        game.makeMove(move("d1", "c1"));
        game.makeMove(move("e8", "f8"));

        game.makeMove(move("c1", "b1"));
        game.makeMove(move("f8", "g8"));

        game.makeMove(move("b1", "a1"));
        game.makeMove(move("g8", "h8"));

        game.makeMove(move("a1", "a2"));
        game.makeMove(move("h8", "h7"));

        game.makeMove(move("a2", "a3"));
        game.makeMove(move("h7", "h6"));

        game.makeMove(move("a3", "a4"));
        game.makeMove(move("h6", "h5"));

        game.makeMove(move("a4", "a5"));
        game.makeMove(move("h5", "h4"));

        game.makeMove(move("a5", "a6"));
        game.makeMove(move("h4", "h3"));

        game.makeMove(move("a6", "a7"));
        game.makeMove(move("h3", "h2"));

        game.makeMove(move("a7", "a8"));
        game.makeMove(move("h2", "h1"));

        game.makeMove(move("a8", "b8"));
        game.makeMove(move("h1", "g1"));

        game.makeMove(move("b8", "c8"));
        game.makeMove(move("g1", "f1"));

        game.makeMove(move("c8", "d8"));
        game.makeMove(move("f1", "e1"));

        game.makeMove(move("d8", "e8"));
        game.makeMove(move("e1", "d1"));

        game.makeMove(move("e8", "f8"));
        game.makeMove(move("d1", "c1"));

        game.makeMove(move("f8", "g8"));
        game.makeMove(move("c1", "b1"));

        game.makeMove(move("g8", "h8"));
        game.makeMove(move("b1", "a1"));

        game.makeMove(move("h8", "h7"));
        game.makeMove(move("a1", "a2"));

        game.makeMove(move("h7", "h6"));
        game.makeMove(move("a2", "a3"));

        game.makeMove(move("h6", "h5"));
        game.makeMove(move("a3", "a4"));

        game.makeMove(move("h5", "h4"));
        game.makeMove(move("a4", "a5"));

        game.makeMove(move("h4", "h3"));
        game.makeMove(move("a5", "a6"));

        game.makeMove(move("h3", "h2"));
        game.makeMove(move("a6", "a7"));

        game.makeMove(move("h2", "h1"));
        game.makeMove(move("a7", "a8"));

        // 50 move rule not yet reached
        GameResult result = game.makeMove(move("h1", "g1"));
        Assertions.assertNotEquals(GameResult.ResultType.DRAW, result.getResultType());

        // 50 move rule reached
        result = game.makeMove(move("a8", "b8"));
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        DrawResult drawResult = (DrawResult) result;
        Assertions.assertEquals(DrawType.FIFTY_MOVE_RULE, drawResult.getDrawType());

    }

    @Test
    public void testDrawByFiftyMovesSinceWhitePawnMove() {

        Board board = BoardUtils.emptyBoard();
        board.setPiece(0, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(36, new Piece(Colour.BLACK, PieceType.PAWN));

        board.setPiece(63, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(12, new Piece(Colour.WHITE, PieceType.PAWN));

        Game game = new Game(board);
        game.setTurn(Colour.WHITE);

        // white pawn makes last possible pawn move
        game.makeMove(move("e2", "e4"));

        // kings walk about until 50 move rule reached
        game.makeMove(move("a1", "a2"));
        game.makeMove(move("h8", "h7"));

        game.makeMove(move("a2", "a3"));
        game.makeMove(move("h7", "h6"));

        game.makeMove(move("a3", "a4"));
        game.makeMove(move("h6", "h5"));

        game.makeMove(move("a4", "a5"));
        game.makeMove(move("h5", "h4"));

        game.makeMove(move("a5", "a6"));
        game.makeMove(move("h4", "h3"));

        game.makeMove(move("a6", "a7"));
        game.makeMove(move("h3", "h2"));

        game.makeMove(move("a7", "a8"));
        game.makeMove(move("h2", "h1"));

        game.makeMove(move("a8", "b8"));
        game.makeMove(move("h1", "g1"));

        game.makeMove(move("b8", "c8"));
        game.makeMove(move("g1", "f1"));

        game.makeMove(move("c8", "d8"));
        game.makeMove(move("f1", "e1"));

        game.makeMove(move("d8", "e8"));
        game.makeMove(move("e1", "d1"));

        game.makeMove(move("e8", "f8"));
        game.makeMove(move("d1", "c1"));

        game.makeMove(move("f8", "g8"));
        game.makeMove(move("c1", "b1"));

        game.makeMove(move("g8", "h8"));
        game.makeMove(move("b1", "a1"));

        game.makeMove(move("h8", "h7"));
        game.makeMove(move("a1", "a2"));

        game.makeMove(move("h7", "h6"));
        game.makeMove(move("a2", "a3"));

        game.makeMove(move("h6", "h5"));
        game.makeMove(move("a3", "a4"));

        game.makeMove(move("h5", "h4"));
        game.makeMove(move("a4", "a5"));

        game.makeMove(move("h4", "h3"));
        game.makeMove(move("a5", "a6"));

        game.makeMove(move("h3", "h2"));
        game.makeMove(move("a6", "a7"));

        game.makeMove(move("h2", "h1"));
        game.makeMove(move("a7", "a8"));

        game.makeMove(move("h1", "g1"));
        game.makeMove(move("a8", "b8"));

        game.makeMove(move("g1", "f1"));
        game.makeMove(move("b8", "c8"));

        game.makeMove(move("f1", "e1"));
        game.makeMove(move("c8", "d8"));

        game.makeMove(move("e1", "d1"));
        game.makeMove(move("d8", "e8"));

        game.makeMove(move("d1", "c1"));
        game.makeMove(move("e8", "f8"));

        game.makeMove(move("c1", "b1"));
        game.makeMove(move("f8", "g8"));

        game.makeMove(move("b1", "a1"));
        game.makeMove(move("g8", "h8"));

        game.makeMove(move("a1", "a2"));
        game.makeMove(move("h8", "h7"));

        game.makeMove(move("a2", "a3"));
        game.makeMove(move("h7", "h6"));

        game.makeMove(move("a3", "a4"));
        game.makeMove(move("h6", "h5"));

        game.makeMove(move("a4", "a5"));
        game.makeMove(move("h5", "h4"));

        game.makeMove(move("a5", "a6"));
        game.makeMove(move("h4", "h3"));

        game.makeMove(move("a6", "a7"));
        game.makeMove(move("h3", "h2"));

        game.makeMove(move("a7", "a8"));
        game.makeMove(move("h2", "h1"));

        game.makeMove(move("a8", "b8"));
        game.makeMove(move("h1", "g1"));

        game.makeMove(move("b8", "c8"));
        game.makeMove(move("g1", "f1"));

        game.makeMove(move("c8", "d8"));
        game.makeMove(move("f1", "e1"));

        game.makeMove(move("d8", "e8"));
        game.makeMove(move("e1", "d1"));

        game.makeMove(move("e8", "f8"));
        game.makeMove(move("d1", "c1"));

        game.makeMove(move("f8", "g8"));
        game.makeMove(move("c1", "b1"));

        game.makeMove(move("g8", "h8"));
        game.makeMove(move("b1", "a1"));

        game.makeMove(move("h8", "h7"));
        game.makeMove(move("a1", "a2"));

        game.makeMove(move("h7", "h6"));
        game.makeMove(move("a2", "a3"));

        game.makeMove(move("h6", "h5"));
        game.makeMove(move("a3", "a4"));

        game.makeMove(move("h5", "h4"));
        game.makeMove(move("a4", "a5"));

        game.makeMove(move("h4", "h3"));
        game.makeMove(move("a5", "a6"));

        game.makeMove(move("h3", "h2"));
        game.makeMove(move("a6", "a7"));

        game.makeMove(move("h2", "h1"));
        game.makeMove(move("a7", "a8"));

        // 50 move rule not yet reached
        GameResult result = game.makeMove(move("h1", "g1"));
        Assertions.assertNotEquals(GameResult.ResultType.DRAW, result.getResultType());

        // 50 move rule reached
        result = game.makeMove(move("a8", "b8"));
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        DrawResult drawResult = (DrawResult) result;
        Assertions.assertEquals(DrawType.FIFTY_MOVE_RULE, drawResult.getDrawType());

    }

    @Test
    public void testDrawByFiftyMovesSinceBlackCapture() {

        Board board = BoardUtils.emptyBoard();
        board.setPiece(0, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(28, new Piece(Colour.WHITE, PieceType.KNIGHT));
        board.setPiece(36, new Piece(Colour.WHITE, PieceType.KNIGHT));
        board.setPiece(35, new Piece(Colour.WHITE, PieceType.QUEEN));

        board.setPiece(63, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(18, new Piece(Colour.BLACK, PieceType.KNIGHT));

        Game game = new Game(board);
        game.setTurn(Colour.BLACK);

        // black knight captures white queen
        game.makeMove(move("c3", "d5"));

        // kings walk about until 50 move rule reached
        game.makeMove(move("a1", "a2"));
        game.makeMove(move("h8", "h7"));

        game.makeMove(move("a2", "a3"));
        game.makeMove(move("h7", "h6"));

        game.makeMove(move("a3", "a4"));
        game.makeMove(move("h6", "h5"));

        game.makeMove(move("a4", "a5"));
        game.makeMove(move("h5", "h4"));

        game.makeMove(move("a5", "a6"));
        game.makeMove(move("h4", "h3"));

        game.makeMove(move("a6", "a7"));
        game.makeMove(move("h3", "h2"));

        game.makeMove(move("a7", "a8"));
        game.makeMove(move("h2", "h1"));

        game.makeMove(move("a8", "b8"));
        game.makeMove(move("h1", "g1"));

        game.makeMove(move("b8", "c8"));
        game.makeMove(move("g1", "f1"));

        game.makeMove(move("c8", "d8"));
        game.makeMove(move("f1", "e1"));

        game.makeMove(move("d8", "e8"));
        game.makeMove(move("e1", "d1"));

        game.makeMove(move("e8", "f8"));
        game.makeMove(move("d1", "c1"));

        game.makeMove(move("f8", "g8"));
        game.makeMove(move("c1", "b1"));

        game.makeMove(move("g8", "h8"));
        game.makeMove(move("b1", "a1"));

        game.makeMove(move("h8", "h7"));
        game.makeMove(move("a1", "a2"));

        game.makeMove(move("h7", "h6"));
        game.makeMove(move("a2", "a3"));

        game.makeMove(move("h6", "h5"));
        game.makeMove(move("a3", "a4"));

        game.makeMove(move("h5", "h4"));
        game.makeMove(move("a4", "a5"));

        game.makeMove(move("h4", "h3"));
        game.makeMove(move("a5", "a6"));

        game.makeMove(move("h3", "h2"));
        game.makeMove(move("a6", "a7"));

        game.makeMove(move("h2", "h1"));
        game.makeMove(move("a7", "a8"));

        game.makeMove(move("h1", "g1"));
        game.makeMove(move("a8", "b8"));

        game.makeMove(move("g1", "f1"));
        game.makeMove(move("b8", "c8"));

        game.makeMove(move("f1", "e1"));
        game.makeMove(move("c8", "d8"));

        game.makeMove(move("e1", "d1"));
        game.makeMove(move("d8", "e8"));

        game.makeMove(move("d1", "c1"));
        game.makeMove(move("e8", "f8"));

        game.makeMove(move("c1", "b1"));
        game.makeMove(move("f8", "g8"));

        game.makeMove(move("b1", "a1"));
        game.makeMove(move("g8", "h8"));

        game.makeMove(move("a1", "a2"));
        game.makeMove(move("h8", "h7"));

        game.makeMove(move("a2", "a3"));
        game.makeMove(move("h7", "h6"));

        game.makeMove(move("a3", "a4"));
        game.makeMove(move("h6", "h5"));

        game.makeMove(move("a4", "a5"));
        game.makeMove(move("h5", "h4"));

        game.makeMove(move("a5", "a6"));
        game.makeMove(move("h4", "h3"));

        game.makeMove(move("a6", "a7"));
        game.makeMove(move("h3", "h2"));

        game.makeMove(move("a7", "a8"));
        game.makeMove(move("h2", "h1"));

        game.makeMove(move("a8", "b8"));
        game.makeMove(move("h1", "g1"));

        game.makeMove(move("b8", "c8"));
        game.makeMove(move("g1", "f1"));

        game.makeMove(move("c8", "d8"));
        game.makeMove(move("f1", "e1"));

        game.makeMove(move("d8", "e8"));
        game.makeMove(move("e1", "d1"));

        game.makeMove(move("e8", "f8"));
        game.makeMove(move("d1", "c1"));

        game.makeMove(move("f8", "g8"));
        game.makeMove(move("c1", "b1"));

        game.makeMove(move("g8", "h8"));
        game.makeMove(move("b1", "a1"));

        game.makeMove(move("h8", "h7"));
        game.makeMove(move("a1", "a2"));

        game.makeMove(move("h7", "h6"));
        game.makeMove(move("a2", "a3"));

        game.makeMove(move("h6", "h5"));
        game.makeMove(move("a3", "a4"));

        game.makeMove(move("h5", "h4"));
        game.makeMove(move("a4", "a5"));

        game.makeMove(move("h4", "h3"));
        game.makeMove(move("a5", "a6"));

        game.makeMove(move("h3", "h2"));
        game.makeMove(move("a6", "a7"));

        game.makeMove(move("h2", "h1"));
        game.makeMove(move("a7", "a8"));

        // 50 move rule not yet reached
        GameResult result = game.makeMove(move("h1", "g1"));
        Assertions.assertNotEquals(GameResult.ResultType.DRAW, result.getResultType());

        // 50 move rule reached
        result = game.makeMove(move("a8", "b8"));
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        DrawResult drawResult = (DrawResult) result;
        Assertions.assertEquals(DrawType.FIFTY_MOVE_RULE, drawResult.getDrawType());

    }

    @Test
    public void testDrawByFiftyMovesSinceBlackPawnMove() {

        Board board = BoardUtils.emptyBoard();
        board.setPiece(0, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(28, new Piece(Colour.WHITE, PieceType.PAWN));

        board.setPiece(63, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(52, new Piece(Colour.BLACK, PieceType.PAWN));

        Game game = new Game(board);
        game.setTurn(Colour.BLACK);

        // black pawn makes last possible pawn move
        game.makeMove(move("e7", "e5"));

        // kings walk about until 50 move rule reached
        game.makeMove(move("a1", "a2"));
        game.makeMove(move("h8", "h7"));

        game.makeMove(move("a2", "a3"));
        game.makeMove(move("h7", "h6"));

        game.makeMove(move("a3", "a4"));
        game.makeMove(move("h6", "h5"));

        game.makeMove(move("a4", "a5"));
        game.makeMove(move("h5", "h4"));

        game.makeMove(move("a5", "a6"));
        game.makeMove(move("h4", "h3"));

        game.makeMove(move("a6", "a7"));
        game.makeMove(move("h3", "h2"));

        game.makeMove(move("a7", "a8"));
        game.makeMove(move("h2", "h1"));

        game.makeMove(move("a8", "b8"));
        game.makeMove(move("h1", "g1"));

        game.makeMove(move("b8", "c8"));
        game.makeMove(move("g1", "f1"));

        game.makeMove(move("c8", "d8"));
        game.makeMove(move("f1", "e1"));

        game.makeMove(move("d8", "e8"));
        game.makeMove(move("e1", "d1"));

        game.makeMove(move("e8", "f8"));
        game.makeMove(move("d1", "c1"));

        game.makeMove(move("f8", "g8"));
        game.makeMove(move("c1", "b1"));

        game.makeMove(move("g8", "h8"));
        game.makeMove(move("b1", "a1"));

        game.makeMove(move("h8", "h7"));
        game.makeMove(move("a1", "a2"));

        game.makeMove(move("h7", "h6"));
        game.makeMove(move("a2", "a3"));

        game.makeMove(move("h6", "h5"));
        game.makeMove(move("a3", "a4"));

        game.makeMove(move("h5", "h4"));
        game.makeMove(move("a4", "a5"));

        game.makeMove(move("h4", "h3"));
        game.makeMove(move("a5", "a6"));

        game.makeMove(move("h3", "h2"));
        game.makeMove(move("a6", "a7"));

        game.makeMove(move("h2", "h1"));
        game.makeMove(move("a7", "a8"));

        game.makeMove(move("h1", "g1"));
        game.makeMove(move("a8", "b8"));

        game.makeMove(move("g1", "f1"));
        game.makeMove(move("b8", "c8"));

        game.makeMove(move("f1", "e1"));
        game.makeMove(move("c8", "d8"));

        game.makeMove(move("e1", "d1"));
        game.makeMove(move("d8", "e8"));

        game.makeMove(move("d1", "c1"));
        game.makeMove(move("e8", "f8"));

        game.makeMove(move("c1", "b1"));
        game.makeMove(move("f8", "g8"));

        game.makeMove(move("b1", "a1"));
        game.makeMove(move("g8", "h8"));

        game.makeMove(move("a1", "a2"));
        game.makeMove(move("h8", "h7"));

        game.makeMove(move("a2", "a3"));
        game.makeMove(move("h7", "h6"));

        game.makeMove(move("a3", "a4"));
        game.makeMove(move("h6", "h5"));

        game.makeMove(move("a4", "a5"));
        game.makeMove(move("h5", "h4"));

        game.makeMove(move("a5", "a6"));
        game.makeMove(move("h4", "h3"));

        game.makeMove(move("a6", "a7"));
        game.makeMove(move("h3", "h2"));

        game.makeMove(move("a7", "a8"));
        game.makeMove(move("h2", "h1"));

        game.makeMove(move("a8", "b8"));
        game.makeMove(move("h1", "g1"));

        game.makeMove(move("b8", "c8"));
        game.makeMove(move("g1", "f1"));

        game.makeMove(move("c8", "d8"));
        game.makeMove(move("f1", "e1"));

        game.makeMove(move("d8", "e8"));
        game.makeMove(move("e1", "d1"));

        game.makeMove(move("e8", "f8"));
        game.makeMove(move("d1", "c1"));

        game.makeMove(move("f8", "g8"));
        game.makeMove(move("c1", "b1"));

        game.makeMove(move("g8", "h8"));
        game.makeMove(move("b1", "a1"));

        game.makeMove(move("h8", "h7"));
        game.makeMove(move("a1", "a2"));

        game.makeMove(move("h7", "h6"));
        game.makeMove(move("a2", "a3"));

        game.makeMove(move("h6", "h5"));
        game.makeMove(move("a3", "a4"));

        game.makeMove(move("h5", "h4"));
        game.makeMove(move("a4", "a5"));

        game.makeMove(move("h4", "h3"));
        game.makeMove(move("a5", "a6"));

        game.makeMove(move("h3", "h2"));
        game.makeMove(move("a6", "a7"));

        game.makeMove(move("h2", "h1"));
        game.makeMove(move("a7", "a8"));

        // 50 move rule not yet reached
        GameResult result = game.makeMove(move("h1", "g1"));
        Assertions.assertNotEquals(GameResult.ResultType.DRAW, result.getResultType());

        // 50 move rule reached
        result = game.makeMove(move("a8", "b8"));
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        DrawResult drawResult = (DrawResult) result;
        Assertions.assertEquals(DrawType.FIFTY_MOVE_RULE, drawResult.getDrawType());

    }

    private Move move(String startSquare, String endSquare) {
        return MoveUtils.fromNotation(startSquare, endSquare);
    }

}
