package com.kelseyde.calvin.service.game.perft;

import com.kelseyde.calvin.model.Game;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.utils.MoveUtils;

import java.util.List;
import java.util.Set;

public class MoveGenerationService {

    public int generateMoves(Game game, int depth) {
        Set<Move> moves = game.getLegalMoves();
        if (depth == 1) {
            return moves.size();
        }
        int totalMoveCount = 0;
        for (Move move : moves) {
            game.makeMove(move);
            totalMoveCount += generateMoves(game, depth - 1);
            game.unmakeMove();
        }
        return totalMoveCount;
    }

    private void log(Game game, int depth, Set<Move> moves) {
        List<String> moveHistory = game.getMoveHistory().stream().map(MoveUtils::toNotation).toList();
        List<String> legalMoves = moves.stream().map(MoveUtils::toNotation).toList();
        System.out.printf("Depth: %s, Previous moves: %s, Legal moves: %s %s%n", depth, moveHistory, legalMoves.size(), legalMoves);
    }

}
