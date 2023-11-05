package com.kelseyde.calvin.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Stores the metadata for a given chess position - that is, the castling rights, en passant rights, the fifty-move counter
 * (the number of half-moves since the last capture or pawn move), and the last captured piece.
 * The game state history is stored by the {@link Board} to easily 'unmake' moves during search + evaluation.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameState {

    public static final int INITIAL_CASTLING_RIGHTS = 0b1111;
    public static final int CLEAR_WHITE_CASTLING_MASK = 0b1100;
    public static final int CLEAR_BLACK_CASTLING_MASK = 0b0011;
    public static final int CLEAR_WHITE_KINGSIDE_MASK = 0b1110;
    public static final int CLEAR_BLACK_KINGSIDE_MASK = 0b1011;
    public static final int CLEAR_WHITE_QUEENSIDE_MASK = 0b1101;
    public static final int CLEAR_BLACK_QUEENSIDE_MASK = 0b0111;

    long zobristKey = 0L;
    Piece capturedPiece;
    int enPassantFile = -1;
    int castlingRights = INITIAL_CASTLING_RIGHTS;
    int fiftyMoveCounter = 0;

    public boolean hasCastlingRights(boolean isWhite) {
        return isKingsideCastlingAllowed(isWhite) || isQueensideCastlingAllowed(isWhite);
    }

    public boolean isKingsideCastlingAllowed(boolean isWhite) {
        long kingsideMask = isWhite ? 0b0001 : 0b0100;
        return (castlingRights & kingsideMask) == kingsideMask;
    }

    public boolean isQueensideCastlingAllowed(boolean isWhite) {
        long queensideMask = isWhite ? 0b0010 : 0b1000;
        return (castlingRights & queensideMask) == queensideMask;
    }

}
