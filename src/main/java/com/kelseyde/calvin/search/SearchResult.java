package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;

public record SearchResult(int eval, Move move, int depth, long time, int nodes, long nps) {

    public static SearchResult empty() {
        return new SearchResult(0, null, 0, 0, 0, 0);
    }

}
