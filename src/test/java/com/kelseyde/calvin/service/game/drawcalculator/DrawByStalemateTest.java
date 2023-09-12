package com.kelseyde.calvin.service.game.drawcalculator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.game.*;
import com.kelseyde.calvin.utils.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByStalemateTest {

    @Test
    public void testSimpleQueenStalemate() {

        Board board = Board.emptyBoard();
        board.setPiece(56, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(42, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(1, new Piece(Colour.WHITE, PieceType.QUEEN));

        Game game = Game.fromPosition(board);
        ActionResult result = game.executeAction(move("b1", "b6"));

        // king stalemated in the corner
        Assertions.assertTrue(result.isDraw());
        Assertions.assertEquals(DrawType.STALEMATE, result.getDrawType());

    }

    @Test
    public void testSimpleKingAndPawnStalemate() {

        Board board = Board.emptyBoard();
        board.setPiece(60, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(43, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(52, new Piece(Colour.WHITE, PieceType.PAWN));

        Game game = Game.fromPosition(board);
        ActionResult result = game.executeAction(move("d6", "e6"));

        // king stalemated by king and pawn
        Assertions.assertTrue(result.isDraw());
        Assertions.assertEquals(DrawType.STALEMATE, result.getDrawType());

    }

    @Test
    public void testSimpleKingAndBishopStalemate() {

        Board board = Board.emptyBoard();
        board.setPiece(63, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(46, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(47, new Piece(Colour.WHITE, PieceType.PAWN));
        board.setPiece(37, new Piece(Colour.WHITE, PieceType.BISHOP));

        Game game = Game.fromPosition(board);
        ActionResult result = game.executeAction(move("f5", "e6"));

        // king stalemated in the corner
        Assertions.assertTrue(result.isDraw());
        Assertions.assertEquals(DrawType.STALEMATE, result.getDrawType());

    }

    @Test
    public void testStalemateWithPinnedPawn() {

        Board board = Board.emptyBoard();
        board.setPiece(63, new Piece(Colour.BLACK, PieceType.KING));
        board.setPiece(54, new Piece(Colour.BLACK, PieceType.PAWN));
        board.setPiece(38, new Piece(Colour.WHITE, PieceType.KING));
        board.setPiece(47, new Piece(Colour.WHITE, PieceType.PAWN));
        board.setPiece(36, new Piece(Colour.WHITE, PieceType.BISHOP));
        board.setPiece(37, new Piece(Colour.WHITE, PieceType.BISHOP));
        board.setPiece(9, new Piece(Colour.WHITE, PieceType.QUEEN));

        Game game = Game.fromPosition(board);
        ActionResult result = game.executeAction(move("b2", "a2"));

        // even though pawn could pseudo-legally capture on h6 with check, it is pinned, therefore stalemate
        Assertions.assertTrue(result.isDraw());
        Assertions.assertEquals(DrawType.STALEMATE, result.getDrawType());

    }

    private GameAction move(String startSquare, String endSquare) {
        return GameAction.builder()
                .actionType(ActionType.MOVE)
                .move(MoveUtils.fromNotation(startSquare, endSquare))
                .build();
    }

}
