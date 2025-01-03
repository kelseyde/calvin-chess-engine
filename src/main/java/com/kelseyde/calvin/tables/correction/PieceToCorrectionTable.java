package com.kelseyde.calvin.tables.correction;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

/**
 * Correction history table indexed by piece and destination square.
 */
public class PieceToCorrectionTable extends CorrectionHistoryTable {

    int[][][] entries;

    public PieceToCorrectionTable() {
        this.entries = new int[2][Piece.COUNT][Square.COUNT];
    }

    public void update(Move prevMove, Piece prevPiece, boolean white, int staticEval, int score, int depth) {
        int oldValue = get(white, prevMove, prevPiece);
        int correction = correction(oldValue, staticEval, score, depth);
        put(white, prevMove, prevPiece, correction);

    }

    public int get(boolean white, Move prevMove, Piece prevPiece) {
        int colourIndex = Colour.index(white);
        int pieceIndex = prevPiece.index();
        int to = prevMove.to();
        return entries[colourIndex][pieceIndex][to];
    }

    private void put(boolean white, Move prevMove, Piece prevPiece, int value) {
        int colourIndex = Colour.index(white);
        int pieceIndex = prevPiece.index();
        int to = prevMove.to();
        entries[colourIndex][pieceIndex][to] = value;
    }

    @Override
    public void clear() {
        this.entries = new int[2][Piece.COUNT][Square.COUNT];
    }

}
