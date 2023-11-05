package com.kelseyde.calvin.board;

import lombok.Getter;

@Getter
public enum Piece {

    PAWN(0),
    KNIGHT(1),
    BISHOP(2),
    ROOK(3),
    QUEEN(4),
    KING(5);

    final int index;

    Piece(int index) {
        this.index = index;
    }

    public boolean isSlider() {
        return this == BISHOP || this == ROOK || this == QUEEN;
    }

}
