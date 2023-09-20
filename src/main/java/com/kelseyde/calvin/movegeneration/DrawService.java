package com.kelseyde.calvin.movegeneration;

import com.kelseyde.calvin.board.Game;
import com.kelseyde.calvin.board.result.DrawType;
import com.kelseyde.calvin.movegeneration.drawcalculator.FiftyMoveRuleCalculator;
import com.kelseyde.calvin.movegeneration.drawcalculator.InsufficientMaterialCalculator;
import com.kelseyde.calvin.movegeneration.drawcalculator.StalemateCalculator;
import com.kelseyde.calvin.movegeneration.drawcalculator.ThreefoldRepetitionCalculator;

import java.util.Optional;

public class DrawService {

    private final StalemateCalculator stalemateCalculator = new StalemateCalculator();

    private final ThreefoldRepetitionCalculator threefoldRepetitionCalculator = new ThreefoldRepetitionCalculator();

    private final InsufficientMaterialCalculator insufficientMaterialCalculator = new InsufficientMaterialCalculator();

    private final FiftyMoveRuleCalculator fiftyMoveRuleCalculator = new FiftyMoveRuleCalculator();


    public Optional<DrawType> calculateDraw(Game game) {
        if (stalemateCalculator.isDraw(game)) {
            return Optional.of(DrawType.STALEMATE);
        }
        if (threefoldRepetitionCalculator.isDraw(game)) {
            return Optional.of(DrawType.THREEFOLD_REPETITION);
        }
        if (insufficientMaterialCalculator.isDraw(game)) {
            return Optional.of(DrawType.INSUFFICIENT_MATERIAL);
        }
        if (fiftyMoveRuleCalculator.isDraw(game)) {
            return Optional.of(DrawType.FIFTY_MOVE_RULE);
        }
        return Optional.empty();
    }

}
