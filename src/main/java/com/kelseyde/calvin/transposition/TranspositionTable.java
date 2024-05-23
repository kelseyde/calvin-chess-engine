package com.kelseyde.calvin.transposition;

import com.kelseyde.calvin.board.Move;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.Objects;

/**
 * The transposition table is a database that stores the results of previously searched positions, as well as relevant
 * information about that position, such as the depth to which it was searched, and the best move found during the previous
 * search.
 * </p>
 * Since many positions can be arrived at by several different move orders, a simple brute-force search of the search tree
 * encounters the same positions again and again (via 'transposition'). A transposition table, therefore, greatly reduces
 * the size of the search tree, since subsequent arrivals at the position can re-use the results of previous searches.
 * </p>
 * @see <a href="https://www.chessprogramming.org/Transposition_Table">Chess Programming Wiki</a>
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TranspositionTable {

    static final int ENTRY_SIZE_BYTES = 32;
    static final int CHECKMATE_BOUND = 1000000 - 256;

    final int tableSize;
    HashEntry[] entries;

    int tries;
    int hits;
    int generation;

    public TranspositionTable(int tableSizeMb) {
        this.tableSize = (tableSizeMb * 1024 * 1024) / ENTRY_SIZE_BYTES;
        entries = new HashEntry[tableSize];
        tries = 0;
        hits = 0;
        generation = 0;
    }

    public HashEntry get(long zobristKey, int ply) {
        int index = getIndex(zobristKey);
        tries++;
        for (int i = 0; i < 4; i++) {
            HashEntry entry = entries[index + i];
            if (entry != null && entry.getHalfZobrist() == HashEntry.halfZobrist(zobristKey)) {
                hits++;
                entry.setGeneration(generation);
                if (isMateScore(entry.getScore())) {
                    int score = retrieveMateScore(entry.getScore(), ply);
                    return entry.withAdjustedScore(score);
                }
                return entry;
            }
        }
        return null;
    }

    public void put(long zobristKey, HashFlag flag, int depth, int ply, Move move, int score) {
        int startIndex = getIndex(zobristKey);
        if (isMateScore(score)) score = calculateMateScore(score, ply);
        HashEntry newEntry = HashEntry.of(zobristKey, score, move, flag, depth, generation);

        int replacedIndex = -1;
        int minDepth = Integer.MAX_VALUE;

        for (int i = startIndex; i < startIndex + 4; i++) {
            HashEntry storedEntry = entries[i];

            // if stored entry is null, overwrite it and exit.
            // if stored entry matches hash, but depth < storedDepth, do not overwrite and exit.
            // if stored entry matches hash, and depth >= storedDepth, overwrite it and exit.
            // if no stored entry matches the hash, just overwrite the one with the shallowest depth

            if (storedEntry == null || storedEntry.getHalfZobrist() == HashEntry.halfZobrist(zobristKey)) {
                if (storedEntry == null || depth >= storedEntry.getDepth()) {
                    if (newEntry.getMove() == null && storedEntry != null && storedEntry.getMove() != null) {
                        newEntry.setMove(storedEntry.getMove());
                    }
                    replacedIndex = i;
                    break;
                } else {
                    return;
                }
            }

            if (storedEntry.getDepth() < minDepth) {
                minDepth = storedEntry.getDepth();
                replacedIndex = i;
            }
//            if (storedEntry == null) {
//                replacedIndex = i;
//                break;
//            }
//
//            if (storedEntry.getHalfZobrist() == HashEntry.halfZobrist(zobristKey)) {
//                if (depth >= storedEntry.getDepth()) {
//                    if (newEntry.getMove() == null && storedEntry.getMove() != null) {
//                        newEntry.setMove(storedEntry.getMove());
//                    }
//                    replacedIndex = i;
//                    break;
//                } else {
//                    return;
//                }
//            }
//
//            if (storedEntry.getGeneration() < newEntry.getGeneration()) {
//                replacedIndex = i;
//            }
//
//            if (storedEntry.getDepth() < minDepth) {
//                minDepth = storedEntry.getDepth();
//                replacedIndex = i;
//            }
        }

        if (replacedIndex != -1) {
            entries[replacedIndex] = newEntry;
        }
    }

    public void incrementGeneration() {
        generation++;
    }

    public void clear() {
        printStatistics();
        tries = 0;
        hits = 0;
        generation = 0;
        entries = new HashEntry[tableSize];
    }

    private int getIndex(long zobristKey) {
        long index = (zobristKey ^ (zobristKey >>> 32)) & 0x7FFFFFFF;
        return (int) (index % (tableSize - 3)) & ~3;
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
        float fillPercentage = ((float) fill / tableSize) * 100;
        float hitPercentage = ((float) hits / tries) * 100;
        System.out.printf("TT %s -- size: %s / %s (%s), tries: %s, hits: %s (%s)%n", this.hashCode(), fill, entries.length, fillPercentage, tries, hits, hitPercentage);
    }
}