package com.kelseyde.calvin.utils.fen;

public class InvalidFENException extends RuntimeException {

    public InvalidFENException(String fen, Exception cause) {
        super("Invalid FEN: " + fen, cause);
    }

    public InvalidFENException(String fen, String message) {
        super("Invalid FEN: " + fen + ", " + message);
    }

}
