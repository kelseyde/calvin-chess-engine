package com.kelseyde.calvin.search.transposition;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TranspositionTable2 implements TT {

    private static final int CHECKMATE_BOUND = 990000;

    /**
     * Maximum size of the table in megabytes
     */
    private static final int TABLE_SIZE_MB = 256;

    /**
     * Estimated size of a single table entry in bytes
     */
    private static final int MAX_ENTRY_SIZE_B = 24;

    private final Board board;

    private Transposition[] entries;

    public TranspositionTable2(Board board) {
        this.board = board;
        int tableSize = calculateTableSize();
        entries = new Transposition[tableSize];
    }

    public Transposition get() {
        long zobristKey = board.getGameState().getZobristKey();
        Transposition entry = getEntry(zobristKey);
//        if (entry != null && entry.getZobristKey() == zobristKey) {
//            entry.setValue(retrieveCorrectedMateScore(entry.getValue(), plyFromRoot));
//            return entry;
//        } else {
//            return null;
//        }
        return (entry != null && entry.getZobristKey() == zobristKey) ? entry : null;
    }

    public void put(NodeType type, int plyRemaining, Move move, int value) {
        long zobristKey = board.getGameState().getZobristKey();
        Transposition entry = new Transposition(zobristKey, type, move, plyRemaining, value);
        int index = getIndex(zobristKey);
//        if (entries[index] == null || entries[index].getDepth() <= entry.getDepth()) {
            entries[index] = entry;
//        }
    }

    public void clear() {
        int tableSize = calculateTableSize();
        entries = new Transposition[tableSize];
    }

    /**
     * The 64-bit zobrist key is too large to use as an index in a hashtable, due to memory constraints.
     * Therefore, we take the modulo of the zobrist and the table size, giving us a more manageable number.
     * Collisions can occur though, so we store the full key inside the entry to check we have the correct position.
     */
    private Transposition getEntry(long zobristKey) {
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

    private int calculateCorrectedMateScore(int score, int plyFromRoot) {
        if (Math.abs(score) < CHECKMATE_BOUND) return score;
        int sign = score > 0 ? 1 : -1;
        return (score * sign + plyFromRoot) * sign;
    }

    private int retrieveCorrectedMateScore(int score, int plyFromRoot) {
        if (Math.abs(score) < CHECKMATE_BOUND) return score;
        int sign = score > 0 ? 1 : -1;
        return (score * sign - plyFromRoot) * sign;
    }

}
