package com.kelseyde.calvin.model.move;

import com.kelseyde.calvin.model.PieceType;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

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
     * Whether this move captures an opponent piece.
     */
    private boolean isCapture;

    /**
     * Whether this move negates kingside castling for the remainder of the game.
     */
    private boolean negatesKingsideCastling;

    /**
     * Whether this move negates queenside castling for the remainder of the game.
     */
    private boolean negatesQueensideCastling;

    /**
     * The destination square for a pawn capturing en passant. Should be set by a double pawn move that
     * enables en passant on the next turn.
     */
    @Builder.Default
    private int enPassantTargetSquare = -1;

    /**
     * The square of a pawn just captured en passant. Should be set by a pawn capturing en passant.
     */
    @Builder.Default
    private int enPassantCapturedSquare = -1;

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
     * In case of castling, the squares the king must travel through (including the starting square).
     */
    @Builder.Default
    private Set<Integer> kingTravelSquares = Collections.emptySet();

    /**
     * In case of promotion, what piece type the pawn should promote to.
     */
    @Builder.Default
    private PieceType promotionPieceType = null;

    public boolean moveMatches(Move move) {
        return startSquare == move.getStartSquare()
                && endSquare == move.getEndSquare()
                && promotionPieceType == move.getPromotionPieceType();
    }

}
