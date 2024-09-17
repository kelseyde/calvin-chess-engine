package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import lombok.AllArgsConstructor;
import lombok.Data;

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
        return entry != null && entry.currentMove != null ? entry.currentMove.move : null;
    }

    public Piece getMovedPiece(int ply) {
        SearchStackEntry entry = get(ply);
        return entry != null && entry.currentMove != null ? entry.currentMove.piece : null;
    }

    public void setMove(int ply, Move move, Piece piece, Piece captured, boolean capture, boolean quiet) {
        if (ply < 0 || ply >= Search.MAX_DEPTH) {
            return;
        }
        stack[ply].currentMove = new PlayedMove(move, piece, captured, capture, quiet);
    }

    public void unsetMove(int ply) {
        if (ply < 0 || ply >= Search.MAX_DEPTH) {
            return;
        }
        stack[ply].currentMove = null;
    }

    public void setBestMove(int ply, Move move, Piece piece, Piece captured, boolean capture, boolean quiet) {
        if (ply < 0 || ply >= Search.MAX_DEPTH) {
            return;
        }
        stack[ply].bestMove = new PlayedMove(move, piece, captured, capture, quiet);
    }

    public PlayedMove getBestMove(int ply) {
        SearchStackEntry entry = get(ply);
        return entry != null ? entry.bestMove : null;
    }

    public void setNullMoveAllowed(int ply, boolean nullAllowed) {
        if (ply < 0 || ply >= Search.MAX_DEPTH) {
            return;
        }
        stack[ply].nullMoveAllowed = nullAllowed;
    }

    public boolean isNullMoveAllowed(int ply) {
        SearchStackEntry entry = get(ply);
        return entry != null && entry.nullMoveAllowed;
    }

    public void setExcludedMove(int ply, Move move) {
        if (ply < 0 || ply >= Search.MAX_DEPTH) {
            return;
        }
        stack[ply].excludedMove = move;
    }

    public Move getExcludedMove(int ply) {
        SearchStackEntry entry = get(ply);
        return entry != null ? entry.excludedMove : null;
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
        public Move excludedMove;
        public boolean nullMoveAllowed = true;
    }

    @Data
    @AllArgsConstructor
    public static class PlayedMove {
        private Move move;
        private Piece piece;
        private Piece captured;
        private boolean capture;
        private boolean quiet;
    }

}
