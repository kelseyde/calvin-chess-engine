package com.kelseyde.calvin.model.api;

import com.kelseyde.calvin.model.PieceType;
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
