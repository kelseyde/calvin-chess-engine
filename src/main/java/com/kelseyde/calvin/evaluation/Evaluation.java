package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;

public interface Evaluation {

    int evaluate(Board board);

    int evaluate(Board board, boolean lazy, int alpha, int beta);

    Score getScore();

}
