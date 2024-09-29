package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CounterMoveTableTest {

    private CounterMoveTable counterMoveTable;

    @BeforeEach
    public void setUp() {
        counterMoveTable = new CounterMoveTable();
    }

    @Test
    public void testAddAndRetrieveCounterMove() {
        Piece pawn = Piece.PAWN;
        Move prevMove = Move.fromUCI("e2e4");
        Move counterMove = Move.fromUCI("e7e5");

        // Add a counter move
        counterMoveTable.add(pawn, prevMove, true, counterMove);

        // Retrieve and check the stored counter move
        assertEquals(counterMove, counterMoveTable.get(pawn, Square.fromNotation("e4"), true));
    }

    @Test
    public void testIsCounterMoveTrue() {
        Piece pawn = Piece.PAWN;
        Move prevMove = Move.fromUCI("e2e4");
        Move counterMove = Move.fromUCI("e7e5");

        // Add a counter move
        counterMoveTable.add(pawn, prevMove, true, counterMove);

        // Assert that isCounterMove returns true for the same move
        assertTrue(counterMoveTable.isCounterMove(pawn, prevMove, true, counterMove));
    }

    @Test
    public void testIsCounterMoveFalseForDifferentMove() {
        Piece pawn = Piece.PAWN;
        Move prevMove = Move.fromUCI("e2e4");
        Move counterMove = Move.fromUCI("e7e5");
        Move differentMove = Move.fromUCI("d7d5");

        // Add a counter move
        counterMoveTable.add(pawn, prevMove, true, counterMove);

        // Assert that isCounterMove returns false for a different move
        assertFalse(counterMoveTable.isCounterMove(pawn, prevMove, true, differentMove));
    }

    @Test
    public void testIsCounterMoveFalseForDifferentPiece() {
        Piece pawn = Piece.PAWN;
        Piece knight = Piece.KNIGHT;
        Move prevMove = Move.fromUCI("e2e4");
        Move counterMove = Move.fromUCI("e7e5");

        // Add a counter move for a pawn
        counterMoveTable.add(pawn, prevMove, true, counterMove);

        // Assert that isCounterMove returns false for a different piece
        assertFalse(counterMoveTable.isCounterMove(knight, prevMove, true, counterMove));
    }

    @Test
    public void testAddAndRetrieveCounterMoveForBlack() {
        Piece bishop = Piece.BISHOP;
        Move prevMove = Move.fromUCI("c1g5");
        Move counterMove = Move.fromUCI("f8e7");

        // Add a counter move for black
        counterMoveTable.add(bishop, prevMove, false, counterMove);

        // Retrieve and check the stored counter move for black
        assertEquals(counterMove, counterMoveTable.get(bishop, Square.fromNotation("g5"), false));
    }

    @Test
    public void testClearTable() {
        Piece rook = Piece.ROOK;
        Move prevMove = Move.fromUCI("h1h3");
        Move counterMove = Move.fromUCI("h8h6");

        // Add a counter move
        counterMoveTable.add(rook, prevMove, true, counterMove);

        // Clear the table
        counterMoveTable.clear();

        // Assert that the counter move is no longer present
        assertNull(counterMoveTable.get(rook, Square.fromNotation("h3"), true));
    }

}