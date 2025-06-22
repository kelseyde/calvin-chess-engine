package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

import java.util.Arrays;

public class PawnHistoryTable extends AbstractHistoryTable {

    final int tableSize;
    final short fill;

    final short[][][][] table;

    public PawnHistoryTable(EngineConfig config) {
        super((short) config.pawnHistMaxScore());
        this.tableSize = config.pawnHistTableSize();
        this.fill = (short) config.pawnHistFill();
        this.table = new short[tableSize][2][Piece.COUNT][Square.COUNT];
        fill();
    }

    public short get(long pawnKey, Piece piece, int square, boolean white) {
        int keyIndex = index(pawnKey);
        int colourIndex = Colour.index(white);
        int pieceIndex = piece.index();
        return table[keyIndex][colourIndex][pieceIndex][square];
    }

    public void add(long pawnKey, Piece piece, int square, boolean white, int bonus) {
        int keyIndex = index(pawnKey);
        int colourIndex = Colour.index(white);
        int pieceIndex = piece.index();
        short current = table[keyIndex][colourIndex][pieceIndex][square];
        short update = gravity(current, (short) bonus);
        table[keyIndex][colourIndex][pieceIndex][square] = update;
    }

    public void clear() {
        fill();
    }

    private int index(long pawnKey) {
        // Ensure the key is positive,
        // then return a modulo of the key and table size.
        return (int) (pawnKey & 0x7FFFFFFF) % tableSize;
    }

    private void fill() {
        for (int i = 0; i < tableSize; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < Piece.COUNT; k++) {
                    Arrays.fill(table[i][j][k], fill);
                }
            }
        }
    }

}
