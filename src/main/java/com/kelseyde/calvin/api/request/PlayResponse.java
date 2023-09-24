package com.kelseyde.calvin.api.request;

import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.utils.NotationUtils;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayResponse {

    GameResult result;
    MoveResponse move;

    @Data
    @Builder
    public static class MoveResponse {
        String from;
        String to;
        String promotion;

        public static MoveResponse fromMove(Move move) {
            return MoveResponse.builder()
                    .from(NotationUtils.toNotation(move.getStartSquare()))
                    .to(NotationUtils.toNotation(move.getEndSquare()))
                    .promotion(getPromotion(move.getPieceType()))
                    .build();
        }

        public static String getPromotion(PieceType pieceType) {
            return switch (pieceType) {
                case QUEEN -> "q";
                case ROOK -> "r";
                case BISHOP -> "b";
                case KNIGHT -> "n";
                default -> null;
            };
        }
    }

}
