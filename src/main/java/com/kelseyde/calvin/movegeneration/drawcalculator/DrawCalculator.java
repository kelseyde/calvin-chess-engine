package com.kelseyde.calvin.movegeneration.drawcalculator;

import com.kelseyde.calvin.board.result.DrawType;
import com.kelseyde.calvin.board.Game;

public interface DrawCalculator {

    DrawType getDrawType();

    boolean isDraw(Game game);

}
