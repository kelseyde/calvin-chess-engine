package com.kelseyde.calvin.model.api;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.game.ActionResult;
import com.kelseyde.calvin.utils.MoveUtils;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Data
@Builder
public class PlayResponse {

    ActionResult result;

    Map<String, String> position;

    public static PlayResponse.PlayResponseBuilder fromBoard(Board board) {
        Map<String, String> position = new HashMap<>();
        IntStream.range(0, 64)
                .forEach(i -> {
                    board.getPieceAt(i).ifPresent(piece -> {
                        String square = MoveUtils.toNotation(i);
                        String pieceCode = piece.toPieceCode();
                        position.put(square, pieceCode);
                    });
                });
        return PlayResponse.builder().position(position);
    }

}
