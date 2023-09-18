package com.kelseyde.calvin.model;

import lombok.Data;

import java.util.Set;

public class Piece {

    public static String getPieceCode(Colour colour, PieceType type) {
        String colourCode = Colour.WHITE.equals(colour) ? "w" : "b";
        String pieceCode = type.pieceCode;
        return colourCode + pieceCode;
    }

}
