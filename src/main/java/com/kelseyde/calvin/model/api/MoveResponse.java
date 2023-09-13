package com.kelseyde.calvin.model.api;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.utils.MoveUtils;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Data
@Builder
public class MoveResponse {

    Map<String, String> position;

    public static MoveResponse fromBoard(Board board) {
        Map<String, String> position = new HashMap<>();
        IntStream.range(0, 64)
                .forEach(i -> {
                    board.pieceAt(i).ifPresent(piece -> {
                        String square = MoveUtils.toNotation(i);
                        String pieceCode = piece.toPieceCode();
                        position.put(square, pieceCode);
                    });
                });
        return MoveResponse.builder().position(position).build();
    }

}
