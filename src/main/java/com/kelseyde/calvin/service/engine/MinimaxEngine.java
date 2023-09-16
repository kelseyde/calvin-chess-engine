package com.kelseyde.calvin.service.engine;

import com.kelseyde.calvin.model.Game;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.service.engine.minimax.MinimaxEvaluator;
import com.kelseyde.calvin.service.engine.minimax.MinimaxEvaluator.MinimaxResult;
import com.kelseyde.calvin.utils.MoveUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinimaxEngine implements Engine {

    private final MinimaxEvaluator minimaxEvaluator;

    @Override
    public Move selectMove(Game game) {
        MinimaxResult result = minimaxEvaluator.minimax(game, 3, -100000, 100000, true);
        log.info("Minimax evaluation: {}, move: {}", result.eval(), MoveUtils.toNotation(result.move()));
        return result.move();
    }

}
