package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;

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

//    private TranspositionNode[] entries;
    private LinkedHashMap<Long, TranspositionNode> entries;

    private int collisions = 0;

    public TranspositionTable(Board board) {
        this.board = board;
        int tableSize = calculateTableSize();
//        entries = new TranspositionNode[tableSize];
        entries = new LinkedHashMap<>();
        collisions = 0;
    }

    public TranspositionNode get() {
        long zobristKey = board.getGameState().getZobristKey();
//        int index = getIndex(zobristKey);
//        TranspositionNode entry = entries[index];
        TranspositionNode entry = entries.get(zobristKey);
        return (entry != null && entry.getZobristKey() == zobristKey) ? entry : null;
    }

    public int put(NodeType type, int depth, Move move, int value) {
        long zobristKey = board.getGameState().getZobristKey();
        int index = getIndex(zobristKey);
        TranspositionNode entry = new TranspositionNode(zobristKey, type, move, depth, value);
//        if (entries[index] != null && entries[index].getZobristKey() != zobristKey) {
//            System.out.printf("size %s zobrist 1 %s index %s%n", entries.length, zobristKey, index);
//            System.out.printf("size %s zobrist 2 %s index %s%n", entries.length, entries[index].getZobristKey(), index);
//            collisions += 1;
//        }
        // In the case we already have an entry for this position, only replace it if the depth of the previous entry
        // is shallower than the depth of the new entry.
//        if (entries[index] == null || depth >= entries[index].getDepth()) {
//            entries[index] = entry;
//        }
        entries.put(zobristKey, entry);
        return index;
    }

    public void clear() {
        int tableSize = calculateTableSize();
//        entries = new TranspositionNode[tableSize];
        entries = new LinkedHashMap<>();
    }

    /**
     * The 64-bit zobrist key is too large to use as an index in a hashtable, due to memory constraints.
     * Therefore, we take the modulo of the zobrist and the table size, giving us a more manageable number.
     * Collisions can occur though, so we store the full key inside the entry to check we have the correct position.
     */
    private int getIndex(long zobristKey) {
//        return Math.abs(Long.valueOf(zobristKey % entries.length).intValue());
        return (int) (zobristKey >>> 32) << 1;
    }

    private int calculateTableSize() {
        int tableSizeBytes = TABLE_SIZE_MB * 1024 * 1024;
        int entrySizeBytes = MAX_ENTRY_SIZE_B;
        int entriesCount = tableSizeBytes / entrySizeBytes;
        log.info("Initialising a transposition table of {} entries based on {}MB table size and {}B entry size.",
                entriesCount, TABLE_SIZE_MB, entrySizeBytes);
        return entriesCount;
    }

    public void logTableSize() {
//        int fullTableSize = entries.length;
//        int occupiedSize = (int) Arrays.stream(entries)
//                .filter(Objects::nonNull)
//                .count();
//        double percent = ((double) occupiedSize / (double) fullTableSize) * 100;
//        System.out.println("collisions " + collisions);
//        System.out.printf("Transposition table size %s/%s (%s%%)%n", occupiedSize, fullTableSize, percent);
    }

}
