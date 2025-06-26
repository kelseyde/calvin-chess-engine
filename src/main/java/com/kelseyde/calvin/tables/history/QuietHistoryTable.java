package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class QuietHistoryTable extends AbstractHistoryTable {

    short[][][][][] table = new short[2][Piece.COUNT][Square.COUNT][2][2];

    public QuietHistoryTable(EngineConfig config) {
        super((short) config.quietHistMaxScore());
    }

    public void add(Move move, Piece piece, boolean white, long threats, int bonus) {
        int colourIndex = Colour.index(white);
        int fromThreatened = threatIndex(threats, move.from());
        int toThreatened = threatIndex(threats, move.to());
        short current = table[colourIndex][piece.index()][move.to()][fromThreatened][toThreatened];
        short update = gravity(current, (short) bonus);
        table[colourIndex][piece.index()][move.to()][fromThreatened][toThreatened] = update;
    }

    public short get(Move move, Piece piece, long threats, boolean white) {
        int colourIndex = Colour.index(white);
        int fromThreatened = threatIndex(threats, move.from());
        int toThreatened = threatIndex(threats, move.to());
        return table[colourIndex][piece.index()][move.to()][fromThreatened][toThreatened];
    }

    public void clear() {
        table = new short[2][Piece.COUNT][Square.COUNT][2][2];
    }

    private int threatIndex(long threats, int square) {
        return Bits.contains(threats, square) ? 1 : 0;
    }

}
