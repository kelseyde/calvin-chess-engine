package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Piece;

/**
 * Represents a single feature used by the neural network. A feature is a piece on a square on the board, with a colour
 * (white or black). The feature can either be activated - meaning the piece is present on that square - or not
 * activated - meaning the piece is not present on that square. The presence or absence of a feature is represented by
 * a 1 or 0 respectively in the input layer.
 */
public class Feature {

    private Feature() {}

    // Bit positions
    private static final int PIECE_BITS = 3;  // 3 bits can encode up to 8 piece types
    private static final int SQUARE_BITS = 6; // 6 bits needed for 64 squares
    private static final int COLOR_BITS = 1;  // 1 bit for color

    // Bit masks
    private static final int PIECE_MASK = (1 << PIECE_BITS) - 1;
    private static final int SQUARE_MASK = (1 << SQUARE_BITS) - 1;
    private static final int COLOR_MASK = 1;

    // Bit shifts
    private static final int PIECE_SHIFT = 0;
    private static final int SQUARE_SHIFT = PIECE_BITS;
    private static final int COLOR_SHIFT = PIECE_BITS + SQUARE_BITS;

    /**
     * Encodes a feature (piece, square, color) into a compact 16-bit representation.
     */
    public static short encode(Piece piece, int square, boolean white) {
        int encoded = 0;
        encoded |= (piece.index() & PIECE_MASK) << PIECE_SHIFT;
        encoded |= (square & SQUARE_MASK) << SQUARE_SHIFT;
        encoded |= ((white ? 1 : 0) & COLOR_MASK) << COLOR_SHIFT;
        return (short) encoded;
    }

    /**
     * Decodes the piece from an encoded feature.
     */
    public static Piece decodePiece(short encoded) {
        int index = (encoded >> PIECE_SHIFT) & PIECE_MASK;
        return Piece.values()[index];
    }

    /**
     * Decodes the square from an encoded feature.
     */
    public static int decodeSquare(short encoded) {
        return (encoded >> SQUARE_SHIFT) & SQUARE_MASK;
    }

    /**
     * Decodes the color from an encoded feature.
     */
    public static boolean decodeColor(short encoded) {
        return (((encoded >> COLOR_SHIFT) & COLOR_MASK) == 1);
    }

    /**
     * Calculates the feature index for neural network lookup, equivalent to the original Feature.index method.
     */
    public static int index(short encoded, boolean whitePerspective, boolean mirror) {
        Piece piece = decodePiece(encoded);
        int square = decodeSquare(encoded);
        boolean white = decodeColor(encoded);

        // Calculate the square index with perspective and mirror
        int squareIndex = whitePerspective ? square : Square.flipRank(square);
        if (mirror) squareIndex = Square.flipFile(squareIndex);

        // Calculate the full index
        final int pieceIndex = piece.index();
        final int pieceOffset = pieceIndex * Square.COUNT;
        final boolean ourPiece = white == whitePerspective;
        final int colourOffset = ourPiece ? 0 : (Square.COUNT * Piece.COUNT);

        return colourOffset + pieceOffset + squareIndex;
    }

}
