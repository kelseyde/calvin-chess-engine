package com.kelseyde.calvin.movegen;

import com.kelseyde.calvin.board.Move;

import java.util.Arrays;
import java.util.List;

public class MoveList {

    private static final int INITIAL_CAPACITY = 64;
    private static final int GROWTH_FACTOR = 8;

    private Move[] moves;

    private int capacity;
    private int size;

    public MoveList() {
        this.capacity = INITIAL_CAPACITY;
        this.moves = new Move[capacity];
        this.size = 0;
    }

    public void add(Move move) {
        if (size == capacity) {
            capacity += GROWTH_FACTOR;
            moves = Arrays.copyOf(moves, capacity);
        }
        moves[size++] = move;
    }

    public Move get(int index) {
        return moves[index];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        size = 0;
    }

    public List<Move> toList() {
        return Arrays.asList(moves).subList(0, size);
    }

}
