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

    public int getStaticEval(int ply) {
        SearchStackEntry entry = get(ply);
        return entry != null ? entry.staticEval : 0;
    }

    public void setStaticEval(int ply, int score) {
        stack[ply].staticEval = score;
    }

    public Move getMove(int ply) {
        SearchStackEntry entry = get(ply);
        return entry != null ? entry.playedMove : null;
    }

    public Piece getMovedPiece(int ply) {
        SearchStackEntry entry = get(ply);
        return entry != null ? entry.movedPiece : null;
    }

    public void setMove(int ply, Move move, Piece movedPiece) {
        if (ply < 0 || ply >= Search.MAX_DEPTH) {
            return;
        }
        stack[ply].playedMove = move;
        stack[ply].movedPiece = movedPiece;
    }

    public Move getExcludedMove(int ply) {
        SearchStackEntry entry = get(ply);
        return entry != null ? entry.excludedMove : null;
    }

    public void setExcludedMove(int ply, Move move) {
        if (ply < 0 || ply >= Search.MAX_DEPTH) {
            return;
        }
        stack[ply].excludedMove = move;
    }

    public void clear() {
        for (int i = 0; i < Search.MAX_DEPTH; i++) {
            stack[i] = new SearchStackEntry();
        }
    }

    public static class SearchStackEntry {
        public int staticEval;
        public Move playedMove;
        public Move excludedMove;
        public Piece movedPiece;
    }

}
