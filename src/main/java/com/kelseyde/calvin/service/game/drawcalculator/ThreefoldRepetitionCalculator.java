package com.kelseyde.calvin.service.game.drawcalculator;

import com.kelseyde.calvin.model.RepetitionMetadata;
import com.kelseyde.calvin.model.DrawType;
import com.kelseyde.calvin.model.Game;
import lombok.Getter;

import java.util.stream.Collectors;

public class ThreefoldRepetitionCalculator implements DrawCalculator {

    @Getter
    private final DrawType drawType = DrawType.THREEFOLD_REPETITION;

    @Override
    public boolean isDraw(Game game) {
       return game.getBoardHistory()
                .stream()
                .collect(Collectors.groupingBy(board -> RepetitionMetadata.fromBoard(board).toString()))
                .values()
                .stream()
                .anyMatch(positions -> positions.size() == 3);
    }

}
