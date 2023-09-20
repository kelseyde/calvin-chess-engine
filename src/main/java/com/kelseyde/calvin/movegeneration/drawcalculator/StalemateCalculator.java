package com.kelseyde.calvin.movegeneration.drawcalculator;

import com.kelseyde.calvin.board.Game;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.result.DrawType;
import lombok.Getter;

import java.util.Collection;

public class StalemateCalculator implements DrawCalculator {

    @Getter
    private final DrawType drawType = DrawType.STALEMATE;

    @Override
    public boolean isDraw(Game game) {
        boolean lastMoveNotCheck =
                game.getMoveHistory().isEmpty() || !game.getMoveHistory().peek().isCheck();
        Collection<Move> legalMoves = game.getLegalMoves();
        return lastMoveNotCheck && legalMoves.isEmpty();
    }

}
