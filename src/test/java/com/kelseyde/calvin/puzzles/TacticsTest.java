package com.kelseyde.calvin.puzzles;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.search.ParallelSearcher;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.tuning.SearchResult;
import com.kelseyde.calvin.tuning.Searcher2;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.NotationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Disabled
public class TacticsTest {

    private Search search;

    @Test
    public void testSimpleBackRankMateInOne() {

        String fen = "6k1/5ppp/8/8/8/8/8/4Q1K1 w - - 0 1";
        Board board = FEN.toBoard(fen);

        search = new Searcher2(board);

        SearchResult result = search.search(Duration.ofMillis(50));

        Move bestMove = NotationUtils.fromNotation("e1", "e8");
        assertMove(bestMove, result.move());

    }

    @Test
    public void testKnightMateInOne() {

        String fen = "5rk1/pp2b1pp/8/5p1B/2P1p3/2PnR3/PP1r2PP/R1Q3NK b - - 0 1";
        Board board = FEN.toBoard(fen);

        search = new Searcher2(board);

        SearchResult result = search.search(Duration.ofMillis(100));

        Move bestMove = NotationUtils.fromNotation("d3", "f2");
        assertMove(bestMove, result.move());

    }

    @Test
    public void testFreeKnight() {

        String fen = "rn3rk1/pp4pp/2pR1p2/4p3/8/2P1BNP1/PPPRbP1P/6K1 b - - 1 1";
        Board board = FEN.toBoard(fen);

        search = new Searcher2(board);

        SearchResult result = search.search(Duration.ofMillis(200));

        Move bestMove = NotationUtils.fromNotation("e2", "f3");
        assertMove(bestMove, result.move());

    }

    @Test
    public void testBackRankMateInTwo() {

        String fen = "6k1/p1p2ppp/1pP5/3r4/2q2PQP/P5PK/8/R7 w - - 0 2";
        Board board = FEN.toBoard(fen);

        search = new Searcher2(board);

        SearchResult result = search.search(Duration.ofMillis(100));

        Move bestMove = NotationUtils.fromNotation("g4", "c8");
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("d5", "d8"));

