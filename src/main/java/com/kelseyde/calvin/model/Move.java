package com.kelseyde.calvin.model;

import lombok.Builder;
import lombok.Data;

@Data
public class Move {

    private final int startSquare;
    private final int endSquare;

    private boolean isEnPassantCapture = false;
    private int enPassantTargetSquare = -1;

}
