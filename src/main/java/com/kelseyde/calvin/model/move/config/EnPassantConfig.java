package com.kelseyde.calvin.model.move.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnPassantConfig {

    @Builder.Default
    private int enPassantTargetSquare = -1;

}
