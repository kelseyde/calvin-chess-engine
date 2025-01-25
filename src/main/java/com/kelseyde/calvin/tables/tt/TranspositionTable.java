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

    private static final int ENTRY_SIZE_BYTES = 16;

    private long[] keys;
    private long[] values;
    private int size;
    private int age;

    public TranspositionTable(int tableSizeMb) {
        this.size = (tableSizeMb * 1024 * 1024) / ENTRY_SIZE_BYTES;
        this.keys = new long[size];
        this.values = new long[size];
        this.age = 0;
    }

    public HashEntry get(long key, int ply) {
        int index = index(key);
        long storedKey = keys[index];
        if (storedKey != 0 && HashEntry.Key.getZobristPart(storedKey) == HashEntry.Key.getZobristPart(key)) {
            storedKey = HashEntry.Key.setAge(storedKey, age);
            keys[index] = storedKey;
            long storedValue = values[index];
            int score = HashEntry.Value.getScore(storedValue);
            if (Score.isMateScore(score)) {
                score = retrieveMateScore(score, ply);
                storedValue = HashEntry.Value.setScore(storedValue, score);
            }
            return HashEntry.of(storedKey, storedValue);
        }

        return null;
    }

    public void put(HashEntry entry, long key, int flag, int depth, int ply, Move move, int staticEval, int score) {

        boolean replace = entry == null
                || entry.flag() == HashFlag.NONE
                || flag == HashFlag.EXACT
                || entry.zobrist() != key
                || depth >= entry.depth() - 4;

        if (replace) {
            if (Score.isMateScore(score)) {
                score = calculateMateScore(score, ply);
            }
            if (entry != null && entry.zobrist() == key && move == null && entry.move() != null) {
                move = entry.move();
            }
            int index = index(key);
            keys[index] = HashEntry.Key.of(key, staticEval, age);
            values[index] = HashEntry.Value.of(score, move, flag, depth);
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
        this.age = 0;
    }

    /**
     * Clears the transposition table, resetting all entries and statistics.
     */
    public void clear() {
        this.age = 0;
        this.keys = new long[size];
        this.values = new long[size];
    }

    /**
     * Compresses the 64-bit zobrist key into a 32-bit key, to be used as an index in the hash table.
     */
    private int index(long key) {
        return (int) key & (size - 1);
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