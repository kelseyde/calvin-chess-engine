package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class QuietHistoryTable extends AbstractHistoryTable {

    short[][][] table = new short[2][Piece.COUNT][Square.COUNT];

    public QuietHistoryTable(EngineConfig config) {
        super((short) config.quietHistBonusMax(),
                (short) config.quietHistBonusScale(),
                (short) config.quietHistMalusMax(),
                (short) config.quietHistMalusScale(),
                (short) config.quietHistMaxScore());
    }

    public void update(Move move, Piece piece, int depth, boolean white, boolean good) {
        int colourIndex = Colour.index(white);
        short current = table[colourIndex][piece.index()][move.to()];
        short bonus = good ? bonus(depth) : malus(depth);
        short update = gravity(current, bonus);
        table[colourIndex][piece.index()][move.to()] = update;
    }

    public void update(Move move, Piece piece, boolean white, int bonus) {
        int colourIndex = Colour.index(white);
//        short current = table[colourIndex][piece.index()][move.to()];
//        //short update = gravity(current, (short) bonus);
        table[colourIndex][piece.index()][move.to()] += (short) bonus;
    }

    public short get(Move move, Piece piece, boolean white) {
        int colourIndex = Colour.index(white);
        return table[colourIndex][piece.index()][move.to()];
    }

    public void clear() {
        table = new short[2][Piece.COUNT][Square.COUNT];
    }

}
