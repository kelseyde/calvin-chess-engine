package com.kelseyde.calvin.board.piece;

public class Piece {

    public static String getPieceCode(boolean isWhiteToMove, PieceType type) {
        String colourCode = isWhiteToMove ? "w" : "b";
        String pieceCode = type.getPieceCode();
        return colourCode + pieceCode;
    }

}
