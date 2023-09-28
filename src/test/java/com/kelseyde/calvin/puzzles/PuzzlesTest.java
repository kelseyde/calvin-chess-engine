package com.kelseyde.calvin.puzzles;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.search.iterative.IterativeDeepeningSearch;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Disabled
public class PuzzlesTest {

    private IterativeDeepeningSearch search;

    @Test
    public void testSimpleBackRankMateInOne() {

        String fen = "6k1/5ppp/8/8/8/8/8/4Q1K1 w - - 0 1";
        Board board = FEN.fromFEN(fen);

        search = new IterativeDeepeningSearch(board);

        SearchResult result = search.search(Duration.ofMillis(50));

        Move bestMove = NotationUtils.fromNotation("e1", "e8");
        assertMove(bestMove, result.move());

    }

    @Test
    public void testKnightMateInOne() {

        String fen = "5rk1/pp2b1pp/8/5p1B/2P1p3/2PnR3/PP1r2PP/R1Q3NK b - - 0 1";
        Board board = FEN.fromFEN(fen);

        search = new IterativeDeepeningSearch(board);

        SearchResult result = search.search(Duration.ofMillis(100));

        Move bestMove = NotationUtils.fromNotation("d3", "f2");
        assertMove(bestMove, result.move());

    }

    @Test
    public void testFreeKnight() {

        String fen = "rn3rk1/pp4pp/2pR1p2/4p3/8/2P1BNP1/PPPRbP1P/6K1 b - - 1 1";
        Board board = FEN.fromFEN(fen);

        search = new IterativeDeepeningSearch(board);

        SearchResult result = search.search(Duration.ofMillis(500));

        Move bestMove = NotationUtils.fromNotation("e2", "f3");
        assertMove(bestMove, result.move());

    }

    @Test
    public void testBackRankMateInTwo() {

        String fen = "6k1/p1p2ppp/1pP5/3r4/2q2PQP/P5PK/8/R7 w - - 0 2";
        Board board = FEN.fromFEN(fen);

        search = new IterativeDeepeningSearch(board);

        SearchResult result = search.search(Duration.ofMillis(100));

        Move bestMove = NotationUtils.fromNotation("g4", "c8", PieceType.QUEEN);
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("d5", "d8", PieceType.ROOK));

        result = search.search(Duration.ofMillis(100));
        bestMove = NotationUtils.fromNotation("c8", "d8", PieceType.QUEEN);
        assertMove(bestMove, result.move());

    }

    @Test
    public void testSimpleRemoveTheDefender() {

        String fen = "r1b1k2r/1p3ppp/5n2/q2pp3/1P1b4/1QB3P1/4PPBP/RN2K1NR b KQkq - 0 1";
        Board board = FEN.fromFEN(fen);

        search = new IterativeDeepeningSearch(board);

        SearchResult result = search.search(Duration.ofSeconds(2));

        Move bestMove = NotationUtils.fromNotation("d4", "c3", PieceType.BISHOP);
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("b1", "c3", PieceType.KNIGHT));

        result = search.search(Duration.ofMillis(100));
        bestMove = NotationUtils.fromNotation("a5", "a1", PieceType.QUEEN);
        assertMove(bestMove, result.move());

    }

    @Test
    public void testRookSacMateInFour() {

        String fen = "1r3rk1/ppp2ppp/8/3N4/3nP1q1/8/PPP5/1K1R3R w - - 0 24";
        Board board = FEN.fromFEN(fen);

        search = new IterativeDeepeningSearch(board);

        SearchResult result = search.search(Duration.ofMillis(500));

        Move bestMove = NotationUtils.fromNotation("d5", "e7", PieceType.KNIGHT);
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("g8", "h8", PieceType.KING));

        result = search.search(Duration.ofMillis(500));
        bestMove = NotationUtils.fromNotation("h1", "h7", PieceType.ROOK);
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("h8", "h7", PieceType.KING));

        result = search.search(Duration.ofMillis(500));
        bestMove = NotationUtils.fromNotation("d1", "h1", PieceType.ROOK);
        assertMove(bestMove, result.move());

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("g4", "h3", PieceType.QUEEN));

        result = search.search(Duration.ofMillis(500));
        bestMove = NotationUtils.fromNotation("h1", "h3", PieceType.ROOK);
        assertMove(bestMove, result.move());

    }

    @Test
    public void testMateInOneBetterThanWinningQueenWithCheck() {

        String fen = "6k1/5ppq/8/8/8/8/8/1B4KQ w - - 0 1";
        Board board = FEN.fromFEN(fen);

        search = new IterativeDeepeningSearch(board);

        SearchResult result = search.search(Duration.ofMillis(100));

        Move bestMove = NotationUtils.fromNotation("h1", "a8", PieceType.QUEEN);
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
