package com.kelseyde.calvin.board;

public class MoveList {

    public static final int MAX_POSSIBLE_MOVES = 218;

    private Move[] moves;
    private int getIndex;
    private int putIndex;

    public MoveList() {
        this.moves = new Move[MAX_POSSIBLE_MOVES];
        this.getIndex = 0;
        this.putIndex = 0;
    }

    public MoveList(Move[] moves) {
        this.moves = moves;
        this.getIndex = 0;
        this.putIndex = 0;
    }

    public void add(Move move) {
        moves[putIndex] = move;
        putIndex++;
    }

    public Move get(int index) {
        return moves[index];
    }

    public void set(int index, Move move) {
        moves[index] = move;
    }

    public Move next() {
        Move move = moves[getIndex];
        getIndex++;
        return move;
    }

    public Move[] getMoves() {
        return moves;
    }

    public boolean hasNext() {
        return moves[getIndex] != null;
    }

    public boolean isEmpty() {
        return putIndex == 0;
    }

    public int size() {
        return putIndex;
    }

}
