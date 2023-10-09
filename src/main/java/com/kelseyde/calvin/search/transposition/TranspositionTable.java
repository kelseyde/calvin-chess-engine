package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
public class TranspositionTable {

    /**
     * Maximum size of the table in megabytes
     */
    private static final int TABLE_SIZE_MB = 10;

    /**
     * Estimated size of a single table entry in bytes
     */
    private static final int MAX_ENTRY_SIZE_B = 24;

    private final Board board;

    private TranspositionNode[] entries;

    public TranspositionTable(Board board) {
        this.board = board;
        int tableSize = calculateTableSize();
        entries = new TranspositionNode[tableSize];
    }

    public TranspositionNode get() {
        long zobristKey = board.getGameState().getZobristKey();
        TranspositionNode entry = getEntry(zobristKey);
        return (entry != null && entry.getZobristKey() == zobristKey) ? entry : null;
    }

    public void put(NodeType type, int depth, Move move, int value) {
        long zobristKey = board.getGameState().getZobristKey();
        TranspositionNode entry = new TranspositionNode(zobristKey, type, move, depth, value);
        int index = getIndex(zobristKey);
        entries[index] = entry;
    }

    public void clear() {
        int tableSize = calculateTableSize();
        entries = new TranspositionNode[tableSize];
    }

    /**
     * The 64-bit zobrist key is too large to use as an index in a hashtable, due to memory constraints.
     * Therefore, we take the modulo of the zobrist and the table size, giving us a more manageable number.
     * Collisions can occur though, so we store the full key inside the entry to check we have the correct position.
     */
    private TranspositionNode getEntry(long zobristKey) {
        int index = getIndex(zobristKey);
        return entries[index];
    }

    private int getIndex(long zobristKey) {
        return Math.abs(Long.valueOf(zobristKey % entries.length).intValue());
    }

    private int calculateTableSize() {
        int tableSizeBytes = TABLE_SIZE_MB * 1024 * 1024;
        int entrySizeBytes = MAX_ENTRY_SIZE_B;
        int entriesCount = tableSizeBytes / entrySizeBytes;
        log.trace("Initialising a transposition table of {} entries based on {}MB table size and {}B entry size.",
                entriesCount, TABLE_SIZE_MB, entrySizeBytes);
        return entriesCount;
    }

    public void logTableSize() {
        int fullTableSize = entries.length;
        int occupiedSize = (int) Arrays.stream(entries)
                .filter(Objects::nonNull)
                .count();
        double percent = ((double) occupiedSize / (double) fullTableSize) * 100;
        System.out.printf("Transposition table size %s/%s (%s%%)%n", occupiedSize, fullTableSize, percent);
    }

}
