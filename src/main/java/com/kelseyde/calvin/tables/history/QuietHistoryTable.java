package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class QuietHistoryTable extends AbstractHistoryTable {

    short[][][][] table = new short[2][Piece.COUNT][Square.COUNT][2];

    public QuietHistoryTable(EngineConfig config) {
        super((short) config.quietHistMaxScore());
    }

    public void add(Move move, Piece piece, boolean white, boolean positiveSee, int bonus) {
        int colourIndex = Colour.index(white);
        int seeIndex = positiveSee ? 0 : 1;
        short current = table[colourIndex][piece.index()][move.to()][seeIndex];
        short update = gravity(current, (short) bonus);
        table[colourIndex][piece.index()][move.to()][seeIndex] = update;
    }

    public short get(Move move, Piece piece, boolean white, boolean positiveSee) {
        int colourIndex = Colour.index(white);
        int seeIndex = positiveSee ? 0 : 1;
        return table[colourIndex][piece.index()][move.to()][seeIndex];
    }

    public void clear() {
        table = new short[2][Piece.COUNT][Square.COUNT][2];
    }

}
