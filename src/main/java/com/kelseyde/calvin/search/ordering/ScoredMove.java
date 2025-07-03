package com.kelseyde.calvin.search.ordering;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.search.ordering.MovePicker.MoveType;

/**
 * A wrapper for a move that has been scored during move ordering. Contains the move, its score and move type, and
 * various metadata that can be re-used during search, such as the moved piece, captured piece, and history score.
 */
public record ScoredMove(Move move,
                         Piece piece,
                         Piece captured,
                         int score,
                         int historyScore,
                         boolean seePositive,
                         MoveType moveType) {

    public boolean isGoodNoisy() {
        return moveType == MoveType.GOOD_NOISY;
    }

    public boolean isBadNoisy() {
        return moveType == MoveType.BAD_NOISY;
    }

    public boolean isQuiet() {
        return moveType == MoveType.GOOD_QUIET || moveType == MoveType.BAD_QUIET;
    }

    public boolean isGoodQuiet() {
        return moveType == MoveType.GOOD_QUIET;
    }

}
