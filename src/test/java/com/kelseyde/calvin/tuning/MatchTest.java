package com.kelseyde.calvin.tuning;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.engine.Engine;
import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.tuning.copy.Searcher2;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class MatchTest {

    @Test
    public void testMatch() {

        Engine engine1 = new Engine(TestUtils.TEST_CONFIG);
        Engine engine2 = new Engine(TestUtils.TEST_CONFIG);

        engine1.setSearch(new Searcher(TestUtils.TEST_CONFIG, new Board()));
        engine2.setSearch(new Searcher2(TestUtils.TEST_CONFIG, new Board()));

        MatchConfig config = MatchConfig.builder()
                .player1(() -> new Player("player1", engine1))
                .player2(() -> new Player("player2", engine2))
                .gameCount(1000)
                .maxMoves(100)
                .minThinkTimeMs(15)
                .maxThinkTimeMs(40)
                .threadCount(9)
                .build();

        MatchManager match = new MatchManager(config);

        match.run();

    }

}