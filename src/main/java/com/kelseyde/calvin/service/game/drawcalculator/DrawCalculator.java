package com.kelseyde.calvin.service.game.drawcalculator;

import com.kelseyde.calvin.model.DrawType;
import com.kelseyde.calvin.model.Game;

public interface DrawCalculator {

    DrawType getDrawType();

    boolean isDraw(Game game);

}
