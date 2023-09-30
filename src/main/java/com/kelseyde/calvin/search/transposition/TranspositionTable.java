package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class TranspositionTable {

    private static final int TABLE_SIZE_MB = 1;
    // TODO this is en estimation
    private static final int ENTRY_SIZE_B = 24;

    private Board board;

    private TranspositionEntry[] entries;

    boolean enabled = true;

    public TranspositionTable(Board board) {
        this.board = board;
        int tableSize = calculateTableSize();
        entries = new TranspositionEntry[tableSize];
    }

    public TranspositionEntry get() {
        if (!enabled) {
            return null;
        }
        long zobristKey = board.getGameState().getZobristKey();
        TranspositionEntry entry = getEntry(zobristKey);
        return (entry != null && entry.getZobristKey() == zobristKey) ? entry : null;
    }

    public void put(NodeType type, int depth, Move move, int value) {
        if (!enabled) {
            return;
        }
        long zobristKey = board.getGameState().getZobristKey();
        TranspositionEntry entry = new TranspositionEntry(zobristKey, type, move, depth, value);
        int index = getIndex(zobristKey);
        entries[index] = entry;
    }

    public void clear() {
        int tableSize = calculateTableSize();
        entries = new TranspositionEntry[tableSize];
    }

    /**
     * The 64-bit zobrist key is too large to use as an index in a hashtable, due to memory constraints.
     * Therefore, we take the modulo of the zobrist and the table size, giving us a more manageable number.
     * Collisions can occur though, so we store the full key inside the entry to check we have the correct position.
     */
    private TranspositionEntry getEntry(long zobristKey) {
        int index = getIndex(zobristKey);
        return entries[index];
    }

    private int getIndex(long zobristKey) {
        return Math.abs(Long.valueOf(zobristKey % entries.length).intValue());
    }

    private int calculateTableSize() {
        int tableSizeBytes = TABLE_SIZE_MB * 1024 * 1024;
        int entrySizeBytes = ENTRY_SIZE_B;
        int entriesCount = tableSizeBytes / entrySizeBytes;
        log.trace("Initialising a transposition table of {} entries based on {}MB table size and {}B entry size.",
                entriesCount, TABLE_SIZE_MB, entrySizeBytes);
        return entriesCount;
    }

}
