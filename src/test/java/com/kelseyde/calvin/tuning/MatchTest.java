package com.kelseyde.calvin.tuning;

import com.kelseyde.calvin.bot.CalvinBot;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class MatchTest {

    @Test
    public void testMatch() {

        MatchConfig config = MatchConfig.builder()
                .player1(new Player("player1", new CalvinBot()))
                .player2(new Player("player2", new CalvinBot()))
                .gameCount(100)
                .maxMoves(100)
                .minThinkTimeMs(50)
                .maxThinkTimeMs(100)
                .threadCount(1)
                .build();

        Match match = new Match(config);

        match.run();

    }

}