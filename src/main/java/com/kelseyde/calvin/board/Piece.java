package com.kelseyde.calvin.board;

import lombok.Getter;

/**
 * Stores basic information for each chess piece type.
 */
@Getter
public enum Piece {

    PAWN(0, 100),
    KNIGHT(1, 320),
    BISHOP(2, 330),
    ROOK(3, 500),
    QUEEN(4, 900),
    KING(5, 0);

    final int index;

    final int value;

    Piece(int index, int value) {
        this.index = index;
        this.value = value;
    }

    public boolean isSlider() {
        return this == BISHOP || this == ROOK || this == QUEEN;
    }

}
