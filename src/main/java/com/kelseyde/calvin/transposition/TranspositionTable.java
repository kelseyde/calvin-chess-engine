package com.kelseyde.calvin.transposition;

import com.kelseyde.calvin.board.Move;

import java.util.Arrays;
import java.util.Objects;

public class TranspositionTable {

    private static final int ENTRY_SIZE_BYTES = 32;
    private static final int CHECKMATE_BOUND = 1000000 - 256;

    private final int tableSize;
    private HashEntry[] entries;

    private int tries;
    private int hits;

    public TranspositionTable(int tableSizeMb) {
        this.tableSize = (tableSizeMb / ENTRY_SIZE_BYTES) * 1024 * 1024;
        entries = new HashEntry[tableSize];
    }

    public HashEntry get(long zobristKey, int ply) {
        int index = getIndex(zobristKey);
        tries++;
        for (int i = 0; i < 4; i++) {
            HashEntry entry = entries[index + i];
            if (entry != null && entry.key() == zobristKey) {
                hits++;
                if (isMateScore(entry.getScore())) {
                    int score = retrieveMateScore(entry.getScore(), ply);
                    entry = HashEntry.withScore(entry, score);
                }
                return entry;
            }
        }
        return null;
    }

    public void put(long zobristKey, HashFlag flag, int depth, int ply, Move move, int score) {
        int startIndex = getIndex(zobristKey);
        if (isMateScore(score)) score = calculateMateScore(score, ply);
        HashEntry newEntry = HashEntry.of(zobristKey, score, move, flag, depth);

        int replacedMinDepth = Integer.MAX_VALUE;
        int replacedIndex = -1;

        for (int i = startIndex; i < startIndex + 4; i++) {

            HashEntry storedEntry = entries[i];
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

            // Keep the lowest depth and its index
            if (storedDepth < replacedMinDepth) {
                replacedMinDepth = storedDepth;
                replacedIndex = i;
            }
        }

        entries[replacedIndex] = newEntry;

    }

    public void clear() {
        printStatistics();
        entries = new HashEntry[tableSize];
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
        index = index % (tableSize - 3);
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

    public void printStatistics() {
        long fill = Arrays.stream(entries).filter(Objects::nonNull).count();
        float fillPercentage = ((float) fill / (float) tableSize) * 100;
        float hitPercentage = ((float) hits / (float) tries) * 100;
//        System.out.printf("TT -- table size: %s / %s (%s), tries: %s, hits: %s (%s)%n", fill, TABLE_SIZE, fillPercentage, tries, hits, hitPercentage);
    }

}
