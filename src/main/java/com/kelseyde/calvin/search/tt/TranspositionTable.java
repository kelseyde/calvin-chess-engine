package com.kelseyde.calvin.search.tt;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class TranspositionTable {

    private static final int TABLE_SIZE_MB = 64;
    // TODO this is en estimation
    private static final int ENTRY_SIZE_B = 24;

    private static final TranspositionEntry EXAMPLE_ENTRY = new TranspositionEntry(0L, NodeType.EXACT, Move.builder().build(), 0, 0);

    private Board board;

    private TranspositionEntry[] entries;

    public TranspositionTable(Board board) {
        this.board = board;
        int tableSizeBytes = TABLE_SIZE_MB * 1024 * 1024;
        int entrySizeBytes = ENTRY_SIZE_B;
        int entriesCount = tableSizeBytes / entrySizeBytes;
        log.info("Initialising a transposition table of with {} entries based on {}MB table size and {}B entry size.",
                entriesCount, tableSizeBytes, entrySizeBytes);
        entries = new TranspositionEntry[entriesCount];
    }

    public TranspositionEntry get(int depth, int alpha, int beta) {
        long zobristKey = board.getGameState().getZobristKey();
        TranspositionEntry entry = getEntry(zobristKey);
        return (entry != null && entry.getZobristKey() == zobristKey) ? entry : null;
    }

    public void put(NodeType type, Move move, int depth, int value) {
        long zobristKey = board.getGameState().getZobristKey();
        TranspositionEntry entry = new TranspositionEntry(zobristKey, type, move, depth, value);
        int index = getIndex(zobristKey);
        entries[index] = entry;
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

}
