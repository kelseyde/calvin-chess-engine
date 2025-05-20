package com.kelseyde.calvin.tables.correction;

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
public abstract class CorrectionHistoryTable {

    protected static final int LIMIT = 1024;
    protected static final int MAX_BONUS = LIMIT / 4;

    protected int bonus(int score, int staticEval, int depth) {
        return clamp((score - staticEval) * depth / 8);
    }

    protected int gravity(int current, int update) {
        return current + update - current * Math.abs(update) / LIMIT;
    }

    private int clamp(int value) {
        return Math.max(-MAX_BONUS, Math.min(MAX_BONUS, value));
    }

    protected abstract void clear();


}
