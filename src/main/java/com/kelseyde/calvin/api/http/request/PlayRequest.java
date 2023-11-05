package com.kelseyde.calvin.api.http.request;

import com.kelseyde.calvin.board.Piece;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayRequest {

    private String gameId;
    private String from;
    private String to;
    private String promotion;
    private Integer thinkTimeMs;

    public Piece getPromotionPieceType() {
        if (promotion == null) {
            return null;
        }
        return switch (promotion) {
            case "q" -> Piece.QUEEN;
            case "r" -> Piece.ROOK;
            case "b" -> Piece.BISHOP;
            case "n" -> Piece.KNIGHT;
            default -> null;
        };
    }

}
