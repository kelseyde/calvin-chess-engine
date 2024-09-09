package com.kelseyde.calvin.tables;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.tables.history.ContHistTable;
import com.kelseyde.calvin.utils.Notation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContHistTableTest {

    @Test
    public void testNoMatch() {
        ContHistTable contHistTable = new ContHistTable();
        Move prevMove = Notation.fromUCI("e2e4");
        Piece prevPiece = Piece.PAWN;
        Move currMove = Notation.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        assertEquals(0, contHistTable.get(prevMove, prevPiece, currMove, currPiece, true));
    }

    @Test
    public void testAdd() {
        ContHistTable contHistTable = new ContHistTable();
        Move prevMove = Notation.fromUCI("e2e4");
        Piece prevPiece = Piece.PAWN;
        int depth = 8;
        Move currMove = Notation.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        contHistTable.add(prevMove, prevPiece, currMove, currPiece, depth, true);
        assertEquals(1200, contHistTable.get(prevMove, prevPiece, currMove, currPiece, true));
    }

    @Test
    public void testAddOppositeColour() {
        ContHistTable contHistTable = new ContHistTable();
        Move prevMove = Notation.fromUCI("e2e4");
        Piece prevPiece = Piece.PAWN;
        int depth = 8;
        Move currMove = Notation.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        contHistTable.add(prevMove, prevPiece, currMove, currPiece, depth, true);
        assertEquals(0, contHistTable.get(prevMove, prevPiece, currMove, currPiece, false));
    }

    @Test
    public void testSub() {
        ContHistTable contHistTable = new ContHistTable();
        Move prevMove = Notation.fromUCI("e2e4");
        Piece prevPiece = Piece.PAWN;
        int depth = 8;
        Move currMove = Notation.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        contHistTable.sub(prevMove, prevPiece, currMove, currPiece, depth, true);
        assertEquals(-1200, contHistTable.get(prevMove, prevPiece, currMove, currPiece, true));
    }

    @Test
    public void testAddWithSearchStack() {
        ContHistTable contHistTable = new ContHistTable();
        SearchStack ss = new SearchStack();
        Move prevMove = Notation.fromUCI("e2e4");
        Piece prevPiece = Piece.PAWN;
        ss.setMove(0, prevMove, prevPiece);
        int depth = 8;
        Move currMove = Notation.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        contHistTable.add(ss.getMove(0), ss.getMovedPiece(0), currMove, currPiece, depth, true);
        assertEquals(1200, contHistTable.get(ss.getMove(0), ss.getMovedPiece(0), currMove, currPiece, true));
    }

    @Test
    public void testAddTwice() {
        ContHistTable contHistTable = new ContHistTable();
        Move prevMove = Notation.fromUCI("e2e4");
        Piece prevPiece = Piece.PAWN;
        int depth = 8;
        Move currMove = Notation.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        contHistTable.add(prevMove, prevPiece, currMove, currPiece, depth, true);
        contHistTable.add(prevMove, prevPiece, currMove, currPiece, depth, true);
        assertEquals(2225, contHistTable.get(prevMove, prevPiece, currMove, currPiece, true));
    }

    @Test
    public void testSetAndGet() {
        ContHistTable contHistTable = new ContHistTable();
        Move prevMove = Notation.fromUCI("e2e4");
        Piece prevPiece = Piece.PAWN;
        Move currMove = Notation.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        contHistTable.set(prevMove, prevPiece, currMove, currPiece, 500, true);
        assertEquals(500, contHistTable.get(prevMove, prevPiece, currMove, currPiece, true));
    }

    @Test
    public void testClear() {
        ContHistTable contHistTable = new ContHistTable();
        Move prevMove = Notation.fromUCI("e2e4");
        Piece prevPiece = Piece.PAWN;
        Move currMove = Notation.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        int depth = 8;
        contHistTable.add(prevMove, prevPiece, currMove, currPiece, depth, true);
        contHistTable.clear();
        assertEquals(0, contHistTable.get(prevMove, prevPiece, currMove, currPiece, true));
    }

    @Test
    public void testAgeScores() {
        ContHistTable contHistTable = new ContHistTable();
        Move prevMove = Notation.fromUCI("e2e4");
        Piece prevPiece = Piece.PAWN;
        Move currMove = Notation.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        int depth = 8;
        contHistTable.add(prevMove, prevPiece, currMove, currPiece, depth, true);
        contHistTable.ageScores(true);
        assertEquals(600, contHistTable.get(prevMove, prevPiece, currMove, currPiece, true));  // 1200 / 2
    }

    @Test
    public void testAddAndSub() {
        ContHistTable contHistTable = new ContHistTable();
        Move prevMove = Notation.fromUCI("e2e4");
        Piece prevPiece = Piece.PAWN;
        Move currMove = Notation.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        int depth = 8;
        contHistTable.add(prevMove, prevPiece, currMove, currPiece, depth, true);
        contHistTable.sub(prevMove, prevPiece, currMove, currPiece, depth, true);
        assertEquals(-175, contHistTable.get(prevMove, prevPiece, currMove, currPiece, true));
    }

    @Test
    public void testNullMoveHandling() {
        ContHistTable contHistTable = new ContHistTable();
        Move currMove = Notation.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        int depth = 8;
        contHistTable.add(null, null, currMove, currPiece, depth, true);
        assertEquals(0, contHistTable.get(null, null, currMove, currPiece, true));
    }

    @Test
    public void testBoundaryValues() {
        ContHistTable contHistTable = new ContHistTable();
        Move prevMove = Notation.fromUCI("a2a3");  // Min move
        Move currMove = Notation.fromUCI("h7h8");  // Max move
        Piece prevPiece = Piece.PAWN;
        Piece currPiece = Piece.QUEEN;
        int depth = 16;
        contHistTable.add(prevMove, prevPiece, currMove, currPiece, depth, true);
        assertEquals(1200, contHistTable.get(prevMove, prevPiece, currMove, currPiece, true));  // Bonus is 2400 for depth 16
    }

}