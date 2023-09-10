package com.kelseyde.calvin.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
public class Move {

    @NotNull
    private final int startSquare;

    @NotNull
    private final int endSquare;

    private boolean isEnPassantCapture = false;
    private int enPassantTargetSquare = -1;

    private PieceType promotionPieceType = null;

    public Optional<PieceType> getPromotionPieceType() {
        return Optional.ofNullable(promotionPieceType);
    }

}
