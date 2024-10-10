package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Colour;

public class CorrectionHistoryTable {

    public static final int SCALE = 256;
    public static final int MAX = SCALE * 32;

    static final int TABLE_SIZE = 16384;

    int[][] entries;

    public CorrectionHistoryTable() {
        this.entries = new int[2][TABLE_SIZE];
    }

    public void update(long key, boolean white, int depth, int score, int staticEval) {
        int colourIndex = Colour.index(white);
        int hashIndex = hashIndex(key);
        int entry = entries[colourIndex][hashIndex];
        int diff = score - staticEval;
        int scaled = diff * SCALE;
        int weight = Math.min(depth + 1, 16);
        int update = (entry * (SCALE - weight) + scaled * weight) / SCALE;
        entry = clamp(update, -MAX, MAX);
        entries[colourIndex][hashIndex] = entry;
    }

    public int correctEvaluation(long pawnHash, boolean white, int staticEval) {
        int colourIndex = Colour.index(white);
        int pawnIndex = hashIndex(pawnHash);
        int entry = entries[colourIndex][pawnIndex];
        return staticEval + entry / SCALE;
    }

    public void ageEntries() {
        for (int i = 0; i < TABLE_SIZE; i++) {
            entries[0][i] = entries[0][i] / 2;
            entries[1][i] = entries[1][i] / 2;
        }
    }

    public void clear() {
        this.entries = new int[2][TABLE_SIZE];
    }

    private int hashIndex(long key) {
        return (int) (key & 0x7FFFFFFF % TABLE_SIZE);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

}
