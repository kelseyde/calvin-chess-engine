package com.kelseyde.calvin.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RepetitionMetadata {

    private Piece[] squares;
    private Colour turn;
    private Map<Colour, CastlingRights> castlingRights;
    private int enPassantTargetSquare;

    public static RepetitionMetadata fromBoard(Board board) {
        return RepetitionMetadata.builder()
                .squares(board.getSquares())
                .turn(board.getTurn())
                .castlingRights(board.getCastlingRights())
                .enPassantTargetSquare(board.getEnPassantTargetSquare())
                .build();

    }

}
