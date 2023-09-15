package com.kelseyde.calvin.service.game.drawcalculator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.game.DrawType;
import com.kelseyde.calvin.model.game.Game;
import com.kelseyde.calvin.model.game.result.DrawResult;
import com.kelseyde.calvin.model.game.result.GameResult;
import com.kelseyde.calvin.model.move.Move;
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
        game.playMove(move("c3", "d5"));

        // kings walk about until 50 move rule reached
        game.playMove(move("a1", "a2"));
        game.playMove(move("h8", "h7"));

        game.playMove(move("a2", "a3"));
        game.playMove(move("h7", "h6"));

        game.playMove(move("a3", "a4"));
        game.playMove(move("h6", "h5"));

        game.playMove(move("a4", "a5"));
        game.playMove(move("h5", "h4"));

        game.playMove(move("a5", "a6"));
        game.playMove(move("h4", "h3"));

        game.playMove(move("a6", "a7"));
        game.playMove(move("h3", "h2"));

        game.playMove(move("a7", "a8"));
        game.playMove(move("h2", "h1"));

        game.playMove(move("a8", "b8"));
        game.playMove(move("h1", "g1"));

        game.playMove(move("b8", "c8"));
        game.playMove(move("g1", "f1"));

        game.playMove(move("c8", "d8"));
        game.playMove(move("f1", "e1"));

        game.playMove(move("d8", "e8"));
        game.playMove(move("e1", "d1"));

        game.playMove(move("e8", "f8"));
        game.playMove(move("d1", "c1"));

        game.playMove(move("f8", "g8"));
        game.playMove(move("c1", "b1"));

        game.playMove(move("g8", "h8"));
        game.playMove(move("b1", "a1"));

        game.playMove(move("h8", "h7"));
        game.playMove(move("a1", "a2"));

        game.playMove(move("h7", "h6"));
        game.playMove(move("a2", "a3"));

        game.playMove(move("h6", "h5"));
        game.playMove(move("a3", "a4"));

        game.playMove(move("h5", "h4"));
        game.playMove(move("a4", "a5"));

        game.playMove(move("h4", "h3"));
        game.playMove(move("a5", "a6"));

        game.playMove(move("h3", "h2"));
        game.playMove(move("a6", "a7"));

        game.playMove(move("h2", "h1"));
        game.playMove(move("a7", "a8"));

        game.playMove(move("h1", "g1"));
        game.playMove(move("a8", "b8"));

        game.playMove(move("g1", "f1"));
        game.playMove(move("b8", "c8"));

        game.playMove(move("f1", "e1"));
        game.playMove(move("c8", "d8"));

        game.playMove(move("e1", "d1"));
        game.playMove(move("d8", "e8"));

        game.playMove(move("d1", "c1"));
        game.playMove(move("e8", "f8"));

        game.playMove(move("c1", "b1"));
        game.playMove(move("f8", "g8"));

        game.playMove(move("b1", "a1"));
        game.playMove(move("g8", "h8"));

        game.playMove(move("a1", "a2"));
        game.playMove(move("h8", "h7"));

        game.playMove(move("a2", "a3"));
        game.playMove(move("h7", "h6"));

        game.playMove(move("a3", "a4"));
        game.playMove(move("h6", "h5"));

        game.playMove(move("a4", "a5"));
        game.playMove(move("h5", "h4"));

        game.playMove(move("a5", "a6"));
        game.playMove(move("h4", "h3"));

        game.playMove(move("a6", "a7"));
        game.playMove(move("h3", "h2"));

        game.playMove(move("a7", "a8"));
        game.playMove(move("h2", "h1"));

        game.playMove(move("a8", "b8"));
        game.playMove(move("h1", "g1"));

        game.playMove(move("b8", "c8"));
        game.playMove(move("g1", "f1"));

        game.playMove(move("c8", "d8"));
        game.playMove(move("f1", "e1"));

        game.playMove(move("d8", "e8"));
        game.playMove(move("e1", "d1"));

        game.playMove(move("e8", "f8"));
        game.playMove(move("d1", "c1"));

        game.playMove(move("f8", "g8"));
        game.playMove(move("c1", "b1"));

        game.playMove(move("g8", "h8"));
        game.playMove(move("b1", "a1"));

        game.playMove(move("h8", "h7"));
        game.playMove(move("a1", "a2"));

        game.playMove(move("h7", "h6"));
        game.playMove(move("a2", "a3"));

        game.playMove(move("h6", "h5"));
        game.playMove(move("a3", "a4"));

        game.playMove(move("h5", "h4"));
        game.playMove(move("a4", "a5"));

        game.playMove(move("h4", "h3"));
        game.playMove(move("a5", "a6"));

        game.playMove(move("h3", "h2"));
        game.playMove(move("a6", "a7"));

        game.playMove(move("h2", "h1"));
        game.playMove(move("a7", "a8"));

        // 50 move rule not yet reached
        GameResult result = game.playMove(move("h1", "g1"));
        Assertions.assertNotEquals(GameResult.ResultType.DRAW, result.getResultType());

        // 50 move rule reached
        result = game.playMove(move("a8", "b8"));
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
        game.playMove(move("e2", "e4"));

        // kings walk about until 50 move rule reached
        game.playMove(move("a1", "a2"));
        game.playMove(move("h8", "h7"));

        game.playMove(move("a2", "a3"));
        game.playMove(move("h7", "h6"));

        game.playMove(move("a3", "a4"));
        game.playMove(move("h6", "h5"));

        game.playMove(move("a4", "a5"));
        game.playMove(move("h5", "h4"));

        game.playMove(move("a5", "a6"));
        game.playMove(move("h4", "h3"));

        game.playMove(move("a6", "a7"));
        game.playMove(move("h3", "h2"));

        game.playMove(move("a7", "a8"));
        game.playMove(move("h2", "h1"));

        game.playMove(move("a8", "b8"));
        game.playMove(move("h1", "g1"));

        game.playMove(move("b8", "c8"));
        game.playMove(move("g1", "f1"));

        game.playMove(move("c8", "d8"));
        game.playMove(move("f1", "e1"));

        game.playMove(move("d8", "e8"));
        game.playMove(move("e1", "d1"));

        game.playMove(move("e8", "f8"));
        game.playMove(move("d1", "c1"));

        game.playMove(move("f8", "g8"));
        game.playMove(move("c1", "b1"));

        game.playMove(move("g8", "h8"));
        game.playMove(move("b1", "a1"));

        game.playMove(move("h8", "h7"));
        game.playMove(move("a1", "a2"));

        game.playMove(move("h7", "h6"));
        game.playMove(move("a2", "a3"));

        game.playMove(move("h6", "h5"));
        game.playMove(move("a3", "a4"));

        game.playMove(move("h5", "h4"));
        game.playMove(move("a4", "a5"));

        game.playMove(move("h4", "h3"));
        game.playMove(move("a5", "a6"));

        game.playMove(move("h3", "h2"));
        game.playMove(move("a6", "a7"));

        game.playMove(move("h2", "h1"));
        game.playMove(move("a7", "a8"));

        game.playMove(move("h1", "g1"));
        game.playMove(move("a8", "b8"));

        game.playMove(move("g1", "f1"));
        game.playMove(move("b8", "c8"));

        game.playMove(move("f1", "e1"));
        game.playMove(move("c8", "d8"));

        game.playMove(move("e1", "d1"));
        game.playMove(move("d8", "e8"));

        game.playMove(move("d1", "c1"));
        game.playMove(move("e8", "f8"));

        game.playMove(move("c1", "b1"));
        game.playMove(move("f8", "g8"));

        game.playMove(move("b1", "a1"));
        game.playMove(move("g8", "h8"));

        game.playMove(move("a1", "a2"));
        game.playMove(move("h8", "h7"));

        game.playMove(move("a2", "a3"));
        game.playMove(move("h7", "h6"));

        game.playMove(move("a3", "a4"));
        game.playMove(move("h6", "h5"));

        game.playMove(move("a4", "a5"));
        game.playMove(move("h5", "h4"));

        game.playMove(move("a5", "a6"));
        game.playMove(move("h4", "h3"));

        game.playMove(move("a6", "a7"));
        game.playMove(move("h3", "h2"));

        game.playMove(move("a7", "a8"));
        game.playMove(move("h2", "h1"));

        game.playMove(move("a8", "b8"));
        game.playMove(move("h1", "g1"));

        game.playMove(move("b8", "c8"));
        game.playMove(move("g1", "f1"));

        game.playMove(move("c8", "d8"));
        game.playMove(move("f1", "e1"));

        game.playMove(move("d8", "e8"));
        game.playMove(move("e1", "d1"));

        game.playMove(move("e8", "f8"));
        game.playMove(move("d1", "c1"));

        game.playMove(move("f8", "g8"));
        game.playMove(move("c1", "b1"));

        game.playMove(move("g8", "h8"));
        game.playMove(move("b1", "a1"));

        game.playMove(move("h8", "h7"));
        game.playMove(move("a1", "a2"));

        game.playMove(move("h7", "h6"));
        game.playMove(move("a2", "a3"));

        game.playMove(move("h6", "h5"));
        game.playMove(move("a3", "a4"));

        game.playMove(move("h5", "h4"));
        game.playMove(move("a4", "a5"));

        game.playMove(move("h4", "h3"));
        game.playMove(move("a5", "a6"));

        game.playMove(move("h3", "h2"));
        game.playMove(move("a6", "a7"));

        game.playMove(move("h2", "h1"));
        game.playMove(move("a7", "a8"));

        // 50 move rule not yet reached
        GameResult result = game.playMove(move("h1", "g1"));
        Assertions.assertNotEquals(GameResult.ResultType.DRAW, result.getResultType());

        // 50 move rule reached
        result = game.playMove(move("a8", "b8"));
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
        game.playMove(move("c3", "d5"));

        // kings walk about until 50 move rule reached
        game.playMove(move("a1", "a2"));
        game.playMove(move("h8", "h7"));

        game.playMove(move("a2", "a3"));
        game.playMove(move("h7", "h6"));

        game.playMove(move("a3", "a4"));
        game.playMove(move("h6", "h5"));

        game.playMove(move("a4", "a5"));
        game.playMove(move("h5", "h4"));

        game.playMove(move("a5", "a6"));
        game.playMove(move("h4", "h3"));

        game.playMove(move("a6", "a7"));
        game.playMove(move("h3", "h2"));

        game.playMove(move("a7", "a8"));
        game.playMove(move("h2", "h1"));

        game.playMove(move("a8", "b8"));
        game.playMove(move("h1", "g1"));

        game.playMove(move("b8", "c8"));
        game.playMove(move("g1", "f1"));

        game.playMove(move("c8", "d8"));
        game.playMove(move("f1", "e1"));

        game.playMove(move("d8", "e8"));
        game.playMove(move("e1", "d1"));

        game.playMove(move("e8", "f8"));
        game.playMove(move("d1", "c1"));

        game.playMove(move("f8", "g8"));
        game.playMove(move("c1", "b1"));

        game.playMove(move("g8", "h8"));
        game.playMove(move("b1", "a1"));

        game.playMove(move("h8", "h7"));
        game.playMove(move("a1", "a2"));

        game.playMove(move("h7", "h6"));
        game.playMove(move("a2", "a3"));

        game.playMove(move("h6", "h5"));
        game.playMove(move("a3", "a4"));

        game.playMove(move("h5", "h4"));
        game.playMove(move("a4", "a5"));

        game.playMove(move("h4", "h3"));
        game.playMove(move("a5", "a6"));

        game.playMove(move("h3", "h2"));
        game.playMove(move("a6", "a7"));

        game.playMove(move("h2", "h1"));
        game.playMove(move("a7", "a8"));

        game.playMove(move("h1", "g1"));
        game.playMove(move("a8", "b8"));

        game.playMove(move("g1", "f1"));
        game.playMove(move("b8", "c8"));

        game.playMove(move("f1", "e1"));
        game.playMove(move("c8", "d8"));

        game.playMove(move("e1", "d1"));
        game.playMove(move("d8", "e8"));

        game.playMove(move("d1", "c1"));
        game.playMove(move("e8", "f8"));

        game.playMove(move("c1", "b1"));
        game.playMove(move("f8", "g8"));

        game.playMove(move("b1", "a1"));
        game.playMove(move("g8", "h8"));

        game.playMove(move("a1", "a2"));
        game.playMove(move("h8", "h7"));

        game.playMove(move("a2", "a3"));
        game.playMove(move("h7", "h6"));

        game.playMove(move("a3", "a4"));
        game.playMove(move("h6", "h5"));

        game.playMove(move("a4", "a5"));
        game.playMove(move("h5", "h4"));

        game.playMove(move("a5", "a6"));
        game.playMove(move("h4", "h3"));

        game.playMove(move("a6", "a7"));
        game.playMove(move("h3", "h2"));

        game.playMove(move("a7", "a8"));
        game.playMove(move("h2", "h1"));

        game.playMove(move("a8", "b8"));
        game.playMove(move("h1", "g1"));

        game.playMove(move("b8", "c8"));
        game.playMove(move("g1", "f1"));

        game.playMove(move("c8", "d8"));
        game.playMove(move("f1", "e1"));

        game.playMove(move("d8", "e8"));
        game.playMove(move("e1", "d1"));

        game.playMove(move("e8", "f8"));
        game.playMove(move("d1", "c1"));

        game.playMove(move("f8", "g8"));
        game.playMove(move("c1", "b1"));

        game.playMove(move("g8", "h8"));
        game.playMove(move("b1", "a1"));

        game.playMove(move("h8", "h7"));
        game.playMove(move("a1", "a2"));

        game.playMove(move("h7", "h6"));
        game.playMove(move("a2", "a3"));

        game.playMove(move("h6", "h5"));
        game.playMove(move("a3", "a4"));

        game.playMove(move("h5", "h4"));
        game.playMove(move("a4", "a5"));

        game.playMove(move("h4", "h3"));
        game.playMove(move("a5", "a6"));

        game.playMove(move("h3", "h2"));
        game.playMove(move("a6", "a7"));

        game.playMove(move("h2", "h1"));
        game.playMove(move("a7", "a8"));

        // 50 move rule not yet reached
        GameResult result = game.playMove(move("h1", "g1"));
        Assertions.assertNotEquals(GameResult.ResultType.DRAW, result.getResultType());

        // 50 move rule reached
        result = game.playMove(move("a8", "b8"));
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
        game.playMove(move("e7", "e5"));

        // kings walk about until 50 move rule reached
        game.playMove(move("a1", "a2"));
        game.playMove(move("h8", "h7"));

        game.playMove(move("a2", "a3"));
        game.playMove(move("h7", "h6"));

        game.playMove(move("a3", "a4"));
        game.playMove(move("h6", "h5"));

        game.playMove(move("a4", "a5"));
        game.playMove(move("h5", "h4"));

        game.playMove(move("a5", "a6"));
        game.playMove(move("h4", "h3"));

        game.playMove(move("a6", "a7"));
        game.playMove(move("h3", "h2"));

        game.playMove(move("a7", "a8"));
        game.playMove(move("h2", "h1"));

        game.playMove(move("a8", "b8"));
        game.playMove(move("h1", "g1"));

        game.playMove(move("b8", "c8"));
        game.playMove(move("g1", "f1"));

        game.playMove(move("c8", "d8"));
        game.playMove(move("f1", "e1"));

        game.playMove(move("d8", "e8"));
        game.playMove(move("e1", "d1"));

        game.playMove(move("e8", "f8"));
        game.playMove(move("d1", "c1"));

        game.playMove(move("f8", "g8"));
        game.playMove(move("c1", "b1"));

        game.playMove(move("g8", "h8"));
        game.playMove(move("b1", "a1"));

        game.playMove(move("h8", "h7"));
        game.playMove(move("a1", "a2"));

        game.playMove(move("h7", "h6"));
        game.playMove(move("a2", "a3"));

        game.playMove(move("h6", "h5"));
        game.playMove(move("a3", "a4"));

        game.playMove(move("h5", "h4"));
        game.playMove(move("a4", "a5"));

        game.playMove(move("h4", "h3"));
        game.playMove(move("a5", "a6"));

        game.playMove(move("h3", "h2"));
        game.playMove(move("a6", "a7"));

        game.playMove(move("h2", "h1"));
        game.playMove(move("a7", "a8"));

        game.playMove(move("h1", "g1"));
        game.playMove(move("a8", "b8"));

        game.playMove(move("g1", "f1"));
        game.playMove(move("b8", "c8"));

        game.playMove(move("f1", "e1"));
        game.playMove(move("c8", "d8"));

        game.playMove(move("e1", "d1"));
        game.playMove(move("d8", "e8"));

        game.playMove(move("d1", "c1"));
        game.playMove(move("e8", "f8"));

        game.playMove(move("c1", "b1"));
        game.playMove(move("f8", "g8"));

        game.playMove(move("b1", "a1"));
        game.playMove(move("g8", "h8"));

        game.playMove(move("a1", "a2"));
        game.playMove(move("h8", "h7"));

        game.playMove(move("a2", "a3"));
        game.playMove(move("h7", "h6"));

        game.playMove(move("a3", "a4"));
        game.playMove(move("h6", "h5"));

        game.playMove(move("a4", "a5"));
        game.playMove(move("h5", "h4"));

        game.playMove(move("a5", "a6"));
        game.playMove(move("h4", "h3"));

        game.playMove(move("a6", "a7"));
        game.playMove(move("h3", "h2"));

        game.playMove(move("a7", "a8"));
        game.playMove(move("h2", "h1"));

        game.playMove(move("a8", "b8"));
        game.playMove(move("h1", "g1"));

        game.playMove(move("b8", "c8"));
        game.playMove(move("g1", "f1"));

        game.playMove(move("c8", "d8"));
        game.playMove(move("f1", "e1"));

        game.playMove(move("d8", "e8"));
        game.playMove(move("e1", "d1"));

        game.playMove(move("e8", "f8"));
        game.playMove(move("d1", "c1"));

        game.playMove(move("f8", "g8"));
        game.playMove(move("c1", "b1"));

        game.playMove(move("g8", "h8"));
        game.playMove(move("b1", "a1"));

        game.playMove(move("h8", "h7"));
        game.playMove(move("a1", "a2"));

        game.playMove(move("h7", "h6"));
        game.playMove(move("a2", "a3"));

        game.playMove(move("h6", "h5"));
        game.playMove(move("a3", "a4"));

        game.playMove(move("h5", "h4"));
        game.playMove(move("a4", "a5"));

        game.playMove(move("h4", "h3"));
        game.playMove(move("a5", "a6"));

        game.playMove(move("h3", "h2"));
        game.playMove(move("a6", "a7"));

        game.playMove(move("h2", "h1"));
        game.playMove(move("a7", "a8"));

        // 50 move rule not yet reached
        GameResult result = game.playMove(move("h1", "g1"));
        Assertions.assertNotEquals(GameResult.ResultType.DRAW, result.getResultType());

        // 50 move rule reached
        result = game.playMove(move("a8", "b8"));
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        DrawResult drawResult = (DrawResult) result;
        Assertions.assertEquals(DrawType.FIFTY_MOVE_RULE, drawResult.getDrawType());

    }

    private Move move(String startSquare, String endSquare) {
        return MoveUtils.fromNotation(startSquare, endSquare);
    }

}
