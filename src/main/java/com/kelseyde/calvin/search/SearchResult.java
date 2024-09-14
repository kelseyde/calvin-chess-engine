package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;

import java.time.Duration;
import java.time.Instant;

public record SearchResult(int eval, Move move, int depth, long time, int nodes, long nps) {

    public static SearchResult empty() {
        return new SearchResult(0, null, 0, 0, 0, 0);
    }

    public static SearchResult of(Move move, int score, ThreadData td) {
        long millis = td.start != null ? Duration.between(td.start, Instant.now()).toMillis() : 0;
        long nps = td.nodes > 0 && millis > 0 ? ((td.nodes / millis) * 1000) : 0;
        return new SearchResult(score, move, td.depth, millis, td.nodes, nps);
    }

}
