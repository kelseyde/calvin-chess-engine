package com.kelseyde.calvin.endgame;

public class TablebaseException extends RuntimeException {

    public TablebaseException(String message) {
        super(message);
    }

    public TablebaseException(String message, Exception cause) {
        super(message, cause);
    }

}