package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Colour;

public class CorrectionHistoryTable {

    public static final int GRAIN = 256;
    public static final int SCALE = 256;
    public static final int MAX = GRAIN * 32;

    static final int TABLE_SIZE = 16384;

    int[][] entries;

    public CorrectionHistoryTable() {
        this.entries = new int[2][TABLE_SIZE];
    }

    public void update(long key, boolean white, int depth, int score, int staticEval) {
        int colourIndex = Colour.index(white);
        int hashIndex = hashIndex(key);
        int entry = entries[colourIndex][hashIndex];

        int scaled = (score - staticEval) * GRAIN;
        int weight = Math.min(depth + 1, 16);
        int update = entry * (SCALE - weight) + scaled * weight;
        entry = clamp(update / SCALE, -MAX, MAX);
        entries[colourIndex][hashIndex] = entry;
    }

    public int get(long key, boolean white) {
        int colourIndex = Colour.index(white);
        int hashIndex = hashIndex(key);
        return entries[colourIndex][hashIndex];
    }

    public int getCorrection(long key, boolean white) {
        return get(key, white) / GRAIN;
    }

    public void clear() {
        this.entries = new int[2][TABLE_SIZE];
    }

    private int hashIndex(long key) {
        // XOR the upper and lower halves of the zobrist key together, producing a pseudo-random 32-bit result.
        // Then apply a mask ensuring the number is always positive, since it is to be used as an array index.
        long index = (key ^ (key >>> 32)) & 0x7FFFFFFF;
        // Modulo the result with the number of entries in the table to get the index within bounds.
        return (int) (index % TABLE_SIZE);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

}
