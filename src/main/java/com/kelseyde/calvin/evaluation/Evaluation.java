package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;

public interface Evaluation {

    int evaluate(Board board);

    void clearHistory();

}
