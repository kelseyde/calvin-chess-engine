package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.NotationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

public class TranspositionTableTest {

    private TranspositionTable4 table;

    private Board board;

    @BeforeEach
    public void beforeEach() {
        board = new Board();
        table = new TranspositionTable4(board);
    }

    @Test
    public void testBasicEntry() {

        Board board = FEN.toBoard("3r1r1k/pQ1b2pp/4p1q1/2p1b3/2B2p2/2N1B2P/PPP2PP1/3R1RK1 w - - 0 23");
        long zobristKey = board.getGameState().getZobristKey();
        int depth = 17;
        int score = 548;
        NodeType flag = NodeType.EXACT;
        Move move = NotationUtils.fromNotation("e4", "e5");
        assertEntry(zobristKey, score, move, flag, depth);

    }

    @Test
    public void testNullMoveEntry() {
        Board board = FEN.toBoard("3r1r1k/pQ1b2pp/4p1q1/2p1b3/2B2p2/2N1B2P/PPP2PP1/3R1RK1 w - - 0 23");
        long zobristKey = board.getGameState().getZobristKey();
        int depth = 1;
        int score = 1;
        NodeType flag = NodeType.UPPER_BOUND;
        Move move = null;
        assertEntry(zobristKey, score, move, flag, depth);
    }

    @Test
    public void testCheckmateEntry() {
        Board board = FEN.toBoard("3r1r1k/pQ1b2pp/4p1q1/2p1b3/2B2p2/2N1B2P/PPP2PP1/3R1RK1 w - - 0 23");
        long zobristKey = board.getGameState().getZobristKey();
        int depth = 1;
        int score = 1000000;
        NodeType flag = NodeType.UPPER_BOUND;
        Move move = NotationUtils.fromNotation("e4", "e5");
        assertEntry(zobristKey, score, move, flag, depth);
    }

    @Test
    public void testNegativeCheckmateEntry() {
        Board board = FEN.toBoard("3r1r1k/pQ1b2pp/4p1q1/2p1b3/2B2p2/2N1B2P/PPP2PP1/3R1RK1 w - - 0 23");
        long zobristKey = board.getGameState().getZobristKey();
        int depth = 1;
        int score = -1000000;
        NodeType flag = NodeType.UPPER_BOUND;
        Move move = NotationUtils.fromNotation("e4", "e5");
        assertEntry(zobristKey, score, move, flag, depth);
    }

    @Test
    public void testMaxDepth() {
        Board board = FEN.toBoard("3r1r1k/pQ1b2pp/4p1q1/2p1b3/2B2p2/2N1B2P/PPP2PP1/3R1RK1 w - - 0 23");
        long zobristKey = board.getGameState().getZobristKey();
        int depth = 256;
        int score = -789;
        NodeType flag = NodeType.UPPER_BOUND;
        Move move = null;
        assertEntry(zobristKey, score, move, flag, depth);
    }

    @Test
    public void testPromotionFlag() {
        Board board = FEN.toBoard("3r1r1k/pQ1b2pp/4p1q1/2p1b3/2B2p2/2N1B2P/PPP2PP1/3R1RK1 w - - 0 23");
        long zobristKey = board.getGameState().getZobristKey();
        int depth = 256;
        int score = -789;
        NodeType flag = NodeType.LOWER_BOUND;
        Move move = NotationUtils.fromNotation("e4", "e5", Move.PROMOTE_TO_KNIGHT_FLAG);
        assertEntry(zobristKey, score, move, flag, depth);
    }

