package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

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
        public Move currentMove;
        public Piece currentPiece;
        public Move bestMove;
        public SearchedMove[] quiets;
        public SearchedMove[] captures;
        public boolean nullMoveAllowed = true;
    }

    public static class SearchedMove {
        public Move move;
        public int depth;

        public SearchedMove(Move move, int depth) {
            this.move = move;
            this.depth = depth;
        }
    }

}
