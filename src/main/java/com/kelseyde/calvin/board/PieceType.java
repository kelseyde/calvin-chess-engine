package com.kelseyde.calvin.board;

import lombok.Getter;

@Getter
public enum PieceType {
    PAWN(0),
    KNIGHT(1),
    BISHOP(2),
    ROOK(3),
    QUEEN(4),
    KING(5);

    final int index;
    PieceType(int index) {
        this.index = index;
    }

}
