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

    public int getStaticEval(int ply) {
        return stack[ply].staticEval;
    }

    public void setStaticEval(int ply, int score) {
        stack[ply].staticEval = score;
    }

    public Move getMove(int ply) {
        return stack[ply].move;
    }

    public void setMove(int ply, Move move, Piece movedPiece) {
        stack[ply].move = move;
        stack[ply].movedPiece = movedPiece;
    }

    public Piece getMovedPiece(int ply) {
        return stack[ply].movedPiece;
    }

    public void clear() {
        for (int i = 0; i < Search.MAX_DEPTH; i++) {
            stack[i] = new SearchStackEntry();
        }
    }

    public static class SearchStackEntry {
        public int staticEval;
        public Move move;
        public Piece movedPiece;
    }

}
