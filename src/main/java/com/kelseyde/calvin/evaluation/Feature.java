package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Piece;

/**
 * Represents a single feature used by the neural network. A feature is a piece on a square on the board, with a colour
 * (white or black). The feature can either be activated - meaning the piece is present on that square - or not
 * activated - meaning the piece is not present on that square. The presence or absence of a feature is represented by
 * a 1 or 0 respectively in the input layer.
 */
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
