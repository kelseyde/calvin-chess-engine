package com.kelseyde.calvin.service.game;

import com.kelseyde.calvin.model.game.DrawType;
import com.kelseyde.calvin.model.game.Game;
import com.kelseyde.calvin.service.game.drawcalculator.*;

import java.util.List;
import java.util.Optional;

public class DrawService {

    private final List<DrawCalculator> drawCalculators = List.of(
            new StalemateCalculator(),
            new ThreefoldRepetitionCalculator(),
            new InsufficientMaterialCalculator(),
            new FiftyMoveRuleCalculator()
    );

    public Optional<DrawType> calculateDraw(Game game) {
        return drawCalculators.stream()
                .filter(calculator -> calculator.isDraw(game))
                .findFirst()
                .map(DrawCalculator::getDrawType);
    }

}
