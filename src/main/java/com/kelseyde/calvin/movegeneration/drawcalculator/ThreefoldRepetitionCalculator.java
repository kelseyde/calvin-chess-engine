package com.kelseyde.calvin.movegeneration.drawcalculator;

import com.kelseyde.calvin.board.result.DrawType;
import com.kelseyde.calvin.board.Game;
import com.kelseyde.calvin.board.RepetitionMetadata;
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
