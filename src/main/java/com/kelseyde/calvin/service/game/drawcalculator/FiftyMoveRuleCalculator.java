package com.kelseyde.calvin.service.game.drawcalculator;

import com.kelseyde.calvin.model.game.DrawType;
import com.kelseyde.calvin.model.game.Game;
import lombok.Getter;

public class FiftyMoveRuleCalculator implements DrawCalculator {

    @Getter
    private final DrawType drawType = DrawType.FIFTY_MOVE_RULE;

    @Override
    public boolean isDraw(Game game) {
        return game.getHalfMoveClock() >= 100;
    }

}
