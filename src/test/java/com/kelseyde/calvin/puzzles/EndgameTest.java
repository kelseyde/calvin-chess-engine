package com.kelseyde.calvin.puzzles;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.search.TimeControl;
import com.kelseyde.calvin.uci.command.PositionCommand;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;

import static com.kelseyde.calvin.utils.TestUtils.ENGINE;

@Disabled
public class EndgameTest {

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

        TimeControl tc = new TimeControl(new EngineConfig(), Duration.ofMillis(300), Duration.ofMillis(300), -1, 0, -1);
        SearchResult result = searcher.search(tc);

        Move bestMove = Move.fromUCI("b2b7");
        assertMove(bestMove, result.move());

    }

    @Test
    public void testRookVsTwoConnectedPawns() {

        String fen = "8/8/2k5/6KP/6P1/8/3r4/8 b - - 1 46";
        Board board = FEN.toBoard(fen);
        searcher.setPosition(board);
        TimeControl tc = new TimeControl(new EngineConfig(), Duration.ofMillis(300), Duration.ofMillis(300), -1, 0, -1);
        Move move = searcher.search(tc).move();
        System.out.println(Move.toUCI(move));

    }

    @Test
    public void testZugzwang1() {

        String fen = "8/8/p1p5/1p5p/1P5p/8/PPP2K1p/4R1rk w - - 0 1";
        ENGINE.setPosition(new PositionCommand(fen, Collections.emptyList()));
        Move move = think(3000).move();
        System.out.println(Move.toUCI(move));
        Assertions.assertEquals(Move.fromUCI("e1f1"), move);

    }

    @Test
    public void testZugzwang2() {

        String fen = "1q1k4/2Rr4/8/2Q3K1/8/8/8/8 w - - 0 1";
        ENGINE.setPosition(new PositionCommand(fen, Collections.emptyList()));
        Move move = think(3000).move();
        System.out.println(Move.toUCI(move));
        Assertions.assertEquals(Move.fromUCI("g5h6"), move);

    }

    @Test
    public void testZugzwang3() {

        String fen = "8/6B1/p5p1/Pp4kp/1P5r/5P1Q/4q1PK/8 w - - 0 32";
        ENGINE.setPosition(new PositionCommand(fen, Collections.emptyList()));
        Move move = think(3000).move();
        System.out.println(Move.toUCI(move));
        Assertions.assertEquals(Move.fromUCI("h3h4"), move);

    }

    @Test
    public void testZugzwang4() {

        String fen = "8/8/1p1r1k2/p1pPN1p1/P3KnP1/1P6/8/3R4 b - - 0 1";
        ENGINE.setPosition(new PositionCommand(fen, Collections.emptyList()));
        Move move = think(3000).move();
        System.out.println(Move.toUCI(move));
        Assertions.assertEquals(Move.fromUCI("f4d5"), move);

    }

    @Test
    public void  testZugzwang5() {

        String fen = "3R4/p5pk/K5np/2p4Q/2P5/8/8/8 w - - 0 1";
        ENGINE.setPosition(new PositionCommand(fen, Collections.emptyList()));
        Move move = think(3000).move();
        System.out.println(Move.toUCI(move));
        Assertions.assertEquals(Move.fromUCI("h5f5"), move);

    }

    @Test
    public void  testZugzwang6() {

        String fen = "2k5/2P5/4K3/8/8/8/8/8 w - - 0 1";
        PositionCommand positionCommand = new PositionCommand(fen, Collections.emptyList());
        ENGINE.setPosition(positionCommand);
        Move move = think(500).move();
        System.out.println(Move.toUCI(move));
        Assertions.assertEquals(Move.fromUCI("e6d6"), move);

    }

    private void assertMove(Move expected, Move actual) {
        boolean matches = expected.matches(actual);
        if (!matches) {
            System.out.printf("Expected move %s, Actual move %s%n",
                    Move.toUCI(expected), Move.toUCI(actual));
        }
        Assertions.assertTrue(matches);
    }


    private SearchResult think(int timeout) {
        TimeControl tc = new TimeControl(new EngineConfig(), Duration.ofMillis(timeout), Duration.ofMillis(timeout), -1, -1, -1);
        return ENGINE.think(tc);
    }

}
