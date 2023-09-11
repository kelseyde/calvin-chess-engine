package com.kelseyde.calvin.model.move;

import com.kelseyde.calvin.model.game.DrawType;
import lombok.Data;

@Data
public class MoveResult {

    private boolean isLegal;
    private Move legalMove;

    private boolean isCheckmate;

    private boolean isDraw;
    private DrawType drawType;

}
