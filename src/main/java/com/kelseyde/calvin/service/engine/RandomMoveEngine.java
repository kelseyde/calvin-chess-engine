package com.kelseyde.calvin.service.engine;

import com.kelseyde.calvin.model.game.Game;
import com.kelseyde.calvin.model.move.Move;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RandomMoveEngine implements Engine {

    @Override
    public Move selectMove(Game game) {
        List<Move> legalMoves = new ArrayList<>(game.getLegalMoves());
        return legalMoves.get(new Random().nextInt(legalMoves.size()));
    }

}
