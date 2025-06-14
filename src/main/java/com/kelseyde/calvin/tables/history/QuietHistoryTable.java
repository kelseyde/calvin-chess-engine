package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class QuietHistoryTable extends AbstractHistoryTable {

    short[][][] table = new short[2][Piece.COUNT][Square.COUNT];

    public QuietHistoryTable(EngineConfig config) {
        super(new HistoryBonus(
                        config.quietHistBonusBase(),
                        config.quietHistBonusScale(),
                        config.quietHistBonusMoveMult(),
                        config.quietHistBonusMax()),
              new HistoryBonus(
                        config.quietHistMalusBase(),
                        config.quietHistMalusScale(),
                        config.quietHistMalusMoveMult(),
                        config.quietHistMalusMax()),
                (short) config.quietHistMaxScore());
    }

    public void add(Move move, Piece piece, boolean white, int bonus) {
        int colourIndex = Colour.index(white);
        short current = table[colourIndex][piece.index()][move.to()];
        short update = gravity(current, (short) bonus);
        table[colourIndex][piece.index()][move.to()] = update;
    }

    public short get(Move move, Piece piece, boolean white) {
        int colourIndex = Colour.index(white);
        return table[colourIndex][piece.index()][move.to()];
    }

    public void clear() {
        table = new short[2][Piece.COUNT][Square.COUNT];
    }

}
