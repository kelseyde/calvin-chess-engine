package com.kelseyde.calvin.repository;

import com.kelseyde.calvin.model.game.Game;

import java.util.Optional;

public interface GameRepository {

    Optional<Game> getGame(String gameId);

    void putGame(Game game);

}
