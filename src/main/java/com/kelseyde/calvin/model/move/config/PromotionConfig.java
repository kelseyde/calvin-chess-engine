package com.kelseyde.calvin.model.move.config;

import com.kelseyde.calvin.model.PieceType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromotionConfig {

    @Builder.Default
    private PieceType promotionPieceType = null;

}
