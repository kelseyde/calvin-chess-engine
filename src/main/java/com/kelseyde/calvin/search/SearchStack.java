package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.tables.history.CaptureHistoryTable.CaptureMove;

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

    public int getStaticEval(int ply) {
        SearchStackEntry entry = get(ply);
        return entry != null ? entry.staticEval : 0;
    }

    public void setStaticEval(int ply, int score) {
        stack[ply].staticEval = score;
    }

    public Move getMove(int ply) {
        SearchStackEntry entry = get(ply);
        return entry != null ? entry.move : null;
    }

    public Piece getMovedPiece(int ply) {
        SearchStackEntry entry = get(ply);
        return entry != null ? entry.movedPiece : null;
    }

    public void setMove(int ply, Move move, Piece movedPiece) {
        if (ply < 0 || ply >= Search.MAX_DEPTH) {
            return;
        }
        stack[ply].move = move;
        stack[ply].movedPiece = movedPiece;
    }

    public void unsetMove(int ply) {
        if (ply < 0 || ply >= Search.MAX_DEPTH) {
            return;
        }
        stack[ply].move = null;
        stack[ply].movedPiece = null;
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

    public void addQuietMove(int ply, Move move) {
        SearchStackEntry entry = get(ply);
        if (entry != null) {
            entry.quietsSearched.add(move);
        }
    }

    public void addCaptureMove(int ply, Move move, Piece piece, Piece capturedPiece) {
        SearchStackEntry entry = get(ply);
        if (entry != null) {
            entry.capturesSearched.add(new CaptureMove(move, piece, capturedPiece));
        }
    }

    public void setBestCapture(int ply, Move move, Piece piece, Piece capturedPiece) {
        SearchStackEntry entry = get(ply);
        if (entry != null) {
            entry.bestCapture = new CaptureMove(move, piece, capturedPiece);
        }
    }

    public CaptureMove getBestCapture(int ply) {
        SearchStackEntry entry = get(ply);
        return entry != null ? entry.bestCapture : null;
    }

    public void setBestQuiet(int ply, Move move) {
        SearchStackEntry entry = get(ply);
        if (entry != null) {
            entry.bestQuiet = move;
        }
    }

    public Move getBestQuiet(int ply) {
        SearchStackEntry entry = get(ply);
        return entry != null ? entry.bestQuiet : null;
    }

    public List<Move> getQuietsSearched(int ply) {
        SearchStackEntry entry = get(ply);
        return entry != null ? entry.quietsSearched : new ArrayList<>();
    }

    public List<CaptureMove> getCapturesSearched(int ply) {
        SearchStackEntry entry = get(ply);
        return entry != null ? entry.capturesSearched : new ArrayList<>();
    }

    public void resetHistory(int ply) {
        SearchStackEntry entry = get(ply);
        if (entry != null) {
            entry.quietsSearched.clear();
            entry.capturesSearched.clear();
            entry.bestCapture = null;
        }
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
        public boolean nullMoveAllowed = true;

        public CaptureMove bestCapture;
        public Move bestQuiet;
        public List<Move> quietsSearched = new ArrayList<>();
        public List<CaptureMove> capturesSearched = new ArrayList<>();
    }

}
