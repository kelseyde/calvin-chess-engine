package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.tables.tt.TranspositionTable;

/**
 * Correction history tracks how much the static evaluation of a position matched the actual search score. We can use
 * this information to 'correct' the current static eval based on the diff between the static eval and the search score
 * of previously searched positions.
 * <p>
 * This is a similar heuristic to re-using the cached search score in the {@link TranspositionTable}, except rather than
 * using the score from the exact same position, we use a running average of eval diffs of previously searched positions
 * which share some feature, e.g. pawn structure, material balance, and so on.
 * <p>
 * The running average also gives more weight to positions that were searched to a greater depth, as these are more
 * likely to have a more accurate final search score.
 *
 * @see <a href="https://www.chessprogramming.org/Static_Evaluation_Correction_History">Chess Programming Wiki</a>
 *
 */
public class CorrectionHistoryTable {

    public static final int SCALE = 256;
    private static final int MAX = SCALE * 32;

    private final int tableSize;
    int[][] entries;

    public CorrectionHistoryTable(int tableSize) {
        this.tableSize = tableSize;
        this.entries = new int[2][tableSize];
    }

    /**
     * Update the correction history entry to be a weighted sum old value and the new delta of the score and static eval.
     */
    public void update(long key, boolean white, int depth, int score, int staticEval) {

        // Compute the new correction value, and retrieve the old value
        int newValue = (score - staticEval) * SCALE;
        int oldValue = get(key, white);

        // Weight the new value based on the search depth, and the old value based on the remaining weight
        int newWeight = Math.min(depth + 1, 16);
        int oldWeight = SCALE - newWeight;

        // Compute the weighted sum of the old and new values, and clamp the result.
        int update = (oldValue * oldWeight + newValue * newWeight) / SCALE;
        update = clamp(update);

        // Update the correction history table with the new value.
        put(key, white, update);

    }

    /**
     * Correct the static eval with the value from the correction history table.
     */
    public int correctEvaluation(long key, boolean white, int staticEval) {
        int entry = get(key, white);
        return staticEval + entry / SCALE;
    }

    /**
     * Retrieve the correction history entry for the given side to move and hash index.
     */
    public int get(long key, boolean white) {
        int colourIndex = Colour.index(white);
        int hashIndex = hashIndex(key);
        return entries[colourIndex][hashIndex];
    }

    /**
     * Update the correction history entry for the given side to move and hash index.
     */
    private void put(long key, boolean white, int value) {
        int colourIndex = Colour.index(white);
        int hashIndex = hashIndex(key);
        entries[colourIndex][hashIndex] = value;
    }

    public void clear() {
        this.entries = new int[2][tableSize];
    }

    /**
     * Compute a unique index into the correction history table based on the given hash key.
     */
    private int hashIndex(long key) {
        // Ensure the key is positive
        key = key & 0x7FFFFFFF;
        // Return a modulo of the key based on the table size
        return (int) key % tableSize;
    }

    private int clamp(int value) {
        return Math.max(-MAX, Math.min(MAX, value));
    }

}
