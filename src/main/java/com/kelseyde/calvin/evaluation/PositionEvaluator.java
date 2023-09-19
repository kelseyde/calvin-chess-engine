package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Game;

/**
 * Assigns the current position a numeric value based on some consideration.
 */
public interface PositionEvaluator {

    int evaluate(Game game);

}
