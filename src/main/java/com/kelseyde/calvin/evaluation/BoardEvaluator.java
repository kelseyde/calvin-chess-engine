package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;

/**
 * Assigns the current position a numeric value based on some consideration. The evaluation should be relative to the side
 * to move - meaning, if the side to move has an advantage the score should be positive, and vice versa.
 */
public interface BoardEvaluator {

    int evaluate(Board board, float gamePhase);

}
