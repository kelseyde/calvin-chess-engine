package com.kelseyde.calvin.service.game.drawcalculator;

import com.kelseyde.calvin.model.game.DrawType;
import com.kelseyde.calvin.model.game.Game;

public interface DrawCalculator {

    DrawType getDrawType();

    boolean isDraw(Game game);

}
