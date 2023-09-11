package com.kelseyde.calvin.model.move.config;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

@Data
@Builder
public class CastlingConfig {

    @Builder.Default
    private int rookStartSquare = -1;
    @Builder.Default
    private int rookEndSquare = -1;
    @Builder.Default
    private Set<Integer> kingTravelSquares = Collections.emptySet();

    private boolean negatesKingsideCastling;
    private boolean negatesQueensideCastling;

}
