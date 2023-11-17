package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;

import java.util.Arrays;
import java.util.Objects;

public class TranspositionTable4 {

    private static final int ENTRY_SIZE_BYTES = 32;

    private static final int TABLE_SIZE_MB = 2048;

    private static final int TABLE_SIZE = calculateTableSize();

    private static final int CHECKMATE_BOUND = 1000000 - 256;

    private TranspositionEntry[] entries;

    private final Board board;

    private int tries;
    private int hits;
    private int collisions;

    public TranspositionTable4(Board board) {
        this.board = board;
        entries = new TranspositionEntry[TABLE_SIZE];
        tries = 0;
        hits = 0;
        collisions = 0;
    }

    public TranspositionEntry get(int ply) {
        long zobristKey = board.getGameState().getZobristKey();
        int index = getIndex(zobristKey);
        TranspositionEntry entry = entries[index];
        tries++;
        if (entry != null) {
            hits++;
            if (isMateScore(entry.getScore())) {
                int score = retrieveMateScore(entry.getScore(), ply);
                entry = TranspositionEntry.withScore(entry, score);
            }
        }
        return entry;
    }

    public void put(NodeType flag, int depth, int ply, Move move, int score) {
        long zobristKey = board.getGameState().getZobristKey();
        int index = getIndex(zobristKey);
        if (isMateScore(score)) score = calculateMateScore(score, ply);
        TranspositionEntry newEntry = TranspositionEntry.of(zobristKey, score, move, flag, depth);
        TranspositionEntry existingEntry = entries[index];
        if (existingEntry != null && existingEntry.key() != zobristKey) {
            collisions++;
        }
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

    private int calculateMateScore(int score, int plyFromRoot) {
        return score > 0 ? score - plyFromRoot : score + plyFromRoot;
    }

    private int retrieveMateScore(int score, int plyFromRoot) {
        return score > 0 ? score + plyFromRoot : score - plyFromRoot;
    }

    private static int calculateTableSize() {
        return (TABLE_SIZE_MB / ENTRY_SIZE_BYTES) * 1024 * 1024;
    }

    public void printStatistics() {
        float hitRate = ((float) hits / (float) tries) * 100;
        float collisionRate = ((float) collisions / (float) tries) * 100;
        System.out.printf("New: tries: %s, hits: %s (%s), collisions: %s (%s)%n", tries, hits, hitRate, collisions, collisionRate);
//        System.out.printf("New size: %s%n", Arrays.stream(entries)
//                .filter(Objects::nonNull)
//                .count());
    }


}
