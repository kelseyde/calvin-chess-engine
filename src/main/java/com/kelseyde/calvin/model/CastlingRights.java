package com.kelseyde.calvin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CastlingRights {

    private boolean kingSide = true;
    private boolean queenSide = true;

    public CastlingRights copy() {
        return new CastlingRights(kingSide, queenSide);
    }

}
