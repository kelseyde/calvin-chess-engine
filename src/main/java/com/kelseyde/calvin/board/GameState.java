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
public class GameState {

    long zobrist;
    long pawnZobrist;
    Piece capturedPiece;
    int enPassantFile;
    int castlingRights;
    int halfMoveClock;

    public GameState(Board board) {
        this.zobrist = Zobrist.generateKey(board);
        this.pawnZobrist = Zobrist.generatePawnKey(board);
        this.capturedPiece = null;
        this.enPassantFile = -1;
        this.castlingRights = Bits.INITIAL_CASTLING_RIGHTS;
        this.halfMoveClock = 0;
    }

    public boolean hasCastlingRights(boolean white) {
        return isKingsideCastlingAllowed(white) || isQueensideCastlingAllowed(white);
    }

    public boolean isKingsideCastlingAllowed(boolean white) {
        long kingsideMask = white ? 0b0001 : 0b0100;
        return (castlingRights & kingsideMask) == kingsideMask;
    }

    public boolean isQueensideCastlingAllowed(boolean white) {
        long queensideMask = white ? 0b0010 : 0b1000;
        return (castlingRights & queensideMask) == queensideMask;
    }

    public GameState copy() {
        return new GameState(zobrist, pawnZobrist, capturedPiece, enPassantFile, castlingRights, halfMoveClock);
    }

}
