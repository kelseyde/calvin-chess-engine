package com.kelseyde.calvin.service.game.drawcalculator;

import com.kelseyde.calvin.model.DrawType;
import com.kelseyde.calvin.model.Game;
import com.kelseyde.calvin.model.move.Move;
import lombok.Getter;

import java.util.Set;

public class StalemateCalculator implements DrawCalculator {

    @Getter
    private final DrawType drawType = DrawType.STALEMATE;

    @Override
    public boolean isDraw(Game game) {
        boolean lastMoveNotCheck =
                game.getMoveHistory().isEmpty() || !game.getMoveHistory().peek().isCheck();
        Set<Move> legalMoves = game.getLegalMoves();
        return lastMoveNotCheck && legalMoves.isEmpty();
    }

}
