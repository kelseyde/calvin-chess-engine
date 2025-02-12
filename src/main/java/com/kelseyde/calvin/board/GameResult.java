package com.kelseyde.calvin.board;

public enum GameResult {
    WHITE_WIN(1),
    BLACK_WIN(-1),
    DRAW(0);

    public final int value;

    GameResult(int value) {
        this.value = value;
    }
}
