package com.kelseyde.calvin.tuning.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.notation.NotationUtils;

import java.util.List;
import java.util.Set;

public class PerftService {

    private final MoveGenerator moveGenerator = new MoveGenerator();

    public int perft(Board board, int depth) {
        List<Move> moves = moveGenerator.generateMoves(board);
        if (depth == 1) {
            return moves.size();
        }
        int totalMoveCount = 0;
        for (Move move : moves) {
            board.makeMove(move);
            totalMoveCount += perft(board, depth - 1);
            board.unmakeMove();
        }
        return totalMoveCount;
    }

    private void log(Board board, int depth, Set<Move> moves) {
        List<String> moveHistory = board.getMoveHistory().stream().map(NotationUtils::toNotation).toList();
        List<String> legalMoves = moves.stream().map(NotationUtils::toNotation).toList();
        System.out.printf("perft(%s) -- %s: %s -- %s%n", depth, moveHistory, legalMoves.size(), legalMoves);
    }

    private void log(Board board, int depth, int count) {
        List<String> moveHistory = board.getMoveHistory().stream().map(NotationUtils::toNotation).toList();
        System.out.printf("perft(%s) -- %s: %s %n", depth, moveHistory, count);
    }

}
