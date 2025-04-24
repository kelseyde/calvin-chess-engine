package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.engine.EngineConfig;

public class NullMoveHistoryTable extends AbstractHistoryTable {

    private short[][][] table;

    public NullMoveHistoryTable(EngineConfig config) {
        super((short) config.nullMoveHistBonusMax(),
                (short) config.nullMoveHistBonusScale(),
                (short) config.nullMoveHistMalusMax(),
                (short) config.nullMoveHistMalusScale(),
                (short) config.nullMoveHistMaxScore());
        table = new short[2][Square.COUNT][Square.COUNT];
    }

    public void update(int from, int to, int depth, boolean white, boolean good) {
        int colourIndex = Colour.index(white);
        short current = table[colourIndex][from][to];
        short bonus = good ? bonus(depth) : malus(depth);
        short update = gravity(current, bonus);
        table[colourIndex][from][to] = update;
    }

    public int get(int from, int to, boolean white) {
        int colourIndex = Colour.index(white);
        return table[colourIndex][from][to];
    }

    public void clear() {
        table = new short[2][Square.COUNT][Square.COUNT];
    }

}
