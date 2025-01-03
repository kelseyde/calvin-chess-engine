package com.kelseyde.calvin.tables;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.search.SearchHistory;
import com.kelseyde.calvin.search.SearchStack;
import com.kelseyde.calvin.tables.history.ContinuationHistoryTable;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class ContHistTableTest {

    @Test
    public void testNoMatch() {
        ContinuationHistoryTable contHistTable = new ContinuationHistoryTable(TestUtils.CONFIG);
        Move prevMove = Move.fromUCI("e2e4");
        Piece prevPiece = Piece.PAWN;
        Move currMove = Move.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        assertEquals(0, contHistTable.get(prevMove, prevPiece, currMove, currPiece, true));
    }

    @Test
    public void testAdd() {
        ContinuationHistoryTable contHistTable = new ContinuationHistoryTable(TestUtils.CONFIG);
        Move prevMove = Move.fromUCI("e2e4");
        Piece prevPiece = Piece.PAWN;
        int depth = 8;
        Move currMove = Move.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        contHistTable.update(prevMove, prevPiece, currMove, currPiece, depth, true, true);
        assertEquals(1200, contHistTable.get(prevMove, prevPiece, currMove, currPiece, true));
    }

    @Test
    public void testSub() {
        ContinuationHistoryTable contHistTable = new ContinuationHistoryTable(TestUtils.CONFIG);
        Move prevMove = Move.fromUCI("e2e4");
        Piece prevPiece = Piece.PAWN;
        int depth = 8;
        Move currMove = Move.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        contHistTable.update(prevMove, prevPiece, currMove, currPiece, depth, true, false);
        assertEquals(-1200, contHistTable.get(prevMove, prevPiece, currMove, currPiece, true));
    }

    @Test
    public void testAddWithSearchStack() {
        ContinuationHistoryTable contHistTable = new ContinuationHistoryTable(TestUtils.CONFIG);
        SearchStack ss = new SearchStack();
        Move prevMove = Move.fromUCI("e2e4");
        Piece prevPiece = Piece.PAWN;
        ss.get(0).currentMove = new SearchHistory.PlayedMove(prevMove, prevPiece, null);
        int depth = 8;
        Move currMove = Move.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        contHistTable.update(ss.get(0).currentMove.move(), ss.get(0).currentMove.piece(), currMove, currPiece, depth, true, true);
        assertEquals(1200, contHistTable.get(ss.get(0).currentMove.move(), ss.get(0).currentMove.piece(), currMove, currPiece, true));
    }

    @Test
    public void testAddTwice() {
        ContinuationHistoryTable contHistTable = new ContinuationHistoryTable(TestUtils.CONFIG);
        Move prevMove = Move.fromUCI("e2e4");
        Piece prevPiece = Piece.PAWN;
        int depth = 8;
        Move currMove = Move.fromUCI("d5e4");
        Piece currPiece = Piece.PAWN;
        contHistTable.update(prevMove, prevPiece, currMove, currPiece, depth, true, true);
        contHistTable.update(prevMove, prevPiece, currMove, currPiece, depth, true, true);
        assertEquals(2225, contHistTable.get(prevMove, prevPiece, currMove, currPiece, true));
    }

}