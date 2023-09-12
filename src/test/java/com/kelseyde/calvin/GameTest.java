package com.kelseyde.calvin;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.game.Game;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class GameTest {

    private final Piece rook = new Piece(Colour.WHITE, PieceType.ROOK);

    private Game game;

    @BeforeEach
    public void beforeEach() {
        Board board = Board.emptyBoard();
        game = Game.fromPosition(board);
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

    private void assertSinglePieceBoard(int startSquare) {
        game.getBoard().setPiece(startSquare, rook);
        Assertions.assertEquals(Set.of(startSquare), game.getBoard().getPiecePositions(Colour.WHITE));
        Assertions.assertEquals(Set.of(), game.getBoard().getPiecePositions(Colour.BLACK));
        game.getBoard().unsetPiece(startSquare);
    }

}
