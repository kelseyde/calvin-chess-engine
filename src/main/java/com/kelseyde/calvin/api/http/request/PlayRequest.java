package com.kelseyde.calvin.api.http.request;

import com.kelseyde.calvin.board.piece.PieceType;
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

    public PieceType getPromotionPieceType() {
        if (promotion == null) {
            return null;
        }
        return switch (promotion) {
            case "q" -> PieceType.QUEEN;
            case "r" -> PieceType.ROOK;
            case "b" -> PieceType.BISHOP;
            case "n" -> PieceType.KNIGHT;
            default -> null;
        };
    }

}
