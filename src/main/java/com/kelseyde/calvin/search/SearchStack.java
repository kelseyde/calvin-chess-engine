package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.search.ordering.ScoredMove;

public class SearchStack {

    private static final int STACK_SIZE = Search.MAX_DEPTH + 8;

    private final SearchStackEntry[] stack = new SearchStackEntry[STACK_SIZE];

    public SearchStack() {
        for (int i = 0; i < STACK_SIZE; i++) {
            stack[i] = new SearchStackEntry();
        }
    }

    public SearchStackEntry get(int ply) {
        return ply >= 0 && ply < STACK_SIZE ? stack[ply] : null;
    }

    public void clear() {
        for (int i = 0; i < STACK_SIZE; i++) {
            stack[i] = new SearchStackEntry();
        }
    }

    public static class SearchStackEntry {
        public int staticEval;
        public ScoredMove move;
        public Piece piece;
        public Piece captured;
        public ScoredMove bestMove;
        public Move excludedMove;
        public ScoredMove[] quiets;
        public ScoredMove[] captures;
        public int reduction;
        public int failHighCount;
        public int pvDistance;
        public boolean quiet;
        public boolean inCheck;
    }

}
