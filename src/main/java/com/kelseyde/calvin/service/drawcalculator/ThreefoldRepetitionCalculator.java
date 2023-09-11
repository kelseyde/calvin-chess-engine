package com.kelseyde.calvin.service.drawcalculator;

import com.kelseyde.calvin.model.BoardMetadata;
import com.kelseyde.calvin.model.game.DrawType;
import com.kelseyde.calvin.model.game.Game;
import lombok.Getter;

import java.util.stream.Collectors;

public class ThreefoldRepetitionCalculator implements DrawCalculator {

    @Getter
    private final DrawType drawType = DrawType.THREEFOLD_REPETITION;

    @Override
    public boolean isDraw(Game game) {
       return game.getBoardHistory()
                .stream()
                .collect(Collectors.groupingBy(BoardMetadata::toString))
                .values()
                .stream()
                .anyMatch(positions -> positions.size() == 3);
    }

}
