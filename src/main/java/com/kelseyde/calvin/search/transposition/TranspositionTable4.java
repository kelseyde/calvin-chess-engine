package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;

public class TranspositionTable4 {

    private static final int ENTRY_SIZE_BYTES = 32;

    private static final int TABLE_SIZE_MB = 1024;

    private static final int TABLE_SIZE = calculateTableSize();

    private static final int CHECKMATE_BOUND = 1000000 - 256;

    private TranspositionEntry[] entries;

    private final Board board;

    public TranspositionTable4(Board board) {
        this.board = board;
        entries = new TranspositionEntry[TABLE_SIZE];
    }

    public TranspositionEntry get(int ply) {
        long zobristKey = board.getGameState().getZobristKey();
        int index = getIndex(zobristKey);
        TranspositionEntry entry = entries[index];
        if (entry != null && isMateScore(entry.getScore())) {
            int score = retrieveCorrectedMateScore(entry.getScore(), ply);
            entry = TranspositionEntry.withScore(entry, score);
        }
        return entry;
    }

    public void put(NodeType flag, int depth, int ply, Move move, int score) {
        long zobristKey = board.getGameState().getZobristKey();
        int index = getIndex(zobristKey);
        if (isMateScore(score)) score = calculateCorrectedMateScore(score, ply);
        TranspositionEntry newEntry = TranspositionEntry.of(zobristKey, score, move, flag, depth);
        TranspositionEntry existingEntry = entries[index];
        if (existingEntry == null || (newEntry.key() == existingEntry.key() && newEntry.getDepth() > existingEntry.getDepth())) {
            entries[index] = newEntry;
        }
    }

    public void clear() {
        printStatistics();
        entries = new TranspositionEntry[TABLE_SIZE];
    }

    private int getIndex(long zobristKey) {
        return (int) Math.abs(zobristKey % TABLE_SIZE);
    }

    private boolean isMateScore(int score) {
        return Math.abs(score) >= CHECKMATE_BOUND;
    }

    private int calculateCorrectedMateScore(int score, int plyFromRoot) {
        int sign = score > 0 ? 1 : -1;
        return (score * sign + plyFromRoot) * sign;
    }

    private int retrieveCorrectedMateScore(int score, int plyFromRoot) {
        int sign = score > 0 ? 1 : -1;
        return (score * sign - plyFromRoot) * sign;
    }

    private static int calculateTableSize() {
        int tableSizeBytes = TABLE_SIZE_MB * 1024 * 1024;
        return tableSizeBytes / ENTRY_SIZE_BYTES;
    }

    public void printStatistics() {
//        int total = hits + collisions;
////        System.out.println("hits: " + hits);
////        System.out.println("collisions: " + collisions);
//        float percentage = (float) (collisions / total) * 100;
//        System.out.println("collision percentage: " + percentage);
    }


}
