package com.kelseyde.calvin.api.repository;

import com.kelseyde.calvin.api.request.Game;
import com.kelseyde.calvin.search.negamax.NegamaxSearch;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryEngineRepository implements EngineRepository {

    private final Map<String, NegamaxSearch> engineMap = new HashMap<>();

    @Override
    public Optional<NegamaxSearch> getEngine(String boardId) {
        return Optional.ofNullable(engineMap.get(boardId));
    }

    @Override
    public void putEngine(NegamaxSearch engine) {
        engineMap.put(engine.getBoard().getId(), engine);
    }

}
