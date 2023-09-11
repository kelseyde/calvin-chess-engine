package com.kelseyde.calvin.model.game;

import com.kelseyde.calvin.model.Player;
import com.kelseyde.calvin.model.move.Move;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameAction {

    Player player;

    /**
     * What action the player takes: either play a move or resign the game.
     * TODO implement draw offer.
     */
    private ActionType actionType;

    private Move move;

}
