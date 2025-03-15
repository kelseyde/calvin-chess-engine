package com.kelseyde.calvin.movegen;

import com.kelseyde.calvin.board.Move;

public class MoveList {

    private static final int DEFAULT_INITIAL_SIZE = 32;

    private Move[] moves;

    private int moveCount;
    private final boolean resizable;

    public MoveList() {
        this(DEFAULT_INITIAL_SIZE, true);
    }

    public MoveList(int initialSize, boolean resizable) {
        this.moves = new Move[initialSize];
        this.resizable = resizable;
        this.moveCount = 0;
    }

    public void add(Move move) {
        if (moveCount == moves.length) {
            if (!resizable) {
                return;
            }
            Move[] newMoves = new Move[moves.length * 2];
            System.arraycopy(moves, 0, newMoves, 0, moves.length);
            moves = newMoves;
        }
        moves[moveCount++] = move;
    }

    public Move get(int index) {
        if (index < 0 || index >= moveCount) {
            return null;
        }
        return moves[index];
    }

    public int size() {
        return moveCount;
    }

    public void clear() {
        moveCount = 0;
    }

}
