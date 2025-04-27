package com.kelseyde.calvin.tables.correction;

import com.kelseyde.calvin.tables.tt.TranspositionTable;

/**
 * Correction history tracks how much the static evaluation of a position matched the actual search score. We can use
 * this information to 'correct' the current static score based on the diff between the static score and the search score
 * of previously searched positions.
 * <p>
 * This is a similar heuristic to re-using the cached search score in the {@link TranspositionTable}, except rather than
 * using the score from the exact same position, we use a running average of score diffs of previously searched positions
 * which share some feature, e.g. pawn structure, material balance, and so on.
 * <p>
 * The running average also gives more weight to positions that were searched to a greater depth, as these are more
 * likely to have a more accurate final search score.
 *
 * @see <a href="https://www.chessprogramming.org/Static_Evaluation_Correction_History">Chess Programming Wiki</a>
 *
 */
public abstract class CorrectionHistoryTable {

    public static final int SCALE = 64;
    protected static final int MAX = SCALE * 32;

    /**
     * Compute the new correction based on a weighted sum of old value and the new delta of the score and static score.
     */
    public int correction(int oldValue, int staticEval, int score, int depth) {

        // Compute the new correction value, and retrieve the old value
        int newValue = (score - staticEval) * SCALE;

        // Weight the new value based on the search depth, and the old value based on the remaining weight
        int newWeight = Math.min(depth + 1, 16);
        int oldWeight = SCALE - newWeight;

        // Compute the weighted sum of the old and new values, and clamp the result.
        int update = (oldValue * oldWeight + newValue * newWeight) / SCALE;
        update = clamp(update);

        return update;

    }

    protected abstract void clear();

    private int clamp(int value) {
        return Math.max(-MAX, Math.min(MAX, value));
    }

}
