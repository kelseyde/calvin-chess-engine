package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class RootHistoryTable extends AbstractHistoryTable {

    short[][][] table = new short[2][Square.COUNT][Square.COUNT];

    public RootHistoryTable(EngineConfig config) {
        super((short) config.quietHistBonusMax(),
                (short) config.quietHistBonusScale(),
                (short) config.quietHistMalusMax(),
                (short) config.quietHistMalusScale(),
                (short) config.quietHistMaxScore());
    }

    public void update(Move move, int depth, boolean white, boolean good) {
        int colourIndex = Colour.index(white);
        short current = table[colourIndex][move.from()][move.to()];
        short bonus = good ? bonus(depth) : malus(depth);
        short update = gravity(current, bonus);
        table[colourIndex][move.from()][move.to()] = update;
    }

    public short get(Move move, boolean white) {
        int colourIndex = Colour.index(white);
        return table[colourIndex][move.from()][move.to()];
    }

    public void clear() {
        table = new short[2][Square.COUNT][Square.COUNT];
    }

}
