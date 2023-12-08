package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.score.Score;

public interface Evaluation {

    int evaluate(Board board);

    Score getScore();

}
