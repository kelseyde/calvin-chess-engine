package com.kelseyde.calvin.puzzles;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.Engine;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.Notation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;

@Disabled
public class EndgameTest {

    private final Engine engine = TestUtils.getEngine();

    private Search searcher;

    @BeforeEach
    public void beforeEach() {
        this.searcher = TestUtils.SEARCHER;
    }

    @Test
    public void testQueenMateInOneBetterThanMateInTwo() {

        String fen = "k7/8/2K5/8/8/8/1Q6/8 w - - 1 1";
        Board board = FEN.toBoard(fen);
        searcher.setPosition(board);

        SearchResult result = searcher.search(Duration.ofMillis(300));

        Move bestMove = Notation.fromNotation("b2", "b7");
        assertMove(bestMove, result.move());

    }

    @Test
    public void testRookVsTwoConnectedPawns() {

        String fen = "8/8/2k5/6KP/6P1/8/3r4/8 b - - 1 46";
        Board board = FEN.toBoard(fen);
        searcher.setPosition(board);
        Move move = searcher.search(Duration.ofSeconds(2)).move();
        System.out.println(Notation.toNotation(move));

    }

    @Test
    public void testZugzwang1() {

        String fen = "8/8/p1p5/1p5p/1P5p/8/PPP2K1p/4R1rk w - - 0 1";
        engine.setPosition(fen, Collections.emptyList());
        Move move = engine.think(3000);
        System.out.println(Notation.toNotation(move));
        Assertions.assertEquals(Notation.fromCombinedNotation("e1f1"), move);

    }

    @Test
    public void testZugzwang2() {

        String fen = "1q1k4/2Rr4/8/2Q3K1/8/8/8/8 w - - 0 1";
        Engine engine = TestUtils.getEngine();
        engine.setPosition(fen, Collections.emptyList());
        Move move = engine.think(3000);
        System.out.println(Notation.toNotation(move));
        Assertions.assertEquals(Notation.fromCombinedNotation("g5h6"), move);

    }

    @Test
    public void testZugzwang3() {

        String fen = "8/6B1/p5p1/Pp4kp/1P5r/5P1Q/4q1PK/8 w - - 0 32";
        Engine engine = TestUtils.getEngine();
        engine.setPosition(fen, Collections.emptyList());
        Move move = engine.think(3000);
        System.out.println(Notation.toNotation(move));
        Assertions.assertEquals(Notation.fromCombinedNotation("h3h4"), move);

    }

    @Test
    public void testZugzwang4() {

        String fen = "8/8/1p1r1k2/p1pPN1p1/P3KnP1/1P6/8/3R4 b - - 0 1";
        Engine engine = TestUtils.getEngine();
        engine.setPosition(fen, Collections.emptyList());
        Move move = engine.think(3000);
        System.out.println(Notation.toNotation(move));
        Assertions.assertEquals(Notation.fromCombinedNotation("f4d5"), move);

    }

    @Test
    public void  testZugzwang5() {

        String fen = "3R4/p5pk/K5np/2p4Q/2P5/8/8/8 w - - 0 1";
        Engine engine = TestUtils.getEngine();
        engine.setPosition(fen, Collections.emptyList());
        Move move = engine.think(3000);
        System.out.println(Notation.toNotation(move));
        Assertions.assertEquals(Notation.fromCombinedNotation("h5f5"), move);

    }

    @Test
    public void  testZugzwang6() {

        String fen = "2k5/2P5/4K3/8/8/8/8/8 w - - 0 1";
        Engine engine = TestUtils.getEngine();
        engine.setPosition(fen, Collections.emptyList());
        Move move = engine.think(500);
        System.out.println(Notation.toNotation(move));
        Assertions.assertEquals(Notation.fromCombinedNotation("e6d6"), move);

    }

    private void assertMove(Move expected, Move actual) {
        boolean matches = expected.matches(actual);
        if (!matches) {
            System.out.printf("Expected move %s, Actual move %s%n",
                    Notation.toNotation(expected), Notation.toNotation(actual));
        }
        Assertions.assertTrue(matches);
    }

}
