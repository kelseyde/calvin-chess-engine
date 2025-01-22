package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

/**
 * A history table that stores the history of quiet moves (moves that are neither checks, captures nor promotions).
 * This table is used to improve move ordering for these moves.
 */
public class QuietHistoryTable extends AbstractHistoryTable {

    int[][][] table = new int[2][Piece.COUNT][Square.COUNT];

    public QuietHistoryTable(EngineConfig config) {
        super(config.quietHistBonusMax.value,
                config.quietHistBonusScale.value,
                config.quietHistMalusMax.value,
                config.quietHistMalusScale.value,
                config.quietHistMaxScore.value);
    }

    public void update(Move move, Piece piece, int depth, boolean white, boolean good) {
        int colourIndex = Colour.index(white);
        int current = table[colourIndex][piece.index()][move.to()];
        int bonus = good ? bonus(depth) : malus(depth);
        int update = gravity(current, bonus);
        table[colourIndex][piece.index()][move.to()] = update;
    }

    public int get(Move move, Piece piece, boolean white) {
        int colourIndex = Colour.index(white);
        return table[colourIndex][piece.index()][move.to()];
    }

    public void clear() {
        table = new int[2][Piece.COUNT][Square.COUNT];
    }

}
