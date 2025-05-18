package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.search.picker.AbstractMovePicker.MoveType;

public record ScoredMove(Move move,
                         Piece piece,
                         Piece captured,
                         int score,
                         int historyScore,
                         boolean givesCheck,
                         MoveType moveType) {

    public boolean isNoisy() {
        return moveType == MoveType.GOOD_NOISY || moveType == MoveType.BAD_NOISY;
    }

    public boolean isGoodNoisy() {
        return moveType == MoveType.GOOD_NOISY;
    }

    public boolean isKiller() {
        return moveType == MoveType.KILLER;
    }

    public boolean isBadNoisy() {
        return moveType == MoveType.BAD_NOISY;
    }

    public boolean isQuiet() {
        return moveType == MoveType.QUIET;
    }

}
