package com.kelseyde.calvin.api.repository;

import com.kelseyde.calvin.api.request.Game;
import com.kelseyde.calvin.search.negamax.NegamaxSearch;

import java.util.Optional;

public interface EngineRepository {

    Optional<NegamaxSearch> getEngine(String boardId);

    void putEngine(NegamaxSearch engine);

}
