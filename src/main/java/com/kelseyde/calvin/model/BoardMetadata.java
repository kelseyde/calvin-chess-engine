package com.kelseyde.calvin.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class BoardMetadata {

    private Board board;
    private Colour turn;
    private Map<Colour, CastlingRights> castlingRights;
    private int enPassantTargetSquare;

}
