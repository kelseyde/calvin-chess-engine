package com.kelseyde.calvin.service.drawcalculator;

import com.kelseyde.calvin.model.game.DrawType;
import com.kelseyde.calvin.model.game.Game;
import lombok.Getter;

public class ThreefoldRepetitionCalculator implements DrawCalculator {

    @Getter
    private final DrawType drawType = DrawType.THREEFOLD_REPETITION;

    @Override
    public boolean isDraw(Game game) {
        return false;
    }

}
