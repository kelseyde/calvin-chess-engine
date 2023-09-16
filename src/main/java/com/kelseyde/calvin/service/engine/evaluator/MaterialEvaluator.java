package com.kelseyde.calvin.service.engine.evaluator;

import com.kelseyde.calvin.model.*;

import java.util.Map;

public class MaterialEvaluator implements PositionEvaluator {

    private static final Map<PieceType, Integer> MATERIAL_VALUES = Map.of(
            PieceType.PAWN, 10,
            PieceType.KNIGHT, 30,
            PieceType.BISHOP, 30,
            PieceType.ROOK, 50,
            PieceType.QUEEN, 90,
            PieceType.KING, 1000
    );

    @Override
    public int evaluate(Game game) {

        Colour playerColour = game.getTurn();
        int playerScore = calculateMaterialScore(game.getBoard(), playerColour);

        Colour opponentColour = playerColour.oppositeColour();
        int opponentScore = calculateMaterialScore(game.getBoard(), opponentColour);

        return playerScore - opponentScore;

    }

    private int calculateMaterialScore(Board board, Colour colour) {
        return board.getPieces(colour)
                .values().stream()
                .map(Piece::getType)
                .map(MATERIAL_VALUES::get)
                .reduce(0, Integer::sum);
    }

}