        result = search.search(Duration.ofMillis(100));
        bestMove = NotationUtils.fromNotation("c8", "d8");
        assertMove(bestMove, result.move());

    }

    @Test
    public void testSimpleRemoveTheDefender() {

        String fen = "r1b1k2r/1p3ppp/5n2/q2pp3/1P1b4/1QB3P1/4PPBP/RN2K1NR b KQkq - 0 1";
        Board board = FEN.toBoard(fen);

        search = new Searcher2(board);

        SearchResult result = search.search(Duration.ofMillis(500));

        Move bestMove = NotationUtils.fromNotation("d4", "c3");
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("b1", "c3"));

        result = search.search(Duration.ofMillis(100));
        bestMove = NotationUtils.fromNotation("a5", "a1");
        assertMove(bestMove, result.move());

    }

    @Test
    public void testRookSacMateInFour() {

        String fen = "1r3rk1/ppp2ppp/8/3N4/3nP1q1/8/PPP5/1K1R3R w - - 0 24";
        Board board = FEN.toBoard(fen);

        search = new Searcher2(board);

        SearchResult result = search.search(Duration.ofMillis(500));

        Move bestMove = NotationUtils.fromNotation("d5", "e7");
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("g8", "h8"));

        result = search.search(Duration.ofMillis(500));
        bestMove = NotationUtils.fromNotation("h1", "h7");
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("h8", "h7"));

        result = search.search(Duration.ofMillis(500));
        bestMove = NotationUtils.fromNotation("d1", "h1");
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("g4", "h3"));

        result = search.search(Duration.ofMillis(500));
        bestMove = NotationUtils.fromNotation("h1", "h3");
        assertMove(bestMove, result.move());

    }

    @Test
    public void testMateInOneBetterThanWinningQueenWithCheck() {

        String fen = "6k1/5ppq/8/8/8/8/8/1B4KQ w - - 0 1";
        Board board = FEN.toBoard(fen);

        search = new Searcher2(board);

        SearchResult result = search.search(Duration.ofMillis(100));

        Move bestMove = NotationUtils.fromNotation("h1", "a8");
        assertMove(bestMove, result.move());

    }

    @Test
    public void testQueenAndRookMateInThree() {

        String fen = "4r3/2R2pk1/5pPp/8/6q1/2PQ2P1/PP6/2K5 b - - 0 34";
        Board board = FEN.toBoard(fen);

        search = new Searcher2(board);

        SearchResult result = search.search(Duration.ofMillis(500));

        Move bestMove = NotationUtils.fromNotation("e8", "e1");
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("c1", "c2"));

        result = search.search(Duration.ofMillis(500));
        bestMove = NotationUtils.fromNotation("g4", "a4");
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("b2", "b3"));

        result = search.search(Duration.ofMillis(1000));
        bestMove = NotationUtils.fromNotation("a4", "a2");
        assertMove(bestMove, result.move());

    }

    @Test
    public void testRookMatingNetToEnterWinningPawnEndgame() {

        String fen = "7k/R6p/2p5/4K2r/1P6/8/8/8 w - - 1 44";
        Board board = FEN.toBoard(fen);

        search = new Searcher2(board);

        SearchResult result = search.search(Duration.ofMillis(1000));

        Move bestMove = NotationUtils.fromNotation("e5", "f6");
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("h7", "h6"));

        result = search.search(Duration.ofMillis(1000));
        bestMove = NotationUtils.fromNotation("f6", "g6");
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("h5", "g5"));

        result = search.search(Duration.ofMillis(1000));
        bestMove = NotationUtils.fromNotation("g6", "h6");
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("g5", "g4"));

        result = search.search(Duration.ofMillis(1000));
        bestMove = NotationUtils.fromNotation("a7", "a8");
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("g4", "g8"));

        result = search.search(Duration.ofMillis(1000));
        bestMove = NotationUtils.fromNotation("a8", "g8");
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("h8", "g8"));

        result = search.search(Duration.ofMillis(1000));
        bestMove = NotationUtils.fromNotation("h6", "g6");
        assertMove(bestMove, result.move());

    }

    @Test
    public void testSacRookBishopMateInThree() {

        String fen = "8/pp3N2/6Rb/1kp5/4P3/1KP5/PP6/2r5 b - - 4 32";

        Board board = FEN.toBoard(fen);

        search = new Searcher2(board);

        SearchResult result = search.search(Duration.ofMillis(500));

        Move bestMove = NotationUtils.fromNotation("c5", "c4");
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("b3", "a3"));

        result = search.search(Duration.ofMillis(500));

        bestMove = NotationUtils.fromNotation("c1", "c3");
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("b2", "c3"));

        result = search.search(Duration.ofMillis(200));
        bestMove = NotationUtils.fromNotation("h6", "c1");
        assertMove(bestMove, result.move());


    }

    @Test
    public void testIgnoreScaryChecksForQueenRookMate() {

        String fen = "2k2b1r/3Rpppp/2p2q2/2Np4/3P4/4Qb2/r3nPPP/5R1K w - - 8 25";

        Board board = FEN.toBoard(fen);

        search = new Searcher2(board);

        SearchResult result = search.search(Duration.ofMillis(1000));

        Move bestMove = NotationUtils.fromNotation("e3", "b3");
        assertMove(bestMove, result.move());

    }

    private void assertMove(Move expected, Move actual) {
        boolean matches = expected.matches(actual);
        if (!matches) {
            System.out.printf("Expected move %s, Actual move %s%n",
                    NotationUtils.toNotation(expected), NotationUtils.toNotation(actual));
        }
        Assertions.assertTrue(matches);
    }

}
