package com.kelseyde.calvin.model.piece;

import com.kelseyde.calvin.model.Colour;
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

    public static Piece fromChar(char pieceChar) {
        if (!PIECE_CHARS.contains(pieceChar)) {
            return null;
        }
        Colour colour = getColour(pieceChar);
        PieceType type = getPieceType(pieceChar);
        return new Piece(colour, type);
    }

    private static Colour getColour(char pieceChar) {
        return Character.isUpperCase(pieceChar) ? Colour.WHITE : Colour.BLACK;
    }

    private static PieceType getPieceType(char pieceChar) {
        return switch (Character.toUpperCase(pieceChar)) {
            case 'K' -> PieceType.KING;
            case 'Q' -> PieceType.QUEEN;
            case 'R' -> PieceType.ROOK;
            case 'B' -> PieceType.BISHOP;
            case 'N' -> PieceType.KNIGHT;
            case 'P' -> PieceType.PAWN;
            default -> throw new IllegalArgumentException(String.format("%s is not a valid piece character!", pieceChar));
        };
    }

}
