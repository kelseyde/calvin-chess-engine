package com.kelseyde.calvin.transposition.pawn;

import java.util.Arrays;
import java.util.Objects;

/**
 * A hash table specifically for storing pawn structure evaluations.
 */
public class PawnHashTable {

    static final int INDEX_MASK = 0x7FFFFFFF;

    PawnHashEntry[] entries;

    int tableSize;
    int tries;
    int hits;

    public PawnHashTable(int tableSizeMb) {
        this.tableSize = (tableSizeMb * 1024 * 1024) / PawnHashEntry.SIZE_BYTES;
        entries = new PawnHashEntry[tableSize];
        tries = 0;
        hits = 0;
    }

    /**
     * Retrieves a pawn hash entry using the pawn key.
     *
     * @param pawnKey the key representing the pawn structure.
     * @return the corresponding {@link PawnHashEntry} if found, or {@code null} if not found.
     */
    public PawnHashEntry get(long pawnKey) {
        int index = getIndex(pawnKey);
        tries++;
        PawnHashEntry entry = entries[index];
        if (entry != null && entry.key() == pawnKey) {
            hits++;
            return entry;
        }
        return null;
    }

    /**
     * Puts a pawn hash entry into the table.
     *
     * @param pawnKey the key representing the pawn structure.
     * @param entry   the {@link PawnHashEntry} to store.
     */
    public void put(long pawnKey, PawnHashEntry entry) {
        int index = getIndex(pawnKey);
        entries[index] = entry;
    }

    /**
     * Computes the index for a given pawn key.
     *
     * @param pawnKey the key representing the pawn structure.
     * @return the index in the table.
     */
    private int getIndex(long pawnKey) {
        // XOR the upper and lower halves of the key and mask the result to ensure it is positive.
        int index = (int) (pawnKey ^ (pawnKey >>> 32)) & INDEX_MASK;
        return index % tableSize;
    }

    public void clear() {
        //printStatistics();
        tries = 0;
        hits = 0;
        entries = new PawnHashEntry[tableSize];
    }

    public void printStatistics() {
        long fill = Arrays.stream(entries).filter(Objects::nonNull).count();
        float fillPercentage = ((float) fill / (float) tableSize) * 100;
        float hitPercentage = ((float) hits / (float) tries) * 100;
        System.out.printf("TT -- table size: %s / %s (%s), tries: %s, hits: %s (%s)%n", fill, tableSize, fillPercentage, tries, hits, hitPercentage);
    }

}
