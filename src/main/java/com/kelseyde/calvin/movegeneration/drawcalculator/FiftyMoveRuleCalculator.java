package com.kelseyde.calvin.movegeneration.drawcalculator;

import com.kelseyde.calvin.board.DrawType;
import com.kelseyde.calvin.board.Game;
import lombok.Getter;

public class FiftyMoveRuleCalculator implements DrawCalculator {

    @Getter
    private final DrawType drawType = DrawType.FIFTY_MOVE_RULE;

    @Override
    public boolean isDraw(Game game) {
        return game.getBoard().getHalfMoveCounter() >= 100;
    }

}
