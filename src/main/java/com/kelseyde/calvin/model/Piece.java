package com.kelseyde.calvin.model;

import lombok.Data;

import java.util.Set;

@Data
public class Piece {

    private static final Set<Character> PIECE_CHARS = Set.of(
            'K', 'Q', 'R', 'B', 'N', 'P', 'k', 'q', 'r', 'b', 'n', 'p'
    );

    private final Colour colour;
    private final PieceType type;

    public boolean isColour(Colour colour) {
        return this.colour.equals(colour);
    }

    public boolean isType(PieceType type) {
        return this.type.equals(type);
    }

    public Piece copy() {
        return new Piece(colour, type);
    }

    private static Colour getColour(char pieceChar) {
        return Character.isUpperCase(pieceChar) ? Colour.WHITE : Colour.BLACK;
    }

    public static Piece fromChar(char pieceChar) {
        if (!PIECE_CHARS.contains(pieceChar)) {
            return null;
        }
        Colour colour = getColour(pieceChar);
        PieceType type = PieceType.fromChar(pieceChar);
        return new Piece(colour, type);
    }

    public char toChar() {
        char pieceTypeChar = type.toChar();
        return Colour.WHITE.equals(colour) ? pieceTypeChar : Character.toLowerCase(pieceTypeChar);
    }

}
