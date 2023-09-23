package com.kelseyde.calvin.movegeneration.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.NotationUtils;

import java.util.List;
import java.util.Set;

public class PerftService {

    private final MoveGenerator moveGenerator = new MoveGenerator();

    public int perft(Board board, int depth) {
        Set<Move> moves = moveGenerator.generateLegalMoves(board);
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

    private void log(Board board, int depth, Move move, Set<Move> moves) {
        List<String> moveHistory = board.getMoveHistory().stream().map(NotationUtils::toNotation).toList();
        List<String> legalMoves = moves.stream().map(NotationUtils::toNotation).toList();
        System.out.println("");
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
