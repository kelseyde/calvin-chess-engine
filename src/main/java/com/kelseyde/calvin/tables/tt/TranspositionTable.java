package com.kelseyde.calvin.tables.tt;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.search.Score;

import java.util.stream.IntStream;

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
public class TranspositionTable {

    private static final int BUCKET_SIZE = 4;
    private static final int ENTRY_SIZE_BYTES = 16;

    private long[] keys;
    private int[] values;
    private int size;

    public TranspositionTable(int tableSizeMb) {
        this.size = (tableSizeMb * 1024 * 1024) / ENTRY_SIZE_BYTES;
        this.keys = new long[size];
        this.values = new int[size];
    }

    public HashEntry get(long key, int ply) {
        int index = index(key);
        for (int i = 0; i < BUCKET_SIZE; i++) {
            long storedKey = keys[index + i];
            if (storedKey != 0 && HashEntry.Key.getZobristPart(storedKey) == HashEntry.Key.getZobristPart(key)) {
                keys[index + i] = storedKey;
                int storedValue = values[index + i];
                int score = HashEntry.Value.getScore(storedValue);
                if (Score.isMate(score)) {
                    score = retrieveMateScore(score, ply);
                    storedValue = HashEntry.Value.setScore(storedValue, score);
                }
                return HashEntry.of(storedKey, storedValue);
            }
        }
        return null;
    }

    public void put(long key, int flag, int depth, int ply, Move move, int staticEval, int score, boolean pv) {

        // Get the start index of the 4-item bucket.
        final int startIndex = index(key);

        // If the eval is checkmate, adjust the score to reflect the number of ply from the root position
        if (Score.isMate(score)) score = calculateMateScore(score, ply);

        int replacedIndex = -1;
        int minDepth = Integer.MAX_VALUE;
        boolean replacedByAge = false;

        // Iterate over the four items in the bucket
        for (int i = startIndex; i < startIndex + 4; i++) {
            long storedKey = keys[i];

            // First, always prefer an empty slot if it is available.
            if (storedKey == 0) {
                replacedIndex = i;
                break;
            }

            // Second, always prefer an exact score
            if (flag == HashFlag.EXACT) {
                replacedIndex = i;
                break;
            }

            int storedValue = values[i];

            int storedFlag = HashEntry.Key.getFlag(storedKey);
            if (storedFlag == HashFlag.NONE) {
                replacedIndex = i;
                break;
            }

            int storedDepth = HashEntry.Key.getDepth(storedKey);
            // Then, if the stored entry matches the zobrist key and the depth is >= the stored depth, replace it.
            // If the depth is < the store depth, don't replace it and exit (although this should never happen).
            if (HashEntry.Key.getZobristPart(storedKey) == HashEntry.Key.getZobristPart(key)) {
                if (depth >= storedDepth - 4) {
                    // If the stored entry has a recorded best move but the new entry does not, use the stored one.
                    Move storedMove = HashEntry.Value.getMove(storedValue);
                    if (move == null && storedMove != null) {
                        move = storedMove;
                    }
                    replacedIndex = i;
                    break;
                } else {
                    return;
                }
            }

            // Finally, just replace the entry with the shallowest search depth.
            if (!replacedByAge && storedDepth < minDepth) {
                minDepth = storedDepth;
                replacedIndex = i;
            }

        }

        // Store the new entry in the table at the chosen index.
        if (replacedIndex != -1) {
            keys[replacedIndex] = HashEntry.Key.of(key, depth, staticEval, flag, pv);
            values[replacedIndex] = HashEntry.Value.of(score, move);
        }
    }

    public int fill() {
        return (int) IntStream.range(0, 1000)
                .filter(i -> keys[i] != 0)
                .count();
    }

    public void resize(int tableSizeMb) {
        this.size = (tableSizeMb * 1024 * 1024) / ENTRY_SIZE_BYTES;
        this.keys = new long[size];
        this.values = new int[size];
    }

    public void clear() {
        this.keys = new long[size];
        this.values = new int[size];
    }

    /**
     * Compresses the 64-bit zobrist key into a 32-bit key, to be used as an index in the hash table.
     */
    private int index(long key) {
        // XOR the upper and lower halves of the zobrist key together, producing a pseudo-random 32-bit result.
        // Then apply a mask ensuring the number is always positive, since it is to be used as an array index.
        long index = (key ^ (key >>> 32)) & 0x7FFFFFFF;
        // Modulo the result with the number of entries in the table, and align it with a multiple of 4,
        // ensuring the entries are always divided into 4-sized buckets.
        return (int) (index % (size - (BUCKET_SIZE - 1))) & -BUCKET_SIZE;
    }

    private int calculateMateScore(int score, int plyFromRoot) {
        // On insertion, adjust the mate score to reflect the number of ply from the root position
        return score > 0 ? score - plyFromRoot : score + plyFromRoot;
    }

    private int retrieveMateScore(int score, int plyFromRoot) {
        // On retrieval, adjust the mate score to reflect the number of ply from the root position
        return score > 0 ? score + plyFromRoot : score - plyFromRoot;
    }

}