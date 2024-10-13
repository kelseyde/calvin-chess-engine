package com.kelseyde.calvin.board;

import static com.kelseyde.calvin.board.Move.*;

/**
 * Stores basic information for each chess piece type.
 */
public enum Piece {

    PAWN(0, 100, "p"),
    KNIGHT(1, 320, "n"),
    BISHOP(2, 330, "b"),
    ROOK(3, 500, "r"),
    QUEEN(4, 900, "q"),
    KING(5, 0, "k");

    public static final int COUNT = 6;

    final int index;

    final int value;

    final String code;

    Piece(int index, int value, String code) {
        this.index = index;
        this.value = value;
        this.code = code;
    }

    public int index() {
        return index;
    }

    public int value() {
        return value;
    }

    public String code() {
        return code;
    }

    public boolean isPawn() {
        return this == PAWN;
    }

    public boolean isKing() {
        return this == KING;
    }

    public boolean isMajor() {
        return this == ROOK || this == QUEEN;
    }

    public boolean isMinor() {
        return this == BISHOP || this == KNIGHT;
    }

    public boolean isSlider() {
        return this == BISHOP || this == ROOK || this == QUEEN;
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
