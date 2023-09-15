package com.kelseyde.calvin.service.engine;

import com.kelseyde.calvin.model.Game;
import com.kelseyde.calvin.model.move.Move;

public interface Engine {

    Move selectMove(Game game);

}
