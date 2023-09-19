package com.kelseyde.calvin.service.evaluator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Game;

public class MaterialEvaluator implements PositionEvaluator {

    @Override
    public int evaluate(Game game) {

        boolean isWhiteToMove = game.getBoard().isWhiteToMove();
        int playerScore = calculateMaterialScore(game.getBoard(), isWhiteToMove);

        int opponentScore = calculateMaterialScore(game.getBoard(), !isWhiteToMove);

        return playerScore - opponentScore;

    }

    private int calculateMaterialScore(Board board, boolean colour) {
        // TODO fix
        return 0;
//        return board.getPieces(colour)
//                .stream()
//                .map(Piece::getType)
//                .map(PieceMaterialValues::get)
//                .reduce(0, Integer::sum);
    }

}
