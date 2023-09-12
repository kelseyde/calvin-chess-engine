package com.kelseyde.calvin.service.game.drawcalculator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.game.*;
import com.kelseyde.calvin.utils.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByFiftyMoveRuleTest {

    @Test
    public void testDrawByFiftyMovesSinceWhiteCapture() {

        Board board = Board.emptyBoard();
        board.setPiece(0, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(28, new Piece(Colour.BLACK, PieceType.KNIGHT));
        board.setPiece(36, new Piece(Colour.BLACK, PieceType.KNIGHT));
        board.setPiece(35, new Piece(Colour.BLACK, PieceType.QUEEN));

        board.setPiece(63, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(18, new Piece(Colour.WHITE, PieceType.KNIGHT));

        Game game = Game.fromPosition(board);
        game.setTurn(Colour.WHITE);

        // black knight captures white queen
        game.executeAction(move("c3", "d5"));

        // kings walk about until 50 move rule reached
        game.executeAction(move("a1", "a2"));
        game.executeAction(move("h8", "h7"));

        game.executeAction(move("a2", "a3"));
        game.executeAction(move("h7", "h6"));

        game.executeAction(move("a3", "a4"));
        game.executeAction(move("h6", "h5"));

        game.executeAction(move("a4", "a5"));
        game.executeAction(move("h5", "h4"));

        game.executeAction(move("a5", "a6"));
        game.executeAction(move("h4", "h3"));

        game.executeAction(move("a6", "a7"));
        game.executeAction(move("h3", "h2"));

        game.executeAction(move("a7", "a8"));
        game.executeAction(move("h2", "h1"));

        game.executeAction(move("a8", "b8"));
        game.executeAction(move("h1", "g1"));

        game.executeAction(move("b8", "c8"));
        game.executeAction(move("g1", "f1"));

        game.executeAction(move("c8", "d8"));
        game.executeAction(move("f1", "e1"));

        game.executeAction(move("d8", "e8"));
        game.executeAction(move("e1", "d1"));

        game.executeAction(move("e8", "f8"));
        game.executeAction(move("d1", "c1"));

        game.executeAction(move("f8", "g8"));
        game.executeAction(move("c1", "b1"));

        game.executeAction(move("g8", "h8"));
        game.executeAction(move("b1", "a1"));

        game.executeAction(move("h8", "h7"));
        game.executeAction(move("a1", "a2"));

        game.executeAction(move("h7", "h6"));
        game.executeAction(move("a2", "a3"));

        game.executeAction(move("h6", "h5"));
        game.executeAction(move("a3", "a4"));

        game.executeAction(move("h5", "h4"));
        game.executeAction(move("a4", "a5"));

        game.executeAction(move("h4", "h3"));
        game.executeAction(move("a5", "a6"));

        game.executeAction(move("h3", "h2"));
        game.executeAction(move("a6", "a7"));

        game.executeAction(move("h2", "h1"));
        game.executeAction(move("a7", "a8"));

        game.executeAction(move("h1", "g1"));
        game.executeAction(move("a8", "b8"));

        game.executeAction(move("g1", "f1"));
        game.executeAction(move("b8", "c8"));

        game.executeAction(move("f1", "e1"));
        game.executeAction(move("c8", "d8"));

        game.executeAction(move("e1", "d1"));
        game.executeAction(move("d8", "e8"));

        game.executeAction(move("d1", "c1"));
        game.executeAction(move("e8", "f8"));

        game.executeAction(move("c1", "b1"));
        game.executeAction(move("f8", "g8"));

        game.executeAction(move("b1", "a1"));
        game.executeAction(move("g8", "h8"));

        game.executeAction(move("a1", "a2"));
        game.executeAction(move("h8", "h7"));

        game.executeAction(move("a2", "a3"));
        game.executeAction(move("h7", "h6"));

        game.executeAction(move("a3", "a4"));
        game.executeAction(move("h6", "h5"));

        game.executeAction(move("a4", "a5"));
        game.executeAction(move("h5", "h4"));

        game.executeAction(move("a5", "a6"));
        game.executeAction(move("h4", "h3"));

        game.executeAction(move("a6", "a7"));
        game.executeAction(move("h3", "h2"));

        game.executeAction(move("a7", "a8"));
        game.executeAction(move("h2", "h1"));

        game.executeAction(move("a8", "b8"));
        game.executeAction(move("h1", "g1"));

        game.executeAction(move("b8", "c8"));
        game.executeAction(move("g1", "f1"));

        game.executeAction(move("c8", "d8"));
        game.executeAction(move("f1", "e1"));

        game.executeAction(move("d8", "e8"));
        game.executeAction(move("e1", "d1"));

        game.executeAction(move("e8", "f8"));
        game.executeAction(move("d1", "c1"));

        game.executeAction(move("f8", "g8"));
        game.executeAction(move("c1", "b1"));

        game.executeAction(move("g8", "h8"));
        game.executeAction(move("b1", "a1"));

        game.executeAction(move("h8", "h7"));
        game.executeAction(move("a1", "a2"));

        game.executeAction(move("h7", "h6"));
        game.executeAction(move("a2", "a3"));

        game.executeAction(move("h6", "h5"));
        game.executeAction(move("a3", "a4"));

        game.executeAction(move("h5", "h4"));
        game.executeAction(move("a4", "a5"));

        game.executeAction(move("h4", "h3"));
        game.executeAction(move("a5", "a6"));

        game.executeAction(move("h3", "h2"));
        game.executeAction(move("a6", "a7"));

        game.executeAction(move("h2", "h1"));
        game.executeAction(move("a7", "a8"));

        // 50 move rule not yet reached
        ActionResult result = game.executeAction(move("h1", "g1"));
        Assertions.assertFalse(result.isDraw());

        // 50 move rule reached
        result = game.executeAction(move("a8", "b8"));
        Assertions.assertTrue(result.isDraw());
        Assertions.assertEquals(DrawType.FIFTY_MOVE_RULE, result.getDrawType());

    }

    @Test
    public void testDrawByFiftyMovesSinceWhitePawnMove() {

        Board board = Board.emptyBoard();
        board.setPiece(0, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(36, new Piece(Colour.BLACK, PieceType.PAWN));

        board.setPiece(63, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(12, new Piece(Colour.WHITE, PieceType.PAWN));

        Game game = Game.fromPosition(board);
        game.setTurn(Colour.WHITE);

        // white pawn makes last possible pawn move
        game.executeAction(move("e2", "e4"));

        // kings walk about until 50 move rule reached
        game.executeAction(move("a1", "a2"));
        game.executeAction(move("h8", "h7"));

        game.executeAction(move("a2", "a3"));
        game.executeAction(move("h7", "h6"));

        game.executeAction(move("a3", "a4"));
        game.executeAction(move("h6", "h5"));

        game.executeAction(move("a4", "a5"));
        game.executeAction(move("h5", "h4"));

        game.executeAction(move("a5", "a6"));
        game.executeAction(move("h4", "h3"));

        game.executeAction(move("a6", "a7"));
        game.executeAction(move("h3", "h2"));

        game.executeAction(move("a7", "a8"));
        game.executeAction(move("h2", "h1"));

        game.executeAction(move("a8", "b8"));
        game.executeAction(move("h1", "g1"));

        game.executeAction(move("b8", "c8"));
        game.executeAction(move("g1", "f1"));

        game.executeAction(move("c8", "d8"));
        game.executeAction(move("f1", "e1"));

        game.executeAction(move("d8", "e8"));
        game.executeAction(move("e1", "d1"));

        game.executeAction(move("e8", "f8"));
        game.executeAction(move("d1", "c1"));

        game.executeAction(move("f8", "g8"));
        game.executeAction(move("c1", "b1"));

        game.executeAction(move("g8", "h8"));
        game.executeAction(move("b1", "a1"));

        game.executeAction(move("h8", "h7"));
        game.executeAction(move("a1", "a2"));

        game.executeAction(move("h7", "h6"));
        game.executeAction(move("a2", "a3"));

        game.executeAction(move("h6", "h5"));
        game.executeAction(move("a3", "a4"));

        game.executeAction(move("h5", "h4"));
        game.executeAction(move("a4", "a5"));

        game.executeAction(move("h4", "h3"));
        game.executeAction(move("a5", "a6"));

        game.executeAction(move("h3", "h2"));
        game.executeAction(move("a6", "a7"));

        game.executeAction(move("h2", "h1"));
        game.executeAction(move("a7", "a8"));

        game.executeAction(move("h1", "g1"));
        game.executeAction(move("a8", "b8"));

        game.executeAction(move("g1", "f1"));
        game.executeAction(move("b8", "c8"));

        game.executeAction(move("f1", "e1"));
        game.executeAction(move("c8", "d8"));

        game.executeAction(move("e1", "d1"));
        game.executeAction(move("d8", "e8"));

        game.executeAction(move("d1", "c1"));
        game.executeAction(move("e8", "f8"));

        game.executeAction(move("c1", "b1"));
        game.executeAction(move("f8", "g8"));

        game.executeAction(move("b1", "a1"));
        game.executeAction(move("g8", "h8"));

        game.executeAction(move("a1", "a2"));
        game.executeAction(move("h8", "h7"));

        game.executeAction(move("a2", "a3"));
        game.executeAction(move("h7", "h6"));

        game.executeAction(move("a3", "a4"));
        game.executeAction(move("h6", "h5"));

        game.executeAction(move("a4", "a5"));
        game.executeAction(move("h5", "h4"));

        game.executeAction(move("a5", "a6"));
        game.executeAction(move("h4", "h3"));

        game.executeAction(move("a6", "a7"));
        game.executeAction(move("h3", "h2"));

        game.executeAction(move("a7", "a8"));
        game.executeAction(move("h2", "h1"));

        game.executeAction(move("a8", "b8"));
        game.executeAction(move("h1", "g1"));

        game.executeAction(move("b8", "c8"));
        game.executeAction(move("g1", "f1"));

        game.executeAction(move("c8", "d8"));
        game.executeAction(move("f1", "e1"));

        game.executeAction(move("d8", "e8"));
        game.executeAction(move("e1", "d1"));

        game.executeAction(move("e8", "f8"));
        game.executeAction(move("d1", "c1"));

        game.executeAction(move("f8", "g8"));
        game.executeAction(move("c1", "b1"));

        game.executeAction(move("g8", "h8"));
        game.executeAction(move("b1", "a1"));

        game.executeAction(move("h8", "h7"));
        game.executeAction(move("a1", "a2"));

        game.executeAction(move("h7", "h6"));
        game.executeAction(move("a2", "a3"));

        game.executeAction(move("h6", "h5"));
        game.executeAction(move("a3", "a4"));

        game.executeAction(move("h5", "h4"));
        game.executeAction(move("a4", "a5"));

        game.executeAction(move("h4", "h3"));
        game.executeAction(move("a5", "a6"));

        game.executeAction(move("h3", "h2"));
        game.executeAction(move("a6", "a7"));

        game.executeAction(move("h2", "h1"));
        game.executeAction(move("a7", "a8"));

        // 50 move rule not yet reached
        ActionResult result = game.executeAction(move("h1", "g1"));
        Assertions.assertFalse(result.isDraw());

        // 50 move rule reached
        result = game.executeAction(move("a8", "b8"));
        Assertions.assertTrue(result.isDraw());
        Assertions.assertEquals(DrawType.FIFTY_MOVE_RULE, result.getDrawType());

    }

    @Test
    public void testDrawByFiftyMovesSinceBlackCapture() {

        Board board = Board.emptyBoard();
        board.setPiece(0, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(28, new Piece(Colour.WHITE, PieceType.KNIGHT));
        board.setPiece(36, new Piece(Colour.WHITE, PieceType.KNIGHT));
        board.setPiece(35, new Piece(Colour.WHITE, PieceType.QUEEN));

        board.setPiece(63, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(18, new Piece(Colour.BLACK, PieceType.KNIGHT));

        Game game = Game.fromPosition(board);
        game.setTurn(Colour.BLACK);

        // black knight captures white queen
        game.executeAction(move("c3", "d5"));

        // kings walk about until 50 move rule reached
        game.executeAction(move("a1", "a2"));
        game.executeAction(move("h8", "h7"));

        game.executeAction(move("a2", "a3"));
        game.executeAction(move("h7", "h6"));

        game.executeAction(move("a3", "a4"));
        game.executeAction(move("h6", "h5"));

        game.executeAction(move("a4", "a5"));
        game.executeAction(move("h5", "h4"));

        game.executeAction(move("a5", "a6"));
        game.executeAction(move("h4", "h3"));

        game.executeAction(move("a6", "a7"));
        game.executeAction(move("h3", "h2"));

        game.executeAction(move("a7", "a8"));
        game.executeAction(move("h2", "h1"));

        game.executeAction(move("a8", "b8"));
        game.executeAction(move("h1", "g1"));

        game.executeAction(move("b8", "c8"));
        game.executeAction(move("g1", "f1"));

        game.executeAction(move("c8", "d8"));
        game.executeAction(move("f1", "e1"));

        game.executeAction(move("d8", "e8"));
        game.executeAction(move("e1", "d1"));

        game.executeAction(move("e8", "f8"));
        game.executeAction(move("d1", "c1"));

        game.executeAction(move("f8", "g8"));
        game.executeAction(move("c1", "b1"));

        game.executeAction(move("g8", "h8"));
        game.executeAction(move("b1", "a1"));

        game.executeAction(move("h8", "h7"));
        game.executeAction(move("a1", "a2"));

        game.executeAction(move("h7", "h6"));
        game.executeAction(move("a2", "a3"));

        game.executeAction(move("h6", "h5"));
        game.executeAction(move("a3", "a4"));

        game.executeAction(move("h5", "h4"));
        game.executeAction(move("a4", "a5"));

        game.executeAction(move("h4", "h3"));
        game.executeAction(move("a5", "a6"));

        game.executeAction(move("h3", "h2"));
        game.executeAction(move("a6", "a7"));

        game.executeAction(move("h2", "h1"));
        game.executeAction(move("a7", "a8"));

        game.executeAction(move("h1", "g1"));
        game.executeAction(move("a8", "b8"));

        game.executeAction(move("g1", "f1"));
        game.executeAction(move("b8", "c8"));

        game.executeAction(move("f1", "e1"));
        game.executeAction(move("c8", "d8"));

        game.executeAction(move("e1", "d1"));
        game.executeAction(move("d8", "e8"));

        game.executeAction(move("d1", "c1"));
        game.executeAction(move("e8", "f8"));

        game.executeAction(move("c1", "b1"));
        game.executeAction(move("f8", "g8"));

        game.executeAction(move("b1", "a1"));
        game.executeAction(move("g8", "h8"));

        game.executeAction(move("a1", "a2"));
        game.executeAction(move("h8", "h7"));

        game.executeAction(move("a2", "a3"));
        game.executeAction(move("h7", "h6"));

        game.executeAction(move("a3", "a4"));
        game.executeAction(move("h6", "h5"));

        game.executeAction(move("a4", "a5"));
        game.executeAction(move("h5", "h4"));

        game.executeAction(move("a5", "a6"));
        game.executeAction(move("h4", "h3"));

        game.executeAction(move("a6", "a7"));
        game.executeAction(move("h3", "h2"));

        game.executeAction(move("a7", "a8"));
        game.executeAction(move("h2", "h1"));

        game.executeAction(move("a8", "b8"));
        game.executeAction(move("h1", "g1"));

        game.executeAction(move("b8", "c8"));
        game.executeAction(move("g1", "f1"));

        game.executeAction(move("c8", "d8"));
        game.executeAction(move("f1", "e1"));

        game.executeAction(move("d8", "e8"));
        game.executeAction(move("e1", "d1"));

        game.executeAction(move("e8", "f8"));
        game.executeAction(move("d1", "c1"));

        game.executeAction(move("f8", "g8"));
        game.executeAction(move("c1", "b1"));

        game.executeAction(move("g8", "h8"));
        game.executeAction(move("b1", "a1"));

        game.executeAction(move("h8", "h7"));
        game.executeAction(move("a1", "a2"));

        game.executeAction(move("h7", "h6"));
        game.executeAction(move("a2", "a3"));

        game.executeAction(move("h6", "h5"));
        game.executeAction(move("a3", "a4"));

        game.executeAction(move("h5", "h4"));
        game.executeAction(move("a4", "a5"));

        game.executeAction(move("h4", "h3"));
        game.executeAction(move("a5", "a6"));

        game.executeAction(move("h3", "h2"));
        game.executeAction(move("a6", "a7"));

        game.executeAction(move("h2", "h1"));
        game.executeAction(move("a7", "a8"));

        // 50 move rule not yet reached
        ActionResult result = game.executeAction(move("h1", "g1"));
        Assertions.assertFalse(result.isDraw());

        // 50 move rule reached
        result = game.executeAction(move("a8", "b8"));
        Assertions.assertTrue(result.isDraw());
        Assertions.assertEquals(DrawType.FIFTY_MOVE_RULE, result.getDrawType());

    }

    @Test
    public void testDrawByFiftyMovesSinceBlackPawnMove() {

        Board board = Board.emptyBoard();
        board.setPiece(0, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(28, new Piece(Colour.WHITE, PieceType.PAWN));

        board.setPiece(63, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(52, new Piece(Colour.BLACK, PieceType.PAWN));

        Game game = Game.fromPosition(board);
        game.setTurn(Colour.BLACK);

        // black pawn makes last possible pawn move
        game.executeAction(move("e7", "e5"));

        // kings walk about until 50 move rule reached
        game.executeAction(move("a1", "a2"));
        game.executeAction(move("h8", "h7"));

        game.executeAction(move("a2", "a3"));
        game.executeAction(move("h7", "h6"));

        game.executeAction(move("a3", "a4"));
        game.executeAction(move("h6", "h5"));

        game.executeAction(move("a4", "a5"));
        game.executeAction(move("h5", "h4"));

        game.executeAction(move("a5", "a6"));
        game.executeAction(move("h4", "h3"));

        game.executeAction(move("a6", "a7"));
        game.executeAction(move("h3", "h2"));

        game.executeAction(move("a7", "a8"));
        game.executeAction(move("h2", "h1"));

        game.executeAction(move("a8", "b8"));
        game.executeAction(move("h1", "g1"));

        game.executeAction(move("b8", "c8"));
        game.executeAction(move("g1", "f1"));

        game.executeAction(move("c8", "d8"));
        game.executeAction(move("f1", "e1"));

        game.executeAction(move("d8", "e8"));
        game.executeAction(move("e1", "d1"));

        game.executeAction(move("e8", "f8"));
        game.executeAction(move("d1", "c1"));

        game.executeAction(move("f8", "g8"));
        game.executeAction(move("c1", "b1"));

        game.executeAction(move("g8", "h8"));
        game.executeAction(move("b1", "a1"));

        game.executeAction(move("h8", "h7"));
        game.executeAction(move("a1", "a2"));

        game.executeAction(move("h7", "h6"));
        game.executeAction(move("a2", "a3"));

        game.executeAction(move("h6", "h5"));
        game.executeAction(move("a3", "a4"));

        game.executeAction(move("h5", "h4"));
        game.executeAction(move("a4", "a5"));

        game.executeAction(move("h4", "h3"));
        game.executeAction(move("a5", "a6"));

        game.executeAction(move("h3", "h2"));
        game.executeAction(move("a6", "a7"));

        game.executeAction(move("h2", "h1"));
        game.executeAction(move("a7", "a8"));

        game.executeAction(move("h1", "g1"));
        game.executeAction(move("a8", "b8"));

        game.executeAction(move("g1", "f1"));
        game.executeAction(move("b8", "c8"));

        game.executeAction(move("f1", "e1"));
        game.executeAction(move("c8", "d8"));

        game.executeAction(move("e1", "d1"));
        game.executeAction(move("d8", "e8"));

        game.executeAction(move("d1", "c1"));
        game.executeAction(move("e8", "f8"));

        game.executeAction(move("c1", "b1"));
        game.executeAction(move("f8", "g8"));

        game.executeAction(move("b1", "a1"));
        game.executeAction(move("g8", "h8"));

        game.executeAction(move("a1", "a2"));
        game.executeAction(move("h8", "h7"));

        game.executeAction(move("a2", "a3"));
        game.executeAction(move("h7", "h6"));

        game.executeAction(move("a3", "a4"));
        game.executeAction(move("h6", "h5"));

        game.executeAction(move("a4", "a5"));
        game.executeAction(move("h5", "h4"));

        game.executeAction(move("a5", "a6"));
        game.executeAction(move("h4", "h3"));

        game.executeAction(move("a6", "a7"));
        game.executeAction(move("h3", "h2"));

        game.executeAction(move("a7", "a8"));
        game.executeAction(move("h2", "h1"));

        game.executeAction(move("a8", "b8"));
        game.executeAction(move("h1", "g1"));

        game.executeAction(move("b8", "c8"));
        game.executeAction(move("g1", "f1"));

        game.executeAction(move("c8", "d8"));
        game.executeAction(move("f1", "e1"));

        game.executeAction(move("d8", "e8"));
        game.executeAction(move("e1", "d1"));

        game.executeAction(move("e8", "f8"));
        game.executeAction(move("d1", "c1"));

        game.executeAction(move("f8", "g8"));
        game.executeAction(move("c1", "b1"));

        game.executeAction(move("g8", "h8"));
        game.executeAction(move("b1", "a1"));

        game.executeAction(move("h8", "h7"));
        game.executeAction(move("a1", "a2"));

        game.executeAction(move("h7", "h6"));
        game.executeAction(move("a2", "a3"));

        game.executeAction(move("h6", "h5"));
        game.executeAction(move("a3", "a4"));

        game.executeAction(move("h5", "h4"));
        game.executeAction(move("a4", "a5"));

        game.executeAction(move("h4", "h3"));
        game.executeAction(move("a5", "a6"));

        game.executeAction(move("h3", "h2"));
        game.executeAction(move("a6", "a7"));

        game.executeAction(move("h2", "h1"));
        game.executeAction(move("a7", "a8"));

        // 50 move rule not yet reached
        ActionResult result = game.executeAction(move("h1", "g1"));
        Assertions.assertFalse(result.isDraw());

        // 50 move rule reached
        result = game.executeAction(move("a8", "b8"));
        Assertions.assertTrue(result.isDraw());
        Assertions.assertEquals(DrawType.FIFTY_MOVE_RULE, result.getDrawType());

    }

    private GameAction move(String startSquare, String endSquare) {
        return GameAction.builder()
                .actionType(ActionType.MOVE)
                .move(MoveUtils.fromNotation(startSquare, endSquare))
                .build();
    }

}
