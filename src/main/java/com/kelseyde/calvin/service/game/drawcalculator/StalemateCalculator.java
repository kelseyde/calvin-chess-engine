package com.kelseyde.calvin.service.game.drawcalculator;

import com.kelseyde.calvin.model.game.DrawType;
import com.kelseyde.calvin.model.game.Game;
import com.kelseyde.calvin.model.move.Move;
import lombok.Getter;

import java.util.Set;

public class StalemateCalculator implements DrawCalculator {

    @Getter
    private final DrawType drawType = DrawType.STALEMATE;

    @Override
    public boolean isDraw(Game game) {
        Move lastMove = game.getMoveHistory().peek();
        Set<Move> legalMoves = game.getLegalMoves();
        return !lastMove.isCheck() && legalMoves.isEmpty();
    }

}
