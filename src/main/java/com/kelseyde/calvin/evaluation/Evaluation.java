package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.score.Material;

public interface Evaluation {

    int evaluate(Board board);

    Material getMaterial(boolean isWhite);

}
