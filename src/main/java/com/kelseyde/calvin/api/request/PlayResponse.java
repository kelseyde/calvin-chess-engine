package com.kelseyde.calvin.api.request;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.NotationUtils;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Data
@Builder
public class PlayResponse {

    GameResult result;

    Map<String, String> position;

    public static PlayResponse.PlayResponseBuilder fromBoard(Board board) {
        Map<String, String> position = new HashMap<>();
        IntStream.range(0, 64)
                .forEach(i -> {
                    BoardUtils.pieceCodeAt(board, i).ifPresent(pieceCode -> {
                        String square = NotationUtils.toNotation(i);
                        position.put(square, pieceCode);
                    });
                });
        return PlayResponse.builder().position(position);
    }

}
