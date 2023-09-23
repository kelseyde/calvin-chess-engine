package com.kelseyde.calvin.search.engine;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;

public interface Engine {

    Move selectMove(Board board);

}
