package com.kelseyde.calvin.movegeneration.drawcalculator;

import com.kelseyde.calvin.board.Game;
import com.kelseyde.calvin.board.result.DrawType;

public interface DrawCalculator {

    DrawType getDrawType();

    boolean isDraw(Game game);

}
