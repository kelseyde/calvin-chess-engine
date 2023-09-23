package com.kelseyde.calvin.movegeneration.result;

public enum GameResult {

    IN_PROGRESS,
    WHITE_WINS_BY_CHECKMATE,
    BLACK_WINS_BY_CHECKMATE,
    WHITE_WINS_BY_TIMEOUT,
    BLACK_WINS_BY_TIMEOUT,
    DRAW_BY_STALEMATE,
    DRAW_BY_REPETITION,
    DRAW_BY_INSUFFICIENT_MATERIAL,
    DRAW_BY_FIFTY_MOVE_RULE;

    public boolean isWin() {
        return this.equals(WHITE_WINS_BY_CHECKMATE) ||
                this.equals(BLACK_WINS_BY_CHECKMATE) ||
                this.equals(WHITE_WINS_BY_TIMEOUT) ||
                this.equals(BLACK_WINS_BY_TIMEOUT);
    }

    public boolean isDraw() {
        return this.equals(DRAW_BY_STALEMATE) ||
                this.equals(DRAW_BY_REPETITION) ||
                this.equals(DRAW_BY_INSUFFICIENT_MATERIAL) ||
                this.equals(DRAW_BY_FIFTY_MOVE_RULE);
    }

    public boolean isCheckmate() {
        return this.equals(WHITE_WINS_BY_CHECKMATE) ||
                this.equals(BLACK_WINS_BY_CHECKMATE);
    }

    public boolean isTimeout() {
        return this.equals(WHITE_WINS_BY_TIMEOUT) ||
                this.equals(BLACK_WINS_BY_TIMEOUT);
    }


}
