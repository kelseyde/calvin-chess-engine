package com.kelseyde.calvin.board.move;

import com.kelseyde.calvin.board.piece.PieceType;
import lombok.Builder;
import lombok.Data;

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
     * Whether this move comes with check.
     */
    private boolean isCheck;

    /**
     * The bitboard representing the destination square for a pawn capturing en passant. Should be set by
     * a double pawn move that enables en passant on the next turn.
     */
    @Builder.Default
    private long enPassantTarget = 0L;

    /**
     * The bitboard representing the square of a pawn just captured en passant. Should be set by a pawn capturing en passant.
     */
    @Builder.Default
    private long enPassantCapture = 0L;

    /**
     * In case of castling, the start square of the castling rook.
     */
    @Builder.Default
    private int rookStartSquare = -1;

    /**
     * In case of castling, the end square of the castling rook.
     */
    @Builder.Default
    private int rookEndSquare = -1;

    /**
     * In case of promotion, what piece type the pawn should promote to.
     */
    @Builder.Default
    private PieceType promotionPieceType = null;

    // TODO remove
    public MoveKey getKey() {
        return new MoveKey(startSquare, endSquare, promotionPieceType);
    }

    public boolean moveMatches(Move move) {
        return startSquare == move.getStartSquare()
                && endSquare == move.getEndSquare()
                && promotionPieceType == move.getPromotionPieceType();
    }

}
