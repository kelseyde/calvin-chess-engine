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

    private long[] keys;
    private short[] values;
    private int size;
    private int mask;
    private int age;

    /**
     * Constructs a transposition table of the given size in megabytes.
     */
    public TranspositionTable(int tableSizeMb) {
        this.size = Integer.highestOneBit(tableSizeMb * 1024 * 1024 / HashEntry.SIZE_BYTES);
        this.mask = size - 1;
        this.keys = new long[size];
        this.values = new short[size];
        this.age = 0;
    }

    /**
     * Retrieves an entry from the transposition table using the given zobrist key.
     */
    public HashEntry get(long key, int ply) {
        int index = (int) (key & mask);
        long storedKey = keys[index];
        if (storedKey != 0 && HashEntry.matches(key, storedKey)) {
            keys[index] = storedKey;
            short storedValue = values[index];
            storedValue = HashEntry.Value.setAge(storedValue, age);
            int score = HashEntry.Key.getScore(storedKey);
            if (Score.isMateScore(score)) {
                score = retrieveMateScore(score, ply);
                storedKey = HashEntry.Key.setScore(storedKey, score);
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

        final int index = (int) (key & mask);

        // Correct mate scores based on distance from root
        if (Score.isMateScore(score))
            score = calculateMateScore(score, ply);

        if (flag == HashFlag.EXACT) {
            keys[index] = HashEntry.Key.of(key, move, score, staticEval);
            values[index] = HashEntry.Value.of(depth, flag, age);
            return;
        }

        long storedKey = keys[index];

        final boolean matches = HashEntry.matches(key, storedKey);

        if (storedKey == 0 || !matches) {
            keys[index] = HashEntry.Key.of(key, move, score, staticEval);
            values[index] = HashEntry.Value.of(depth, flag, age);
            return;
        }

        short storedValue = values[index];

        int storedDepth = HashEntry.Value.getDepth(storedValue);

        if (depth >= storedDepth - 4) {

            Move storedMove = HashEntry.Key.getMove(storedKey);
            if (move == null && storedMove != null) {
                move = storedMove;
            }

            keys[index] = HashEntry.Key.of(key, move, score, staticEval);
            values[index] = HashEntry.Value.of(depth, flag, age);
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
        this.size = Integer.highestOneBit(tableSizeMb * 1024 * 1024 / HashEntry.SIZE_BYTES);
        this.mask = size - 1;
        this.keys = new long[size];
        this.values = new short[size];
        this.age = 0;
    }

    /**
     * Clears the transposition table, resetting all entries and statistics.
     */
    public void clear() {
        this.age = 0;
        this.keys = new long[size];
        this.values = new short[size];
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