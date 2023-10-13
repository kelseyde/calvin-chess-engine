package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;

public interface EvaluationUpdater {

    void init(Board board, Evaluation evaluation, boolean isWhite);

    void update(Evaluation evaluation, Move move, boolean isWhite);

}
