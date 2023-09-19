package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Game;
import com.kelseyde.calvin.board.move.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//@Service
public class RandomMoveEngine implements Engine {

    @Override
    public Move selectMove(Game game) {
        List<Move> legalMoves = new ArrayList<>(game.getLegalMoves());
        return legalMoves.get(new Random().nextInt(legalMoves.size()));
    }

}
