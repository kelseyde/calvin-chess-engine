package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class QuietHistoryTable extends AbstractHistoryTable {

    short[][][][][] table = new short[2][Square.COUNT][Square.COUNT][2][2];

    public QuietHistoryTable(EngineConfig config) {
        super((short) config.quietHistBonusMax(),
                (short) config.quietHistBonusScale(),
                (short) config.quietHistMalusMax(),
                (short) config.quietHistMalusScale(),
                (short) config.quietHistMaxScore());
    }

    public void update(Move move, int depth, boolean white, boolean good, long threats) {
        int colourIndex = Colour.index(white);
        int fromThreatIndex = threatIndex(threats, move.from());
        int toThreatIndex = threatIndex(threats, move.to());
        short current = table[colourIndex][move.from()][move.to()][fromThreatIndex][toThreatIndex];
        short bonus = good ? bonus(depth) : malus(depth);
        short update = gravity(current, bonus);
        table[colourIndex][move.from()][move.to()][fromThreatIndex][toThreatIndex] = update;
    }

    public short get(Move move, boolean white, long threats) {
        int colourIndex = Colour.index(white);
        int fromThreatIndex = threatIndex(threats, move.from());
        int toThreatIndex = threatIndex(threats, move.to());
        return table[colourIndex][move.from()][move.to()][fromThreatIndex][toThreatIndex];
    }

    public void clear() {
        table = new short[2][Square.COUNT][Square.COUNT][2][2];
    }

    private int threatIndex(long threats, int square) {
        return Bits.contains(threats, square) ? 1 : 0;
    }

}
