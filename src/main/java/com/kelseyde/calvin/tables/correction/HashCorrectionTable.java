package com.kelseyde.calvin.tables.correction;

import com.kelseyde.calvin.board.Colour;

/**
 * Correction history table indexed by hash key.
 */
public class HashCorrectionTable extends CorrectionHistoryTable {

    private static final int TABLE_SIZE = 16384;

    short[][] entries;

    public HashCorrectionTable() {
        this.entries = new short[2][TABLE_SIZE];
    }

    public void update(long key, boolean white, int depth, int score, int staticEval) {
        int bonus = bonus(score, staticEval, depth);
        int oldValue = get(key, white);
        int newValue = gravity(oldValue, bonus);
        put(key, white, newValue);
    }

    public int get(long key, boolean white) {
        int colourIndex = Colour.index(white);
        int hashIndex = hashIndex(key);
        return entries[colourIndex][hashIndex];
    }

    private void put(long key, boolean white, int value) {
        int colourIndex = Colour.index(white);
        int hashIndex = hashIndex(key);
        entries[colourIndex][hashIndex] = (short) value;
    }

    @Override
    public void clear() {
        this.entries = new short[2][TABLE_SIZE];
    }

    private int hashIndex(long key) {
        // Ensure the key is positive,
        // then return a modulo of the key and table size.
        return (int) (key & 0x7FFFFFFF) % TABLE_SIZE;
    }

}
