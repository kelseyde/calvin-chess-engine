package com.kelseyde.calvin.model.api;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.result.GameResult;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class PlayResponse {

    GameResult result;

    Map<String, String> position;

    public static PlayResponse.PlayResponseBuilder fromBoard(Board board) {
        Map<String, String> position = new HashMap<>();
        // TODO fix ui
//        IntStream.range(0, 64)
//                .forEach(i -> {
//                    board.getPieceAt(i).ifPresent(piece -> {
//                        String square = MoveUtils.toNotation(i);
//                        String pieceCode = piece.toPieceCode();
//                        position.put(square, pieceCode);
//                    });
//                });
        return PlayResponse.builder().position(position);
    }

}
