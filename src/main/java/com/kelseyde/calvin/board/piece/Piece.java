package com.kelseyde.calvin.board.piece;

public class Piece {

    public static String getPieceCode(boolean isWhite, PieceType type) {
        String colourCode = isWhite ? "w" : "b";
        String pieceCode = type.getPieceCode();
        return colourCode + pieceCode;
    }

}
