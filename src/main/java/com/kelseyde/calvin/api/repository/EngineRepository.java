package com.kelseyde.calvin.api.repository;

import com.kelseyde.calvin.api.request.Game;
import com.kelseyde.calvin.search.iterative.IterativeDeepeningSearch;
import com.kelseyde.calvin.search.negamax.NegamaxSearch;

import java.util.Optional;

public interface EngineRepository {

    Optional<IterativeDeepeningSearch> getEngine(String boardId);

    void putEngine(IterativeDeepeningSearch engine);

}
