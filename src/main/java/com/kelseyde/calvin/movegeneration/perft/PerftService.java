package com.kelseyde.calvin.movegeneration.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Game;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.NotationUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class PerftService {

    private final MoveGenerator moveGenerator = new MoveGenerator();

    public int perft(Board board, int depth) {
        Collection<Move> moves = moveGenerator.generateLegalMoves(board);
        if (depth == 1) {
            return moves.size();
        }
        int totalMoveCount = 0;
        for (Move move : moves) {
            Board boardCopy = board.copy();
            boardCopy.applyMove(move);
            totalMoveCount += perft(boardCopy, depth - 1);
        }
        return totalMoveCount;
    }

    private void log(Game game, int depth, Set<Move> moves) {
        List<String> moveHistory = game.getMoveHistory().stream().map(NotationUtils::toNotation).toList();
        List<String> legalMoves = moves.stream().map(NotationUtils::toNotation).toList();
        System.out.printf("perft(%s) -- %s: %s -- %s%n", depth, moveHistory, legalMoves.size(), legalMoves);
    }

    private void log(Game game, int depth, int count) {
        List<String> moveHistory = game.getMoveHistory().stream().map(NotationUtils::toNotation).toList();
        System.out.printf("perft(%s) -- %s: %s %n", depth, moveHistory, count);
    }

}
