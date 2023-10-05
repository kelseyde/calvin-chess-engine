package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GamePhaseCalculatorTest {

    private final GamePhaseCalculator phaseCalculator = new GamePhaseCalculator();

    @Test
    public void testStartingPosition() {

        Board board = new Board();

        float value = phaseCalculator.calculate(board);

        Assertions.assertEquals(1, value);

    }

}