package com.kelseyde.calvin.service.evaluator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Game;

public class MaterialEvaluator implements PositionEvaluator {

    @Override
    public int evaluate(Game game) {

        Colour playerColour = game.getTurn();
        int playerScore = calculateMaterialScore(game.getBoard(), playerColour);

        Colour opponentColour = playerColour.oppositeColour();
        int opponentScore = calculateMaterialScore(game.getBoard(), opponentColour);

        return playerScore - opponentScore;

    }

    private int calculateMaterialScore(Board board, Colour colour) {
        // TODO fix
        return 0;
//        return board.getPieces(colour)
//                .stream()
//                .map(Piece::getType)
//                .map(PieceMaterialValues::get)
//                .reduce(0, Integer::sum);
    }

}
