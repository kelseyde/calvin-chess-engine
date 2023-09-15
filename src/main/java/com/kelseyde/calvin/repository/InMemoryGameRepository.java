package com.kelseyde.calvin.repository;

import com.kelseyde.calvin.model.Game;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryGameRepository implements GameRepository {

    private final Map<String, Game> gameMap = new HashMap<>();

    @Override
    public Optional<Game> getGame(String gameId) {
        return Optional.ofNullable(gameMap.get(gameId));
    }

    @Override
    public void putGame(Game game) {
        gameMap.put(game.getId(), game);
    }

}
