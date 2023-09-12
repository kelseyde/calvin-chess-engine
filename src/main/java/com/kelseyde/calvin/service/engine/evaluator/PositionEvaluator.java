package com.kelseyde.calvin.service.engine.evaluator;

import com.kelseyde.calvin.model.game.Game;

/**
 * Assigns the current position a numeric value based on some consideration.
 */
public interface PositionEvaluator {

    int evaluate(Game game);

}
