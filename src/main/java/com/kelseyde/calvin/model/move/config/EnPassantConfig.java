package com.kelseyde.calvin.model.move.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnPassantConfig {

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



}
