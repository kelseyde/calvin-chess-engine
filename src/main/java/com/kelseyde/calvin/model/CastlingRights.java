package com.kelseyde.calvin.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CastlingRights {

    private boolean whiteKingSide = true;
    private boolean whiteQueenSide = true;
    private boolean blackKingSide = true;
    private boolean blackQueenSide = true;

}
