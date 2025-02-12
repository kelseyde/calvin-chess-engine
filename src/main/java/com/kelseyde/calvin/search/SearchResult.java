package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;

import java.time.Duration;
import java.time.Instant;

public record SearchResult(int score, Move move, int depth, int seldepth, long time, int nodes, long nps) {

    public static SearchResult of(Move move) {
        return new SearchResult(0, move, 0, 0, 0, 0, 0);
    }

    public static SearchResult of(Move move, int score, ThreadData td, TimeControl tc) {
        long millis = tc.start() != null ? Duration.between(tc.start(), Instant.now()).toMillis() : 0;
        long nps = td.nodes > 0 && millis > 0 ? ((td.nodes / millis) * 1000) : 0;
        return new SearchResult(score, move, td.depth, td.seldepth, millis, td.nodes, nps);
    }

}
