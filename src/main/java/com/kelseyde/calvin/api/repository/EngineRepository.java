package com.kelseyde.calvin.api.repository;

import com.kelseyde.calvin.search.iterative.IterativeDeepeningSearch;

import java.util.Optional;

public interface EngineRepository {

    Optional<IterativeDeepeningSearch> getEngine(String boardId);

    void putEngine(IterativeDeepeningSearch engine);

}
