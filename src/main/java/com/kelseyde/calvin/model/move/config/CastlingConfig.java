package com.kelseyde.calvin.model.move.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CastlingConfig {

    @Builder.Default
    private int rookStartSquare = -1;
    @Builder.Default
    private int rookEndSquare = -1;

    private boolean negatesKingsideCastling;
    private boolean negatesQueensideCastling;

}
