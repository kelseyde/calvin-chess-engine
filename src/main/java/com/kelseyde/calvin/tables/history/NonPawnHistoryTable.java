package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class NonPawnHistoryTable extends AbstractHistoryTable {

    private final int tableSize;
    private final short[][][][] table;

    public NonPawnHistoryTable(EngineConfig config) {
        super((short) config.nonPawnHistBonusMax(),
                (short) config.nonPawnHistBonusScale(),
                (short) config.nonPawnHistMalusMax(),
                (short) config.nonPawnHistMalusScale(),
                (short) config.nonPawnHistMaxScore());
        tableSize = config.nonPawnHistTableSize();
        table = new short[2][tableSize][Piece.COUNT][Square.COUNT];
    }

    public void update(long key, Piece piece, int to, int depth, boolean white, boolean good) {
        int hashIndex = hashIndex(key);
        int colourIndex = Colour.index(white);
        int pieceIndex = piece.index();
        short current = table[colourIndex][hashIndex][pieceIndex][to];
        short bonus = good ? bonus(depth) : malus(depth);
        short update = gravity(current, bonus);
        table[colourIndex][hashIndex][pieceIndex][to] = update;
    }

    public int get(long key, Piece piece, int to, boolean white) {
        int hashIndex = hashIndex(key);
        int colourIndex = Colour.index(white);
        int pieceIndex = piece.index();
        return table[colourIndex][hashIndex][pieceIndex][to];
    }

    private int hashIndex(long key) {
        // Ensure the key is positive,
        // then return a modulo of the key and table size.
        return (int) (key & 0x7FFFFFFF) % tableSize;
    }

}
