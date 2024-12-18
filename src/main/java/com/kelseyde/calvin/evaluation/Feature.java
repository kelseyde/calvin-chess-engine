package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Piece;

public record Feature(Piece piece, int square, boolean white) {

    public int index(boolean whitePerspective, boolean mirror) {
        final int squareIndex = squareIndex(whitePerspective, mirror);
        final int pieceIndex = piece.index();
        final int pieceOffset = pieceIndex * Square.COUNT;
        final boolean ourPiece = white == whitePerspective;
        final int colourOffset = ourPiece ? 0 : (Square.COUNT * Piece.COUNT);
        return colourOffset + pieceOffset + squareIndex;
    }

    private int squareIndex(boolean whitePerspective, boolean mirror) {
        int squareIndex = whitePerspective ? square : Square.flipRank(square);
        if (mirror) squareIndex = Square.flipFile(squareIndex);
        return squareIndex;
    }

}
