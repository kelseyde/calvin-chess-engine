package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Move;

public record ScoredMove(Move move, int score) {

    public boolean isTTMove() {
        return score >= MoveBonus.TT_MOVE;
    }

    public boolean isGoodCapture() {
        return score >= MoveBonus.GOOD_CAPTURE && score < MoveBonus.TT_MOVE;
    }

    public boolean isKiller() {
        return score >= MoveBonus.KILLER_MOVE && score < MoveBonus.GOOD_CAPTURE;
    }

    public boolean isBadCapture() {
        return score >= MoveBonus.BAD_CAPTURE && score < MoveBonus.KILLER_MOVE;
    }

    public boolean isQuiet() {
        return score >= MoveBonus.QUIET_MOVE && score < MoveBonus.BAD_CAPTURE;
    }

}
