package com.kelseyde.calvin.search.picker;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.search.picker.MovePicker.MoveType;

import java.util.Objects;

public class ScoredMove {
    public final Move move;
    public final Piece piece;
    public final Piece captured;
    public final int score;
    public final int historyScore;
    public MoveType moveType;

    public ScoredMove(Move move,
                      Piece piece,
                      Piece captured,
                      int score,
                      int historyScore,
                      MoveType moveType) {
        this.move = move;
        this.piece = piece;
        this.captured = captured;
        this.score = score;
        this.historyScore = historyScore;
        this.moveType = moveType;
    }

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
        return moveType == MoveType.GOOD_QUIET || moveType == MoveType.BAD_QUIET;
    }

    public boolean isGoodQuiet() {
        return moveType == MoveType.GOOD_QUIET;
    }

    public Move move() {
        return move;
    }

    public Piece piece() {
        return piece;
    }

    public Piece captured() {
        return captured;
    }

    public int score() {
        return score;
    }

    public int historyScore() {
        return historyScore;
    }

    public MoveType moveType() {
        return moveType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ScoredMove) obj;
        return Objects.equals(this.move, that.move) &&
                Objects.equals(this.piece, that.piece) &&
                Objects.equals(this.captured, that.captured) &&
                this.score == that.score &&
                this.historyScore == that.historyScore &&
                Objects.equals(this.moveType, that.moveType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(move, piece, captured, score, historyScore, moveType);
    }

    @Override
    public String toString() {
        return "ScoredMove[" +
                "move=" + move + ", " +
                "piece=" + piece + ", " +
                "captured=" + captured + ", " +
                "score=" + score + ", " +
                "historyScore=" + historyScore + ", " +
                "moveType=" + moveType + ']';
    }


}
