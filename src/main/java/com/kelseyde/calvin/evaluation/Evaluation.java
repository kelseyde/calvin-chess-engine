package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;

public interface Evaluation {

    int evaluate();

    void setPosition(Board board);

    void makeMove(Board board, Move move);

    void unmakeMove();

    void clearHistory();

}
