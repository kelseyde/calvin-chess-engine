package com.kelseyde.calvin.tables;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

public class ContHistTable {

    int[][][][][] table = new int[2][6][64][6][64];

    public void add(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, int depth, boolean white) {
        if (prevMove != null && prevPiece != null && currMove != null && currPiece != null) {
            update(prevMove, prevPiece, currMove, currPiece, depth * depth, white);
        }
    }

    public void sub(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, int depth, boolean white) {
        if (prevMove != null && prevPiece != null && currMove != null && currPiece != null) {
            update(prevMove, prevPiece, currMove, currPiece, -depth * depth, white);
        }
    }

    public int score(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, boolean white) {
        int colourIndex = Board.colourIndex(white);
        if (prevMove != null && prevPiece != null && currMove != null && currPiece != null) {
            return table[colourIndex][prevPiece.getIndex()][prevMove.getTo()][currPiece.getIndex()][currMove.getTo()];
        }
        return 0;
    }

    private void update(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, int update, boolean white) {
        int colourIndex = Board.colourIndex(white);
        if (prevMove != null && prevPiece != null && currMove != null && currPiece != null) {
            table[colourIndex][prevPiece.getIndex()][prevMove.getTo()][currPiece.getIndex()][currMove.getTo()] += update;
        }
    }

    // TODO aging?
    public void ageScores(boolean white) {

    }

    public void clear() {
        table = new int[2][6][64][6][64];
    }

}
