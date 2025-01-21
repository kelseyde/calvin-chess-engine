package com.kelseyde.calvin.search;

import com.kelseyde.calvin.search.SearchHistory.PlayedMove;

import java.util.ArrayList;
import java.util.List;

public class SearchStack {

    private final SearchStackEntry[] stack = new SearchStackEntry[Search.MAX_DEPTH];

    public SearchStack() {
        for (int i = 0; i < Search.MAX_DEPTH; i++) {
            stack[i] = new SearchStackEntry();
        }
    }

    public SearchStackEntry get(int ply) {
        return ply >= 0 && ply < Search.MAX_DEPTH ? stack[ply] : null;
    }

    public void clear() {
        for (int i = 0; i < Search.MAX_DEPTH; i++) {
            stack[i] = new SearchStackEntry();
        }
    }

    public static class SearchStackEntry {
        public int staticEval;
        public PlayedMove currentMove;
        public PlayedMove bestMove;
        public boolean nullMoveAllowed = true;
        public List<PlayedMove> searchedMoves = new ArrayList<>();
    }

}
