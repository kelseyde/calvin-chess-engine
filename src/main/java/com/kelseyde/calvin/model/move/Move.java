package com.kelseyde.calvin.model.move;

import com.kelseyde.calvin.model.move.config.CastlingConfig;
import com.kelseyde.calvin.model.move.config.EnPassantConfig;
import com.kelseyde.calvin.model.move.config.PromotionConfig;
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

    private boolean isCheck;

    @Builder.Default
    private MoveType type = MoveType.STANDARD;

    @Builder.Default
    private EnPassantConfig enPassantConfig = EnPassantConfig.builder().build();

    @Builder.Default
    private CastlingConfig castlingConfig = CastlingConfig.builder().build();

    @Builder.Default
    private PromotionConfig promotionConfig = PromotionConfig.builder().build();

    public boolean moveMatches(Move move) {
        return startSquare == move.getStartSquare()
                && endSquare == move.getEndSquare()
                && promotionConfig.getPromotionPieceType() == move.getPromotionConfig().getPromotionPieceType();
    }

}
