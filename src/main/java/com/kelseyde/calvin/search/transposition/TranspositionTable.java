package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;

@Slf4j
public class TranspositionTable {

    /**
     * Maximum size of the table in megabytes
     */
    private static final int TABLE_SIZE_MB = 64;

    /**
     * Estimated size of a single table entry in bytes
     */
    private static final int MAX_ENTRY_SIZE_B = 24;

    private final Board board;

    private LinkedHashMap<Long, TranspositionNode> entries;

    public TranspositionTable(Board board) {
        this.board = board;
        int tableSize = calculateTableSize();
        entries = new LinkedHashMap<>();
    }

    public TranspositionNode get() {
        long zobristKey = board.getGameState().getZobristKey();
        TranspositionNode entry = entries.get(zobristKey);
        return (entry != null && entry.getZobristKey() == zobristKey) ? entry : null;
    }

    public void put(NodeType type, int depth, Move move, int value) {
        long zobristKey = board.getGameState().getZobristKey();
        TranspositionNode entry = new TranspositionNode(zobristKey, type, move, depth, value);
        entries.put(zobristKey, entry);
    }

    public void clear() {
        entries = new LinkedHashMap<>();
    }

    private int calculateTableSize() {
        int tableSizeBytes = TABLE_SIZE_MB * 1024 * 1024;
        int entrySizeBytes = MAX_ENTRY_SIZE_B;
        int entriesCount = tableSizeBytes / entrySizeBytes;
        log.info("Initialising a transposition table of {} entries based on {}MB table size and {}B entry size.",
                entriesCount, TABLE_SIZE_MB, entrySizeBytes);
        return entriesCount;
    }

}
