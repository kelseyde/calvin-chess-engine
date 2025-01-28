package com.kelseyde.calvin.tables.tt;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.search.Score;
import com.kelseyde.calvin.tables.tt.HashEntry.Key;
import com.kelseyde.calvin.tables.tt.HashEntry.Value;

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

    private static final int ENTRY_SIZE_BYTES = 16;

    private long[] keys;
    private long[] values;
    private int size;
    private int age;
    private int tries;
    private int hits;

    /**
     * Constructs a transposition table of the given size in megabytes.
     */
    public TranspositionTable(int tableSizeMb) {
        this.size = (tableSizeMb * 1024 * 1024) / ENTRY_SIZE_BYTES;
        this.keys = new long[size];
        this.values = new long[size];
        this.tries = 0;
        this.hits = 0;
        this.age = 0;
    }

    /**
     * Retrieves an entry from the transposition table using the given zobrist key.
     */
    public HashEntry get(long key, int ply) {
        int index = index(key);
        tries++;
        long storedKey = keys[index];
        if (storedKey != 0 && Key.matches(storedKey, key)) {
            hits++;
            storedKey = Key.setAge(storedKey, age);
            keys[index] = storedKey;
            long storedValue = values[index];
            int score = Value.getScore(storedValue);
            if (Score.isMateScore(score)) {
                score = retrieveMateScore(score, ply);
                storedValue = Value.setScore(storedValue, score);
            }
            return HashEntry.of(storedKey, storedValue);
        }

        return null;
    }

    /**
     * Puts an entry into the transposition table.
     * </p>
     * The transposition table is separated into buckets of 4 entries each. This method uses a replacement scheme that
     * prefers to replace the least-valuable entry among the 4 candidates in the bucket. The order of preference
     * for replacement is:
     * <ol>
     * <li>An empty entry.</li>
     * <li>An entry with the same zobrist key and a depth less than or equal to the new entry.</li>
     * <li>The oldest entry in the bucket, stored further back in the game and so less likely to be relevant.</li>
     * <li>The entry with the lowest depth.</li>
     * </ol>
     */
    public void put(long key, int flag, int depth, int ply, Move move, int staticEval, int score) {

        // Get the start index of the 4-item bucket.
        final int index = index(key);

        // If the eval is checkmate, adjust the score to reflect the number of ply from the root position
        if (Score.isMateScore(score)) score = calculateMateScore(score, ply);

        long storedKey = keys[index];

        if (storedKey == 0) {
            keys[index] = Key.of(key, staticEval, age);
            values[index] = Value.of(score, move, flag, depth);
        }

        boolean matches = Key.matches(storedKey, key);

        if (!matches) {
            keys[index] = Key.of(key, staticEval, age);
            values[index] = Value.of(score, move, flag, depth);
        }

        long storedValue = values[index];

        Move storedMove = Value.getMove(storedValue);
        if (move == null && storedMove != null) {
            move = storedMove;
        }

        if (flag == HashFlag.EXACT || depth > Value.getDepth(values[index]) - 4) {
            keys[index] = Key.of(key, staticEval, age);
            values[index] = Value.of(score, move, flag, depth);
        }

    }

    /**
     * Calculate how full the transposition table currently is.
     * @return the number of entries out of 1000 that are currently not-null.
     */
    public int fill() {
        return (int) IntStream.range(0, 1000)
                .filter(i -> keys[i] != 0)
                .count();
    }

    /**
     * Increments the age counter for the transposition table.
     */
    public void incrementAge() {
        this.age++;
    }

    public void resize(int tableSizeMb) {
        this.size = (tableSizeMb * 1024 * 1024) / ENTRY_SIZE_BYTES;
        this.keys = new long[size];
        this.values = new long[size];
        this.tries = 0;
        this.hits = 0;
        this.age = 0;
    }

    /**
     * Clears the transposition table, resetting all entries and statistics.
     */
    public void clear() {
        this.tries = 0;
        this.hits = 0;
        this.age = 0;
        this.keys = new long[size];
        this.values = new long[size];
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
        return (int) index % size;
    }

    // On insertion, adjust the mate score to reflect the number of ply from the root position
    private int calculateMateScore(int score, int plyFromRoot) {
        return score > 0 ? score - plyFromRoot : score + plyFromRoot;
    }

    private int retrieveMateScore(int score, int plyFromRoot) {
        // On retrieval, adjust the mate score to reflect the number of ply from the root position
        return score > 0 ? score + plyFromRoot : score - plyFromRoot;
    }

}