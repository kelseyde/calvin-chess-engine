package com.kelseyde.calvin.search.moveordering.tables;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.search.moveordering.MoveBonus;

public class KillerTable {

    static final int KILLERS_PER_PLY = 3;
    static final int MAX_KILLER_PLY = 32;
    static final int KILLER_MOVE_ORDER_BONUS = 10000;

    private Move[][] table = new Move[MAX_KILLER_PLY][KILLERS_PER_PLY];

    /**
     * Adds a new killer move for a given ply.
     *
     * @param ply The current ply from root.
     * @param move The new killer move to be added.
     */
    public void add(int ply, Move move) {
        if (ply >= MAX_KILLER_PLY) {
            return;
        }
        // Check if the move already exists in the killer moves list
        for (int i = 0; i < KILLERS_PER_PLY; i++) {
            if (move.equals(table[ply][i])) {
                // Move the existing killer to the front
                for (int j = i; j > 0; j--) {
                    table[ply][j] = table[ply][j - 1];
                }
                table[ply][0] = move;
                return;
            }
        }

        // If the move is not already a killer, add it to the front and shift others
        for (int i = KILLERS_PER_PLY - 1; i > 0; i--) {
            table[ply][i] = table[ply][i - 1];
        }
        table[ply][0] = move;
    }

    public int getScore(Move move, int ply) {
        if (ply >= MAX_KILLER_PLY) {
            return 0;
        }
        else if (move.equals(table[ply][0])) {
            return MoveBonus.KILLER_MOVE_BIAS + KILLER_MOVE_ORDER_BONUS * 3;
        }
        else if (move.equals(table[ply][1])) {
            return MoveBonus.KILLER_MOVE_BIAS + KILLER_MOVE_ORDER_BONUS * 2;
        }
        else if (move.equals(table[ply][2])) {
            return MoveBonus.KILLER_MOVE_BIAS + KILLER_MOVE_ORDER_BONUS;
        }
        else {
            return 0;
        }
    }

    public void clear() {
        table = new Move[MAX_KILLER_PLY][KILLERS_PER_PLY];
    }

}
