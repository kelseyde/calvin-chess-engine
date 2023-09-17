package com.kelseyde.calvin.service.evaluator;

import com.kelseyde.calvin.model.Game;

/**
 * Assigns the current position a numeric value based on some consideration.
 */
public interface PositionEvaluator {

    int evaluate(Game game);

}
