package com.kelseyde.calvin.api.http.request;

import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.utils.NotationUtils;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoveResponse {
    String from;
    String to;
    String promotion;

    public static MoveResponse fromMove(Move move) {
        return MoveResponse.builder()
                .from(NotationUtils.toNotation(move.getStartSquare()))
                .to(NotationUtils.toNotation(move.getEndSquare()))
                .promotion(getPromotion(move.getPromotionPieceType()))
                .build();
    }

    public static String getPromotion(PieceType pieceType) {
        if (pieceType == null) {
            return null;
        }
        return switch (pieceType) {
            case QUEEN -> "q";
            case ROOK -> "r";
            case BISHOP -> "b";
            case KNIGHT -> "n";
            default -> null;
        };
    }
}
