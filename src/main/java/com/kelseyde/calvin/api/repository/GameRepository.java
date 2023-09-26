package com.kelseyde.calvin.api.repository;

import com.kelseyde.calvin.api.request.Game;

import java.util.Optional;

public interface GameRepository {

    Optional<Game> getGame(String gameId);

    void putGame(Game game);

}
