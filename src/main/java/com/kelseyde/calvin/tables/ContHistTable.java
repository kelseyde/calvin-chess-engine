package com.kelseyde.calvin.tables;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

public class ContHistTable {

    int[][][][] table = new int[6][64][6][64];

    public void add(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, int depth) {
        update(prevMove, prevPiece, currMove, currPiece, depth * depth);
    }

    public void sub(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, int depth) {
        update(prevMove, prevPiece, currMove, currPiece, -depth * depth);
    }

    private void update(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, int update) {
        table[prevPiece.getIndex()][prevMove.getTo()][currPiece.getIndex()][currMove.getTo()] += update;
    }

    public int get(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece) {
        return table[prevPiece.getIndex()][prevMove.getTo()][currPiece.getIndex()][currMove.getTo()];
    }

    // TODO aging?

    public void clear() {
        table = new int[6][64][6][64];
    }

}
