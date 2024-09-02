package com.kelseyde.calvin.tables;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

public class ContHistTable {

    int[][][][] table = new int[6][64][6][64];

    public void add(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, int depth) {
        if (prevMove != null && prevPiece != null && currMove != null && currPiece != null) {
            update(prevMove, prevPiece, currMove, currPiece, depth * depth);
        }
    }

    public void sub(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, int depth) {
        if (prevMove != null && prevPiece != null && currMove != null && currPiece != null) {
            update(prevMove, prevPiece, currMove, currPiece, -depth * depth);
        }
    }

    public int score(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece) {
        if (prevMove != null && prevPiece != null && currMove != null && currPiece != null) {
            return table[prevPiece.getIndex()][prevMove.getTo()][currPiece.getIndex()][currMove.getTo()];
        }
        return 0;
    }

    private void update(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, int update) {
        if (prevMove != null && prevPiece != null && currMove != null && currPiece != null) {
            table[prevPiece.getIndex()][prevMove.getTo()][currPiece.getIndex()][currMove.getTo()] += update;
        }
    }

    // TODO aging?
    public void ageScores(boolean white) {

    }

    public void clear() {
        table = new int[6][64][6][64];
    }

}
