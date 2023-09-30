package com.kelseyde.calvin.api.http.repository;

import com.kelseyde.calvin.search.iterative.IterativeDeepeningSearch;

import java.util.Optional;

public interface EngineRepository {

    Optional<IterativeDeepeningSearch> getEngine(String boardId);

    void putEngine(IterativeDeepeningSearch engine);

}
