package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Board;

public class CorrectionHistoryTable {

    public static final int GRAIN = 256;
    public static final int SCALE = 256;
    public static final int MAX = GRAIN * 32;

    static final int TABLE_SIZE = 16384;

    int[][] entries;

    public CorrectionHistoryTable() {
        this.entries = new int[2][TABLE_SIZE];
    }

    public void updateCorrectionHistory(long pawnHash, boolean white, int depth, int diff) {
        int colourIndex = Board.colourIndex(white);
        int pawnIndex = getIndex(pawnHash);
        int entry = entries[colourIndex][pawnIndex];
        int scaled = diff * GRAIN;
        int weight = Math.min(16, depth + 1);
        int update = entry * (SCALE - weight) + scaled * weight;
        entry = clamp(update / SCALE, -MAX, MAX);
        entries[colourIndex][pawnIndex] = entry;
    }

    public int correctEvaluation(long pawnHash, boolean white, int rawEval) {
        int colourIndex = Board.colourIndex(white);
        int pawnIndex = getIndex(pawnHash);
        int entry = entries[colourIndex][pawnIndex];
        return rawEval + entry / GRAIN;
    }

    public void clear() {
        this.entries = new int[2][TABLE_SIZE];
    }

    private int getIndex(long pawnZobrist) {
        // XOR the upper and lower halves of the zobrist key together, producing a pseudo-random 32-bit result.
        // Then apply a mask ensuring the number is always positive, since it is to be used as an array index.
        long index = (pawnZobrist ^ (pawnZobrist >>> 32)) & 0x7FFFFFFF;
        // Modulo the result with the number of entries in the table to get the index within bounds.
        return (int) (index % TABLE_SIZE);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

}
