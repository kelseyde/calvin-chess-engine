package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.search.SearchStack.SearchStackEntry;

public class ContinuationHistoryTable extends AbstractHistoryTable {

    short[][][][][] table = new short[2][Piece.COUNT][Square.COUNT][Piece.COUNT][Square.COUNT];

    public ContinuationHistoryTable(EngineConfig config) {
        super((short) config.contHistBonusMax(),
                (short) config.contHistBonusScale(),
                (short) config.contHistMalusMax(),
                (short) config.contHistMalusScale(),
                (short) config.contHistMaxScore());
    }

    public void update(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, int depth, boolean white, boolean good) {
        short current = get(prevMove, prevPiece, currMove, currPiece, white);
        short bonus = good ? bonus(depth) : malus(depth);
        short update = gravity(current, bonus);
        set(prevMove, prevPiece, currMove, currPiece, update, white);
    }

    public short get(SearchStackEntry prevEntry, Move currMove, Piece currPiece, boolean white) {
        if (prevEntry == null || prevEntry.move == null || prevEntry.piece == null || currMove == null || currPiece == null) {
            return 0;
        }
        int colourIndex = Colour.index(white);
        return table[colourIndex][prevEntry.piece.index()][prevEntry.move.to()][currPiece.index()][currMove.to()];
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
