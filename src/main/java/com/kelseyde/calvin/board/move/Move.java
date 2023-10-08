package com.kelseyde.calvin.board.move;

import com.kelseyde.calvin.board.piece.PieceType;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

/**
 * A legal chess move, indicating a start square, end square, and any special rules (en passant, castling, promotion
 */
@Data
@Builder
public class Move {

    private final int startSquare;

    private final int endSquare;

    /**
     * The type of piece making the move.
     */
    private PieceType pieceType;

    /**
     * The type of move this is (standard, en passant, castling or promotion).
     */
    @Builder.Default
    private MoveType moveType = MoveType.STANDARD;

    /**
     * The bitboard representing the destination square for a pawn capturing en passant. Should be set by
     * a double pawn move that enables en passant on the next turn.
     */
    @Builder.Default
    private int enPassantFile = -1;

    /**
     * In case of promotion, what piece type the pawn should promote to.
     */
    @Builder.Default
    private PieceType promotionPieceType = null;

    public boolean matches(Move move) {
        boolean squareMatch = startSquare == move.getStartSquare() && endSquare == move.getEndSquare();
        boolean promotionMatch = Optional.ofNullable(promotionPieceType)
                .map(piece -> piece.equals(move.getPromotionPieceType()))
                .orElse(true);
        return squareMatch && promotionMatch;
    }

}
