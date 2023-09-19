package com.kelseyde.calvin.api.request;

import com.kelseyde.calvin.board.PieceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayRequest {

    private String gameId;
    private String startSquare;
    private String endSquare;
    private PieceType promotionPieceType;

}
