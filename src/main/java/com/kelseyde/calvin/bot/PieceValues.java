package com.kelseyde.calvin.bot;

import com.kelseyde.calvin.board.PieceType;

public interface PieceValues {

    PieceValues DEFAULT = pieceType -> switch (pieceType) {
        case PAWN -> 100;
        case KNIGHT -> 320;
        case BISHOP -> 330;
        case ROOK -> 500;
        case QUEEN -> 900;
        case KING -> 10000;
    };

    int get(PieceType pieceType);

}
