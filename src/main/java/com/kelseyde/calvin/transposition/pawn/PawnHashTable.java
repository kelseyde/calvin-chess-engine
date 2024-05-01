package com.kelseyde.calvin.transposition.pawn;

public class PawnHashTable {

    static final int ENTRY_SIZE_BYTES = 32;

    final int tableSize;
    PawnHashEntry[] entries;

    public PawnHashTable(int tableSizeMb) {
        this.tableSize = (tableSizeMb / ENTRY_SIZE_BYTES) * 1024 * 1024;
        entries = new PawnHashEntry[tableSize];
    }

    public PawnHashEntry get(long pawnKey) {
        int index = getIndex(pawnKey);
        PawnHashEntry entry = entries[index];
        if (entry != null && entry.key() == pawnKey) {
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
        index = index % tableSize;
        return index;
    }

    public void clear() {
        entries = new PawnHashEntry[tableSize];
    }

}
