package com.kelseyde.calvin.model.result;

public abstract class GameResult {

    public abstract ResultType getResultType();

    public enum ResultType {
        WIN,
        DRAW,
        NEXT_MOVE,
        ILLEGAL_MOVE
    }

}
