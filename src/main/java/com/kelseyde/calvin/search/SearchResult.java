package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;

import java.time.Duration;
import java.time.Instant;

public record SearchResult(int eval, Move move, int depth, long time, int nodes, long nps) {

    public static SearchResult empty() {
        return new SearchResult(0, null, 0, 0, 0, 0);
    }

    public static SearchResult of(Move move, int score, int depth, Instant start, int nodes) {
        long millis = start != null ? Duration.between(start, Instant.now()).toMillis() : 0;
        long nps = nodes > 0 && millis > 0 ? ((nodes / millis) * 1000) : 0;
        return new SearchResult(score, move, depth, millis, nodes, nps);
    }

}
