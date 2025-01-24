package com.kelseyde.calvin.board;

import static com.kelseyde.calvin.board.Move.*;

/**
 * Stores basic information for each chess piece type.
 */
public enum Piece {

    PAWN    (0, "p"),
    KNIGHT  (1, "n"),
    BISHOP  (2, "b"),
    ROOK    (3, "r"),
    QUEEN   (4, "q"),
    KING    (5, "k");

    public static final int COUNT = 6;
    public static final int WHITE_PIECES = 6;
    public static final int BLACK_PIECES = 7;

    final int index;

    final String code;

    Piece(int index, String code) {
        this.index = index;
        this.code = code;
    }

    public int index() {
        return index;
    }

    public String code() {
        return code;
    }

    public boolean isSlider() {
        return this == BISHOP || this == ROOK || this == QUEEN;
    }

    public boolean isMajor() {
        return this == ROOK || this == QUEEN || this == KING;
    }

    public static short promoFlag(Piece piece) {
        if (piece == null) {
            return NO_FLAG;
        }
        return switch (piece) {
            case QUEEN -> PROMOTE_TO_QUEEN_FLAG;
            case ROOK -> PROMOTE_TO_ROOK_FLAG;
            case BISHOP -> PROMOTE_TO_BISHOP_FLAG;
            case KNIGHT -> PROMOTE_TO_KNIGHT_FLAG;
            default -> NO_FLAG;
        };
    }

}
