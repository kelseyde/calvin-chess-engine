package com.kelseyde.calvin.tables;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.search.Score;
import com.kelseyde.calvin.tables.tt.HashEntry;
import com.kelseyde.calvin.tables.tt.HashFlag;
import com.kelseyde.calvin.tables.tt.TranspositionTable;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TranspositionTableTest {

    private TranspositionTable table;

    private Board board;

    private HashEntry entry;

    @BeforeEach
    public void beforeEach() {
        board = Board.from(FEN.STARTPOS);
        table = new TranspositionTable(TestUtils.CONFIG.defaultHashSizeMb);
        entry = new HashEntry();
    }

    @Test
    public void testBasicEntry() {

        Board board = FEN.toBoard("3r1r1k/pQ1b2pp/4p1q1/2p1b3/2B2p2/2N1B2P/PPP2PP1/3R1RK1 w - - 0 23");
        long zobristKey = board.getState().getKey();
        int depth = 17;
        int score = 548;
        int flag = HashFlag.EXACT;
        Move move = Move.fromUCI("e2e4");
        assertEntry(zobristKey, score, move, flag, depth, false);

    }

    @Test
    public void testNullMoveEntry() {
        Board board = FEN.toBoard("3r1r1k/pQ1b2pp/4p1q1/2p1b3/2B2p2/2N1B2P/PPP2PP1/3R1RK1 w - - 0 23");
        long zobristKey = board.getState().getKey();
        int depth = 1;
        int score = 1;
        int flag = HashFlag.UPPER;
        assertEntry(zobristKey, score, null, flag, depth, true);
    }

    @Test
    public void testCheckmateEntry() {
        Board board = FEN.toBoard("3r1r1k/pQ1b2pp/4p1q1/2p1b3/2B2p2/2N1B2P/PPP2PP1/3R1RK1 w - - 0 23");
        long zobristKey = board.getState().getKey();
        int depth = 1;
        int score = Score.MATE;
        int flag = HashFlag.UPPER;
        Move move = Move.fromUCI("e2e4");
        assertEntry(zobristKey, score, move, flag, depth, true);
    }

    @Test
    public void testNegativeCheckmateEntry() {
        Board board = FEN.toBoard("3r1r1k/pQ1b2pp/4p1q1/2p1b3/2B2p2/2N1B2P/PPP2PP1/3R1RK1 w - - 0 23");
        long zobristKey = board.getState().getKey();
        int depth = 1;
        int score = -Score.MATE;
        int flag = HashFlag.UPPER;
        Move move = Move.fromUCI("e2e4");
        assertEntry(zobristKey, score, move, flag, depth, false);
    }

    @Test
    public void testMaxDepth() {
        Board board = FEN.toBoard("3r1r1k/pQ1b2pp/4p1q1/2p1b3/2B2p2/2N1B2P/PPP2PP1/3R1RK1 w - - 0 23");
        long zobristKey = board.getState().getKey();
        int depth = 255;
        int score = -789;
        int flag = HashFlag.UPPER;
        assertEntry(zobristKey, score, null, flag, depth, false);
    }

    @Test
    public void testPromotionFlag() {
        Board board = FEN.toBoard("3r1r1k/pQ1b2pp/4p1q1/2p1b3/2B2p2/2N1B2P/PPP2PP1/3R1RK1 w - - 0 23");
        long zobristKey = board.getState().getKey();
        int depth = 255;
        int score = -789;
        int flag = HashFlag.LOWER;
        Move move = Move.fromUCI("e7e8q");
        assertEntry(zobristKey, score, move, flag, depth, true);
    }

    @Test
    public void testSimplePutAndGetExact() {

        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));

        // Do some evaluation on the node at this position.
        int flag = HashFlag.EXACT;
        Move bestMove = Move.fromUCI("e2e4");
        int eval = 60;
        int depth = 3;
        int ply = 2;

        table.put(board.getState().getKey(), flag, depth, ply, bestMove,  0, eval, true);

        // Do some more searching, return to this position

        table.get(entry, board.getState().getKey(), ply);

        Assertions.assertNotNull(entry);
        Assertions.assertEquals(flag, entry.flag());
        Assertions.assertEquals(bestMove, entry.move());
        Assertions.assertEquals(eval, entry.score());
        Assertions.assertEquals(depth, entry.depth());

        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));
        flag = HashFlag.UPPER;
        bestMove = Move.fromUCI("e2e4");
        eval = 28666;
        depth = 255;
        table.put(board.getState().getKey(), flag, depth, ply + 1, bestMove, 0,  eval, true);

        table.get(entry, board.getState().getKey(), ply);
        Assertions.assertNotNull(entry);
        Assertions.assertEquals(flag, entry.flag());
        Assertions.assertEquals(bestMove, entry.move());
        Assertions.assertEquals(eval, entry.score());
        Assertions.assertEquals(depth, entry.depth());

        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));
        flag = HashFlag.LOWER;
        eval = -Score.MATE;
        depth = 10;
        table.put(board.getState().getKey(), flag, depth, ply + 2, null, 0,  eval, true);

        table.get(entry, board.getState().getKey(), ply);
        Assertions.assertNotNull(entry);
        Assertions.assertEquals(flag, entry.flag());
        Assertions.assertNull(entry.move());
        Assertions.assertEquals(eval + 2, entry.score());
        Assertions.assertEquals(depth, entry.depth());
    }

    @Test
    public void testSimplePutAndGetNotFound() {

        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));

        // Do some evaluation on the node at this position.
        int flag = HashFlag.EXACT;
        Move bestMove = Move.fromUCI("e2e4");
        int eval = 60;
        int depth = 3;
        int ply = 25;

        table.put(board.getState().getKey(), flag, depth, ply, bestMove, 0,  eval, false);

        board.unmakeMove();
        board.unmakeMove();

        // Do some more searching, return to this position

        table.get(entry, board.getState().getKey(), ply);
        Assertions.assertFalse(entry.exists);

        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        table.get(entry, board.getState().getKey(), ply);
        Assertions.assertFalse(entry.exists);

        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        table.get(entry, board.getState().getKey(), ply);
        Assertions.assertTrue(entry.exists);

    }

    @Test
    public void testCanStorePromotionFlag() {

        int flag = HashFlag.EXACT;
        Move bestMove = Move.fromUCI("e7e8b");
        int eval = 60;
        int depth = 3;
        int ply = 255;

        table.put(board.getState().getKey(), flag, depth, ply, bestMove, 0,  eval, false);

        // Do some more searching, return to this position

        table.get(entry, board.getState().getKey(), ply);

        Assertions.assertNotNull(entry);
        Assertions.assertEquals(flag, entry.flag());
        Assertions.assertEquals(bestMove, entry.move());
        Assertions.assertTrue(entry.move() != null && entry.move().isPromotion());
        Assertions.assertEquals(Piece.BISHOP, entry.move().promoPiece());
        Assertions.assertEquals(eval, entry.score());
        Assertions.assertEquals(depth, entry.depth());

    }

    @Test
    public void testStoreCheckmateAtRoot() {

        int flag = HashFlag.EXACT;
        Move bestMove = Move.fromUCI("e7e8b");
        int plyRemaining = 10;
        int plyFromRoot = 0;

        table.put(board.getState().getKey(), flag, plyRemaining, plyFromRoot, bestMove, 0, Score.MATE, true);

        table.get(entry, board.getState().getKey(), 0);
        Assertions.assertEquals(Score.MATE, entry.score());

        table.put(board.getState().getKey(), flag, plyRemaining + 1, plyFromRoot, bestMove, 0, -Score.MATE, false);

        table.get(entry, board.getState().getKey(), 0);
        Assertions.assertEquals(-Score.MATE, entry.score());

    }

    @Test
    public void testStoreCheckmateAtRootPlusOne() {

        int flag = HashFlag.EXACT;
        Move bestMove = Move.fromUCI("e7e8b");
        int plyRemaining = 10;
        int plyFromRoot = 1;

        table.put(board.getState().getKey(), flag, plyRemaining, plyFromRoot, bestMove, 0,  Score.MATE, true);

        table.get(entry, board.getState().getKey(), 0);
        Assertions.assertEquals(Score.MATE - 1, entry.score());

        table.put(board.getState().getKey(), flag, plyRemaining + 1, plyFromRoot, bestMove, 0,  -Score.MATE, false);

        table.get(entry, board.getState().getKey(), 0);
        Assertions.assertEquals(-Score.MATE + 1, entry.score());

    }

    @Test
    public void testScorePositiveCheckmateThenAccessFromDeeperPly() {

        long zobrist = board.getState().getKey();
        int flag = HashFlag.EXACT;
        Move bestMove = Move.fromUCI("e7e8b");
        int score = Score.MATE;
        int plyRemaining = 10;
        int plyFromRoot = 5;

        table.put(board.getState().getKey(), flag, plyRemaining, plyFromRoot, bestMove, 0,  score, true);

        table.get(entry, zobrist, 5);
        Assertions.assertEquals(Score.MATE, entry.score());

        table.get(entry, zobrist, 4);
        Assertions.assertEquals(Score.MATE - 1, entry.score());

        table.get(entry, zobrist, 3);
        Assertions.assertEquals(Score.MATE - 2, entry.score());

        table.get(entry, zobrist, 2);
        Assertions.assertEquals(Score.MATE - 3, entry.score());

        table.get(entry, zobrist, 1);
        Assertions.assertEquals(Score.MATE - 4, entry.score());

        table.get(entry, zobrist, 0);
        Assertions.assertEquals(Score.MATE - 5, entry.score());
    }

    @Test
    public void testDoesNotReplaceEntryWithMoreDepth() {

        long zobrist = board.getState().getKey();
        int flag = HashFlag.EXACT;
        Move bestMove = Move.fromUCI("e2e4");
        int eval = 60;
        int plyFromRoot = 0;
        int plyRemaining = 12;

        table.put(board.getState().getKey(), flag, plyRemaining, plyFromRoot, bestMove, 0,  eval, false);

        flag = HashFlag.UPPER;
        eval = 70;
        plyRemaining = 11;
        bestMove = Move.fromUCI("e2e4");
        table.put(board.getState().getKey(), flag, plyRemaining, plyFromRoot, bestMove, 0,  eval, true);

        assertEntry(zobrist, 60, Move.fromUCI("e2e4"), HashFlag.EXACT, 12, true);

    }

    @Test
    public void testReplacesEntryWithLessDepth() {

        long zobrist = board.getState().getKey();
        int flag = HashFlag.EXACT;
        Move bestMove = Move.fromUCI("e2e4");
        int eval = 60;
        int plyFromRoot = 0;
        int plyRemaining = 12;

        table.put(board.getState().getKey(), flag, plyRemaining, plyFromRoot, bestMove, 0,  eval, false);

        flag = HashFlag.UPPER;
        eval = 70;
        plyRemaining = 13;
        bestMove = Move.fromUCI("e2e4");
        table.put(board.getState().getKey(), flag, plyRemaining, plyFromRoot, bestMove, 0,  eval, true);

        assertEntry(zobrist, 60, bestMove, flag, 13, true);

    }

    @Test
    public void testStoreOnlyStaticEvalInTt() {

        long key = board.key();
        int flag = HashFlag.NONE;
        int depth = 0;
        int ply = 0;
        int eval = 126;
        int score = 0;

        table.put(key, flag, depth, ply, null, eval, score, true);

        table.get(entry, key, ply);

        Assertions.assertNotNull(entry);
        Assertions.assertEquals(HashFlag.NONE, entry.flag());
        Assertions.assertNull(entry.move());
        Assertions.assertEquals(126, entry.staticEval());
        Assertions.assertEquals(0, entry.score());
        Assertions.assertEquals(0, entry.depth());
        Assertions.assertTrue(entry.pv());

    }

    @Test
    public void testTTPV() {

        long key = board.key();

        table.put(key, HashFlag.EXACT, 0, 0, Move.fromUCI("e2e4"), 0, 0, true);
        table.get(entry, key, 0);
        Assertions.assertTrue(entry.pv());

        table.put(key, HashFlag.EXACT, 0, 0, Move.fromUCI("e2e4"), 0, 0, false);
        table.get(entry, key, 0);
        Assertions.assertFalse(entry.pv());

    }

    private void assertEntry(long zobrist, int score, Move move, int flag, int depth, boolean pv) {
        long key = HashEntry.Key.of(zobrist, depth, 0, flag, pv);
        int value = HashEntry.Value.of(score, move);
        HashEntry entry = new HashEntry();
        entry.init(key, value);
        Assertions.assertEquals(depth, entry.depth());
        Assertions.assertEquals(score, entry.score());
        Assertions.assertEquals(flag, entry.flag());
        Assertions.assertEquals(move, entry.move());
        Assertions.assertEquals(pv, entry.pv());
    }

}