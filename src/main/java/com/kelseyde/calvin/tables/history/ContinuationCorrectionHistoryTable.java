package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
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
public class ContinuationCorrectionHistoryTable {

    public static final int SCALE = 256;
    private static final int MAX = SCALE * 32;
    private static final int TABLE_SIZE = 16384;

    int[][][][][] entries;

    public ContinuationCorrectionHistoryTable() {
        this.entries = new int[2][6][64][6][64];
    }

    /**
     * Update the correction history entry to be a weighted sum old value and the new delta of the score and static eval.
     */
    public void update(Move prevMove1, Piece prevPiece1, Move prevMove2, Piece prevPiece2, boolean white, int depth, int score, int staticEval) {

        // Compute the new correction value, and retrieve the old value
        int newValue = (score - staticEval) * SCALE;
        int oldValue = get(white, prevMove1, prevPiece1, prevMove2, prevPiece2);

        // Weight the new value based on the search depth, and the old value based on the remaining weight
        int newWeight = Math.min(depth + 1, 16);
        int oldWeight = SCALE - newWeight;

        // Compute the weighted sum of the old and new values, and clamp the result.
        int update = (oldValue * oldWeight + newValue * newWeight) / SCALE;
        update = clamp(update);

        // Update the correction history table with the new value.
        put(white, prevMove1, prevPiece1, prevMove2, prevPiece2, update);

    }

    /**
     * Correct the static eval with the value from the correction history table.
     */
    public int correctEvaluation(Move prevMove1, Piece prevPiece1, Move prevMove2, Piece prevPiece2, boolean white, int staticEval) {
        int entry = get(white, prevMove1, prevPiece1, prevMove2, prevPiece2);
        return staticEval + entry / SCALE;
    }

    /**
     * Retrieve the correction history entry for the given side to move and hash index.
     */
    public int get(boolean white, Move prevMove1, Piece prevPiece1, Move prevMove2, Piece prevPiece2) {
        int colourIndex = Colour.index(white);
        int pieceIndex1 = prevPiece1.index();
        int to1 = prevMove1.to();
        int pieceIndex2 = prevPiece2.index();
        int to2 = prevMove2.to();
        return entries[colourIndex][pieceIndex1][to1][pieceIndex2][to2];
    }

    /**
     * Update the correction history entry for the given side to move and hash index.
     */
    private void put(boolean white, Move prevMove1, Piece prevPiece1, Move prevMove2, Piece prevPiece2, int value) {
        int colourIndex = Colour.index(white);
        int pieceIndex1 = prevPiece1.index();
        int to1 = prevMove1.to();
        int pieceIndex2 = prevPiece2.index();
        int to2 = prevMove2.to();
        entries[colourIndex][pieceIndex1][to1][pieceIndex2][to2] = value;
    }

    public void clear() {
        this.entries = new int[2][6][64][6][64];
    }

    private int clamp(int value) {
        return Math.max(-MAX, Math.min(MAX, value));
    }

}
