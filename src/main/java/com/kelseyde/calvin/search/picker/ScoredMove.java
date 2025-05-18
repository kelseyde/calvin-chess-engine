package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

public record ScoredMove(Move move,
                         Piece piece,
                         Piece captured,
                         int score,
                         int historyScore,
                         AbstractMovePicker.MoveType moveType) {

    public boolean isNoisy() {
        return moveType == AbstractMovePicker.MoveType.GOOD_NOISY || moveType == AbstractMovePicker.MoveType.BAD_NOISY;
    }

    public boolean isGoodNoisy() {
        return moveType == AbstractMovePicker.MoveType.GOOD_NOISY;
    }

    public boolean isKiller() {
        return moveType == AbstractMovePicker.MoveType.KILLER;
    }

    public boolean isBadNoisy() {
        return moveType == AbstractMovePicker.MoveType.BAD_NOISY;
    }

    public boolean isQuiet() {
        return moveType == AbstractMovePicker.MoveType.QUIET;
    }

}
