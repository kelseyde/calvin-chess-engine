package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Move;

import java.util.Arrays;
import java.util.Objects;

public class TranspositionTable {

    private static final int ENTRY_SIZE_BYTES = 32;

    private static final int TABLE_SIZE_MB = 2048;

    private static final int TABLE_SIZE = calculateTableSize();

    private static final int CHECKMATE_BOUND = 1000000 - 256;

    private TranspositionEntry[] entries;

    private int tries;
    private int hits;

    public TranspositionTable() {
        entries = new TranspositionEntry[TABLE_SIZE];
        tries = 0;
        hits = 0;
    }

    public TranspositionEntry get(long zobristKey, int ply) {
        int index = getIndex(zobristKey);
        tries++;
        for (int i = 0; i < 4; i++) {
            TranspositionEntry entry = entries[index + i];
            if (entry != null && entry.key() == zobristKey) {
                hits++;
                if (isMateScore(entry.getScore())) {
                    int score = retrieveMateScore(entry.getScore(), ply);
                    entry = TranspositionEntry.withScore(entry, score);
                }
                return entry;
            }
        }
        return null;
    }

    public void put(long zobristKey, NodeType flag, int depth, int ply, Move move, int score) {
        int startIndex = getIndex(zobristKey);
        if (isMateScore(score)) score = calculateMateScore(score, ply);
        TranspositionEntry newEntry = TranspositionEntry.of(zobristKey, score, move, flag, depth);

        int replacedMinDepth = Integer.MAX_VALUE;
        int replacedIndex = -1;

        for (int i = startIndex; i < startIndex + 4; i++) {

            TranspositionEntry storedEntry = entries[i];
            // If there is an empty entry in the bucket, use that immediately
            if (storedEntry == null) {
                replacedIndex = i;
                break;
            }
            // If the stored entry matches the new entry, don't bother overwriting it
            if (storedEntry.equals(newEntry)) {
                return;
            }

            int storedDepth = storedEntry.getDepth();

            if (storedEntry.key() == zobristKey) {
                if (depth >= storedDepth) {
                    replacedMinDepth = storedDepth;
                    replacedIndex = i;
                    break;
                } else {
                    return;
                }
            }

            // :eep the lowest depth and its index
            if (storedDepth < replacedMinDepth) {
                replacedMinDepth = storedDepth;
                replacedIndex = i;
            }
        }

        entries[replacedIndex] = newEntry;

    }

    public void clear() {
//        printStatistics();
        entries = new TranspositionEntry[TABLE_SIZE];
    }

    /**
     * The 64-bit zobrist key is too large to use as an index in a hashtable, due to memory constraints.
     * Therefore, we take the modulo of the zobrist and the table size, giving us a more manageable number.
     * Collisions can occur though, so we store the full key inside the entry to check we have the correct position.
     */
    private int getIndex(long zobristKey) {
        long index = (int) (zobristKey ^ (zobristKey >>> 32));
        if (index < 0) {
            index = -index;
        }
        index = index % (TABLE_SIZE - 3);
        index = 4 * (index / 4);
        return (int) index;
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
        long fill = Arrays.stream(entries).filter(Objects::nonNull).count();
        float fillPercentage = ((float) TABLE_SIZE / (float) fill) * 100;
        float hitPercentage = ((float) hits / (float) tries) * 100;
        System.out.printf("table size: %s / %s (%s), tries: %s, hits: %s (%s)%n", fill, TABLE_SIZE, fillPercentage, tries, hits, hitPercentage);
    }


}
