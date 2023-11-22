package com.kelseyde.calvin.tuning;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.bot.CalvinBot;
import com.kelseyde.calvin.search.ParallelSearcher;
import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.tuning.copy.CalvinBot2;
import com.kelseyde.calvin.tuning.copy.ParallelSearcher2;
import com.kelseyde.calvin.tuning.copy.Searcher2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class MatchTest {

    @Test
    public void testSingleMatch() {
        MatchConfig config = MatchConfig.builder()
                .player1(() -> new Player("player1", new CalvinBot(new Searcher())))
                .player2(() -> new Player("player2", new CalvinBot(new Searcher2())))
                .gameCount(300)
                .maxMoves(100)
                .minThinkTimeMs(15)
                .maxThinkTimeMs(40)
                .threadCount(9)
                .build();
        MatchResult result = new Match(config).run();
        System.out.println(result);
    }

    @Test
    public void testMatch() {

        MatchConfig config = MatchConfig.builder()
                .player1(() -> new Player("player1", new CalvinBot(new Searcher())))
                .player2(() -> new Player("player2", new CalvinBot(new Searcher2())))
                .gameCount(50)
                .maxMoves(100)
                .minThinkTimeMs(35)
                .maxThinkTimeMs(75)
                .threadCount(1)
                .build();

        MatchManager match = new MatchManager(config);

        match.run();

    }

    @Test
    public void testSlowMatch() {

        MatchConfig config = MatchConfig.builder()
                .player1(() -> new Player("player1", new CalvinBot(new Searcher())))
                .player2(() -> new Player("player2", new CalvinBot(new Searcher2())))
                .gameCount(16)
                .maxMoves(100)
                .minThinkTimeMs(250)
                .maxThinkTimeMs(550)
                .threadCount(1)
                .build();

        MatchManager match = new MatchManager(config);

        match.run();

    }

    @Test
    public void testLongMatch() {

        MatchConfig config = MatchConfig.builder()
                .player1(() -> new Player("player1", new CalvinBot(new Searcher())))
                .player2(() -> new Player("player2", new CalvinBot(new Searcher2())))
                .gameCount(300)
                .maxMoves(100)
                .minThinkTimeMs(15)
                .maxThinkTimeMs(40)
                .threadCount(9)
                .build();

        MatchManager match = new MatchManager(config);

        match.run();

    }

}