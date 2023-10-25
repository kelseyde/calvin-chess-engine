package com.kelseyde.calvin.tuning;

import com.kelseyde.calvin.bot.CalvinBot;
import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.search.Searcher2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class MatchTest {

    @Test
    public void testMatch() {

        MatchConfig config = MatchConfig.builder()
                .player1(() -> new Player("player1", new CalvinBot(new Searcher())))
                .player2(() -> new Player("player2", new CalvinBot(new Searcher2())))
                .gameCount(100)
                .maxMoves(100)
                .minThinkTimeMs(50)
                .maxThinkTimeMs(100)
                .threadCount(1)
                .build();

        MatchManager match = new MatchManager(config);

        match.run();

    }

}