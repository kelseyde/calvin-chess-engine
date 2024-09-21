package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.search.Search;

public class KillerTable {

    public static final int KILLERS_PER_PLY = 3;

    Move[][] table = new Move[Search.MAX_DEPTH][KILLERS_PER_PLY];

    public void add(int ply, Move move) {
        if (ply >= Search.MAX_DEPTH) return;

        if (!move.equals(table[ply][0])) {
            // If move is not already the first entry, shift and insert it
            for (int i = KILLERS_PER_PLY - 1; i > 0; i--) {
                table[ply][i] = table[ply][i - 1];
            }
            table[ply][0] = move;
        }
    }

    public int getIndex(Move move, int ply) {
        for (int i = 0; i < KILLERS_PER_PLY; i++) {
            if (move.equals(table[ply][i])) {
                return i;
            }
        }
        return -1;
    }

    public void clear(int ply) {
        if (ply >= Search.MAX_DEPTH) return;
        table[ply] = new Move[KILLERS_PER_PLY];
    }

    public void clear() {
        table = new Move[Search.MAX_DEPTH][KILLERS_PER_PLY];
    }

}
