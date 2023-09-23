package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;

public class MaterialEvaluator implements PositionEvaluator {

    @Override
    public int evaluate(Board board) {

        boolean isWhiteToMove = board.isWhiteToMove();
        int playerScore = calculateMaterialScore(board, isWhiteToMove);

        int opponentScore = calculateMaterialScore(board, !isWhiteToMove);

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
