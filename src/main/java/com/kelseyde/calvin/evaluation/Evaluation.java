package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.evaluation.score.Material;

public interface Evaluation {

    void init(Board board);

    void makeMove(Move move);

    void unmakeMove();

    Material getMaterial(boolean isWhite);

    int get();

}
