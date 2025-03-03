package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

public class SearchStack {

    private static final int SIZE = Search.MAX_DEPTH + 6;

    private final SearchStackEntry[] stack = new SearchStackEntry[SIZE];

    public SearchStack() {
        for (int i = 0; i < SIZE; i++) {
            stack[i] = new SearchStackEntry();
        }
    }

    public SearchStackEntry get(int ply) {
        return ply >= 0 && ply < SIZE ? stack[ply] : null;
    }

    public void clear() {
        for (int i = 0; i < SIZE; i++) {
            stack[i] = new SearchStackEntry();
        }
    }

    public static class SearchStackEntry {
        public int staticEval;
        public Move currentMove;
        public Piece currentPiece;
        public Move bestMove;
        public Move excludedMove;
        public Move[] quiets;
        public Move[] captures;
        public boolean nullMoveAllowed = true;
        public int cutoffCount;
    }

}
