package com.kelseyde.calvin.model.move;

import com.kelseyde.calvin.model.MoveType;
import com.kelseyde.calvin.model.move.config.CastlingConfig;
import com.kelseyde.calvin.model.move.config.EnPassantConfig;
import com.kelseyde.calvin.model.move.config.PromotionConfig;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Move {

    @NotNull
    private final int startSquare;

    @NotNull
    private final int endSquare;

    @Builder.Default
    private MoveType type = MoveType.STANDARD;

    @Builder.Default
    private EnPassantConfig enPassantConfig = EnPassantConfig.builder().build();

    @Builder.Default
    private CastlingConfig castlingConfig = CastlingConfig.builder().build();

    @Builder.Default
    private PromotionConfig promotionConfig = PromotionConfig.builder().build();

}
