package com.kelseyde.calvin.transposition.pawn;

import java.util.Arrays;
import java.util.Objects;

public class PawnHashTable {

    static final int TABLE_SIZE = 250000;

    PawnHashEntry[] entries;

    int tries;
    int hits;

    public PawnHashTable() {
        entries = new PawnHashEntry[TABLE_SIZE];
    }

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

    public void put(long pawnKey, PawnHashEntry entry) {
        int index = getIndex(pawnKey);
        entries[index] = entry;
    }

    private int getIndex(long pawnKey) {
        int index = (int) (pawnKey ^ (pawnKey >>> 32));
        if (index < 0) {
            index = -index;
        }
        index = index % TABLE_SIZE;
        return index;
    }

    public void clear() {
        tries = 0;
        hits = 0;
        entries = new PawnHashEntry[TABLE_SIZE];
    }

    public void printStatistics() {
        long fill = Arrays.stream(entries).filter(Objects::nonNull).count();
        float fillPercentage = ((float) fill / (float) TABLE_SIZE) * 100;
        float hitPercentage = ((float) hits / (float) tries) * 100;
        //System.out.printf("TT -- table size: %s / %s (%s), tries: %s, hits: %s (%s)%n", fill, TABLE_SIZE, fillPercentage, tries, hits, hitPercentage);
    }

}
