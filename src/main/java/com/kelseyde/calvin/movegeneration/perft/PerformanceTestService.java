package com.kelseyde.calvin.movegeneration.perft;

import com.kelseyde.calvin.board.Game;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.utils.MoveUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class PerformanceTestService {

    public int generateAllLegalMoves(Game game, int depth) {
        Collection<Move> moves = game.getLegalMoves();
        if (depth == 1) {
            return moves.size();
        }
        int totalMoveCount = 0;
        for (Move move : moves) {
            game.makeMove(move);
            totalMoveCount += generateAllLegalMoves(game, depth - 1);
            game.unmakeMove();
        }
        return totalMoveCount;
    }

    private void log(Game game, int depth, Set<Move> moves) {
        List<String> moveHistory = game.getMoveHistory().stream().map(MoveUtils::toNotation).toList();
        List<String> legalMoves = moves.stream().map(MoveUtils::toNotation).toList();
        System.out.printf("perft(%s) -- %s: %s -- %s%n", depth, moveHistory, legalMoves.size(), legalMoves);
    }

    private void log(Game game, int depth, int count) {
        List<String> moveHistory = game.getMoveHistory().stream().map(MoveUtils::toNotation).toList();
        System.out.printf("perft(%s) -- %s: %s %n", depth, moveHistory, count);
    }

}
