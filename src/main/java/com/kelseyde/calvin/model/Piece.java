package com.kelseyde.calvin.model;

public class Piece {

    public static String getPieceCode(Colour colour, PieceType type) {
        String colourCode = Colour.WHITE.equals(colour) ? "w" : "b";
        String pieceCode = type.pieceCode;
        return colourCode + pieceCode;
    }

}
