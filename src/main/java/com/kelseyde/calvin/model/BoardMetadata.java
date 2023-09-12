package com.kelseyde.calvin.model;

import com.kelseyde.calvin.model.game.Game;
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

    public static BoardMetadata fromGame(Game game) {
        return BoardMetadata.builder()
                .board(game.getBoard().copy())
                .turn(game.getTurn())
                .castlingRights(game.getCastlingRights())
                .enPassantTargetSquare(game.getEnPassantTargetSquare())
                .build();
    }

}