    @Test
    public void testSimplePutAndGetExact() {

        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));

        // Do some evaluation on the node at this position.
        NodeType nodeType = NodeType.EXACT;
        Move bestMove = NotationUtils.fromNotation("g1", "f3");
        int eval = 60;
        int depth = 3;
        int ply = 2;

        table.put(nodeType, depth, ply, bestMove, eval);

        // Do some more searching, return to this position

        TranspositionEntry entry = table.get(ply);

        Assertions.assertNotNull(entry);
        Assertions.assertEquals(board.getGameState().getZobristKey(), entry.key());
        Assertions.assertEquals(nodeType, entry.getFlag());
        Assertions.assertEquals(bestMove, entry.getMove());
        Assertions.assertEquals(eval, entry.getScore());
        Assertions.assertEquals(depth, entry.getDepth());

        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));
        nodeType = NodeType.UPPER_BOUND;
        bestMove = NotationUtils.fromNotation("g8", "f6");
        eval = 28666;
        depth = 256;
        table.put(nodeType, depth, ply + 1, bestMove, eval);

        entry = table.get(ply);
        Assertions.assertNotNull(entry);
        Assertions.assertEquals(board.getGameState().getZobristKey(), entry.key());
        Assertions.assertEquals(nodeType, entry.getFlag());
        Assertions.assertEquals(bestMove, entry.getMove());
        Assertions.assertEquals(eval, entry.getScore());
        Assertions.assertEquals(depth, entry.getDepth());

        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));
        nodeType = NodeType.LOWER_BOUND;
        bestMove = null;
        eval = 1000000;
        depth = 10;
        table.put(nodeType, depth, ply + 2, bestMove, eval);

        entry = table.get(ply);
        Assertions.assertNotNull(entry);
        Assertions.assertEquals(board.getGameState().getZobristKey(), entry.key());
        Assertions.assertEquals(nodeType, entry.getFlag());
        Assertions.assertEquals(bestMove, entry.getMove());
        Assertions.assertEquals(eval, entry.getScore());
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
        int ply = 25;

        table.put(nodeType, depth, ply, bestMove, eval);

        board.unmakeMove();
        board.unmakeMove();

        // Do some more searching, return to this position

        TranspositionEntry entry = table.get(ply);
        Assertions.assertNull(entry);

        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        entry = table.get(ply);
        Assertions.assertNull(entry);

        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        entry = table.get(ply);
        Assertions.assertNotNull(entry);

    }

    @Test
    public void testCanStorePromotionFlag() {

        NodeType nodeType = NodeType.EXACT;
        Move bestMove = NotationUtils.fromNotation("g1", "f3", Move.PROMOTE_TO_BISHOP_FLAG);
        int eval = 60;
        int depth = 3;
        int ply = 255;

        table.put(nodeType, depth, ply, bestMove, eval);

        // Do some more searching, return to this position

        TranspositionEntry entry = table.get(ply);

        Assertions.assertNotNull(entry);
        Assertions.assertEquals(board.getGameState().getZobristKey(), entry.key());
        Assertions.assertEquals(nodeType, entry.getFlag());
        Assertions.assertEquals(bestMove, entry.getMove());
        Assertions.assertTrue(entry.getMove() != null && entry.getMove().isPromotion());
        Assertions.assertEquals(Piece.BISHOP, entry.getMove().getPromotionPieceType());
        Assertions.assertEquals(eval, entry.getScore());
        Assertions.assertEquals(depth, entry.getDepth());

    }

    @Test
    public void testStoreCheckmateAtRoot() {

        NodeType nodeType = NodeType.EXACT;
        Move bestMove = NotationUtils.fromNotation("g1", "f3", Move.PROMOTE_TO_QUEEN_FLAG);
        int plyRemaining = 10;
        int plyFromRoot = 0;

        table.put(nodeType, plyRemaining, plyFromRoot, bestMove, 1000000);

        Assertions.assertEquals(1000000, table.get(0).getScore());

        table.put(nodeType, plyRemaining + 1, plyFromRoot, bestMove, -1000000);

        Assertions.assertEquals(-1000000, table.get(0).getScore());

    }

    @Test
    public void testStoreCheckmateAtRootPlusOne() {

        NodeType nodeType = NodeType.EXACT;
        Move bestMove = NotationUtils.fromNotation("g1", "f3", Move.PROMOTE_TO_QUEEN_FLAG);
        int plyRemaining = 10;
        int plyFromRoot = 1;

        table.put(nodeType, plyRemaining, plyFromRoot, bestMove, 1000000);

        Assertions.assertEquals(999999, table.get(0).getScore());

        table.put(nodeType, plyRemaining + 1, plyFromRoot, bestMove, -1000000);

        Assertions.assertEquals(-999999, table.get(0).getScore());

    }

    @Test
    public void testScorePositiveCheckmateThenAccessFromDeeperPly() {

        NodeType nodeType = NodeType.EXACT;
        Move bestMove = NotationUtils.fromNotation("g1", "f3", Move.PROMOTE_TO_QUEEN_FLAG);
        int eval = 1000000;
        int plyRemaining = 10;
        int plyFromRoot = 5;

        table.put(nodeType, plyRemaining, plyFromRoot, bestMove, eval);

        Assertions.assertEquals(1000000, table.get(5).getScore());
        Assertions.assertEquals(999999, table.get(4).getScore());
        Assertions.assertEquals(999998, table.get(3).getScore());
        Assertions.assertEquals(999997, table.get(2).getScore());
        Assertions.assertEquals(999996, table.get(1).getScore());
        Assertions.assertEquals(999995, table.get(0).getScore());
    }

    @Test
    public void testScorePositiveCheckmateThenAccessFromShallowerPly() {

    }

    @Test
    public void testScoreNegativeCheckmateThenAccessFromDeeperPly() {

    }

    @Test
    public void testScoreNegativeCheckmateThenAccessFromShallowerPly() {

    }

    @Test
    public void testDoesNotReplaceEntryWithMoreDepth() {

        long zobrist = board.getGameState().getZobristKey();
        NodeType flag = NodeType.EXACT;
        Move bestMove = NotationUtils.fromNotation("e2", "e4");
        int eval = 60;
        int plyFromRoot = 0;
        int plyRemaining = 12;

        table.put(flag, plyRemaining, plyFromRoot, bestMove, eval);

        flag = NodeType.UPPER_BOUND;
        eval = 70;
        plyRemaining = 11;
        bestMove = NotationUtils.fromNotation("d2", "d4");
        table.put(flag, plyRemaining, plyFromRoot, bestMove, eval);

        assertEntry(zobrist, 60, NotationUtils.fromNotation("e2", "e4"), NodeType.EXACT, 12);

    }

    @Test
    public void testReplacesEntryWithLessDepth() {

        long zobrist = board.getGameState().getZobristKey();
        NodeType flag = NodeType.EXACT;
        Move bestMove = NotationUtils.fromNotation("e2", "e4");
        int eval = 60;
        int plyFromRoot = 0;
        int plyRemaining = 12;

        table.put(flag, plyRemaining, plyFromRoot, bestMove, eval);

        flag = NodeType.UPPER_BOUND;
        eval = 70;
        plyRemaining = 13;
        bestMove = NotationUtils.fromNotation("d2", "d4");
        table.put(flag, plyRemaining, plyFromRoot, bestMove, eval);

        assertEntry(zobrist, 60, bestMove, flag, 13);

    }

    private void assertEntry(long zobrist, int score, Move move, NodeType flag, int depth) {
        TranspositionEntry entry = TranspositionEntry.of(zobrist, score, move, flag, depth);
        Assertions.assertEquals(zobrist, entry.key());
        Assertions.assertEquals(depth, entry.getDepth());
        Assertions.assertEquals(score, entry.getScore());
        Assertions.assertEquals(flag, entry.getFlag());
        Assertions.assertEquals(move, entry.getMove());
    }

}