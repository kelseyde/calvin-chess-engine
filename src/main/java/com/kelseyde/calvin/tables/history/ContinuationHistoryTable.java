package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class ContinuationHistoryTable extends AbstractHistoryTable {

    short[][][][][] table = new short[2][Piece.COUNT][Square.COUNT][Piece.COUNT][Square.COUNT];

    public ContinuationHistoryTable(EngineConfig config) {
        super((short) config.contHistMaxScore());
    }

    public void add(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, boolean white, int bonus) {
        short current = get(prevMove, prevPiece, currMove, currPiece, white);
        short update = gravity(current, (short) bonus);
        set(prevMove, prevPiece, currMove, currPiece, update, white);
    }

    public short get(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, boolean white) {
        if (prevMove == null || prevPiece == null || currMove == null || currPiece == null) {
            return 0;
        }
        int colourIndex = Colour.index(white);
        return table[colourIndex][prevPiece.index()][prevMove.to()][currPiece.index()][currMove.to()];
    }

    public void set(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, short update, boolean white) {
        if (prevMove == null || prevPiece == null || currMove == null || currPiece == null) {
            return;
        }
        int colourIndex = Colour.index(white);
        table[colourIndex][prevPiece.index()][prevMove.to()][currPiece.index()][currMove.to()] = update;
    }

    public void clear() {
        table = new short[2][Piece.COUNT][Square.COUNT][Piece.COUNT][Square.COUNT];
    }

}
