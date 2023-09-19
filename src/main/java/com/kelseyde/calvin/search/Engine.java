package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Game;
import com.kelseyde.calvin.board.move.Move;

public interface Engine {

    Move selectMove(Game game);

}
