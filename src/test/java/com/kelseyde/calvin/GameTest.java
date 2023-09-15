package com.kelseyde.calvin;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.Game;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class GameTest {

    private final Piece rook = new Piece(Colour.WHITE, PieceType.ROOK);

    private Game game;

    @BeforeEach
    public void beforeEach() {
        Board board = BoardUtils.emptyBoard();
        game = new Game(board);
    }

    @Test
    public void testFromPositionDoesNotCorruptBoard() {

        assertSinglePieceBoard(0);
        assertSinglePieceBoard(7);
        assertSinglePieceBoard(12);
        assertSinglePieceBoard(18);
        assertSinglePieceBoard(25);
        assertSinglePieceBoard(31);
        assertSinglePieceBoard(38);
        assertSinglePieceBoard(36);
        assertSinglePieceBoard(43);
        assertSinglePieceBoard(54);
        assertSinglePieceBoard(59);
        assertSinglePieceBoard(60);
        assertSinglePieceBoard(63);

    }

    @Test
    public void testBoardHistoryPreservesMoveCounter() {

        Game game = new Game();
        Assertions.assertEquals(1, game.getBoard().getFullMoveCounter());

        game.playMove(move("e2", "e3"));
        Assertions.assertEquals(1, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(1, game.getBoardHistory().peek().getFullMoveCounter());

        game.playMove(move("e7", "e6"));
        Assertions.assertEquals(2, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(1, game.getBoardHistory().peek().getFullMoveCounter());

        game.playMove(move("d2", "d3"));
        Assertions.assertEquals(2, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(2, game.getBoardHistory().peek().getFullMoveCounter());

        game.playMove(move("d7", "d6"));
        Assertions.assertEquals(3, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(2, game.getBoardHistory().peek().getFullMoveCounter());

        game.playMove(move("c2", "c3"));
        Assertions.assertEquals(3, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(3, game.getBoardHistory().peek().getFullMoveCounter());

        game.playMove(move("c7", "c6"));
        Assertions.assertEquals(4, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(3, game.getBoardHistory().peek().getFullMoveCounter());

        game.playMove(move("b2", "b3"));
        Assertions.assertEquals(4, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(4, game.getBoardHistory().peek().getFullMoveCounter());

        game.playMove(move("b7", "b6"));
        Assertions.assertEquals(5, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(4, game.getBoardHistory().peek().getFullMoveCounter());

        game.playMove(move("a2", "a3"));
        Assertions.assertEquals(5, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(5, game.getBoardHistory().peek().getFullMoveCounter());

        game.playMove(move("a7", "a6"));
        Assertions.assertEquals(6, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(5, game.getBoardHistory().peek().getFullMoveCounter());

        game.playMove(move("h2", "h3"));
        Assertions.assertEquals(6, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(6, game.getBoardHistory().peek().getFullMoveCounter());

        game.playMove(move("h7", "h6"));
        Assertions.assertEquals(7, game.getBoard().getFullMoveCounter());
        Assertions.assertEquals(6, game.getBoardHistory().peek().getFullMoveCounter());
    }

    @Test
    public void testBoardHistoryPreservesCastlingRights() {
        Game game = new Game();

        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.WHITE).isKingSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.WHITE).isQueenSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isKingSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isQueenSide());

        game.playMove(move("e2", "e3"));
        game.playMove(move("e7", "e6"));

        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.WHITE).isKingSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.WHITE).isQueenSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isKingSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isQueenSide());

        game.playMove(move("e1", "e2"));

        Assertions.assertFalse(game.getBoard().getCastlingRights().get(Colour.WHITE).isKingSide());
        Assertions.assertFalse(game.getBoard().getCastlingRights().get(Colour.WHITE).isQueenSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isKingSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isQueenSide());

        Assertions.assertTrue(game.getBoardHistory().peek().getCastlingRights().get(Colour.WHITE).isKingSide());
        Assertions.assertTrue(game.getBoardHistory().peek().getCastlingRights().get(Colour.WHITE).isQueenSide());
        Assertions.assertTrue(game.getBoardHistory().peek().getCastlingRights().get(Colour.BLACK).isKingSide());
        Assertions.assertTrue(game.getBoardHistory().peek().getCastlingRights().get(Colour.BLACK).isQueenSide());

        game.playMove(move("f7", "f6"));

        Assertions.assertFalse(game.getBoard().getCastlingRights().get(Colour.WHITE).isKingSide());
        Assertions.assertFalse(game.getBoard().getCastlingRights().get(Colour.WHITE).isQueenSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isKingSide());
        Assertions.assertTrue(game.getBoard().getCastlingRights().get(Colour.BLACK).isQueenSide());

        Assertions.assertFalse(game.getBoardHistory().peek().getCastlingRights().get(Colour.WHITE).isKingSide());
        Assertions.assertFalse(game.getBoardHistory().peek().getCastlingRights().get(Colour.WHITE).isQueenSide());
        Assertions.assertTrue(game.getBoardHistory().peek().getCastlingRights().get(Colour.BLACK).isKingSide());
        Assertions.assertTrue(game.getBoardHistory().peek().getCastlingRights().get(Colour.BLACK).isQueenSide());

    }

    private Move move(String startSquare, String endSquare) {
        return MoveUtils.fromNotation(startSquare, endSquare);
    }

    private void assertSinglePieceBoard(int startSquare) {
        game.getBoard().setPiece(startSquare, rook);
        Assertions.assertEquals(Set.of(startSquare), game.getBoard().getPiecePositions(Colour.WHITE));
        Assertions.assertEquals(Set.of(), game.getBoard().getPiecePositions(Colour.BLACK));
        game.getBoard().unsetPiece(startSquare);
    }

}
