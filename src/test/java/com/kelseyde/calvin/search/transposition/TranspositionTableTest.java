package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TranspositionTableTest {

    private TranspositionTable table;

    private Board board;

    @BeforeEach
    public void beforeEach() {
        board = new Board();
        table = new TranspositionTable(board);
    }

    @Test
    public void testSimplePutAndGet() {

        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));

        // Do some evaluation on the node at this position.
        NodeType nodeType = NodeType.EXACT;
        Move bestMove = NotationUtils.fromNotation("g1", "f3");
        int eval = 60;
        int depth = 3;

        table.put(nodeType, bestMove, depth, eval);

        // Do some more searching, return to this position

        TranspositionEntry entry = table.get();

        Assertions.assertNotNull(entry);
        Assertions.assertEquals(board.getGameState().getZobristKey(), entry.getZobristKey());
        Assertions.assertEquals(nodeType, entry.getType());
        Assertions.assertEquals(bestMove, entry.getBestMove());
        Assertions.assertEquals(eval, entry.getValue());
        Assertions.assertEquals(depth, entry.getDepth());

    }

    @Test
    public void testSimplePutAndGetNotFound() {

        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));

        // Do some evaluation on the node at this position.
        NodeType nodeType = NodeType.EXACT;
        Move bestMove = NotationUtils.fromNotation("g1", "f3");
        int eval = 60;
        int depth = 3;

        table.put(nodeType, bestMove, depth, eval);

        board.unmakeMove();
        board.unmakeMove();

        // Do some more searching, return to this position

        TranspositionEntry entry = table.get();
        Assertions.assertNull(entry);

        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        entry = table.get();
        Assertions.assertNull(entry);

        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        entry = table.get();
        Assertions.assertNotNull(entry);

    }

}