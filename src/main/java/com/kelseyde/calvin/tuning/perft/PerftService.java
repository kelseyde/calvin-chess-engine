package com.kelseyde.calvin.tuning.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.generation.MoveGenerator;
import com.kelseyde.calvin.generation.picker.MovePicker;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;
import com.kelseyde.calvin.utils.notation.Notation;

import java.util.List;
import java.util.Set;

public class PerftService {

    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final MoveOrderer moveOrderer = new MoveOrderer();

    public long perft(Board board, int depth) {
        MovePicker movePicker = new MovePicker(moveGenerator, moveOrderer, board, depth);
        if (depth == 1) {
            return moveGenerator.generateMoves(board).size();
        }
        long totalMoveCount = 0;
        while (true) {
            Move move = movePicker.pickNextMove();
            if (move == null) break;
            board.makeMove(move);
            totalMoveCount += perft(board, depth - 1);
            board.unmakeMove();
        }
        return totalMoveCount;
    }

    private void log(Board board, int depth, Set<Move> moves) {
        List<String> moveHistory = board.getMoveHistory().stream().map(Notation::toNotation).toList();
        List<String> legalMoves = moves.stream().map(Notation::toNotation).toList();
        System.out.printf("perft(%s) -- %s: %s -- %s%n", depth, moveHistory, legalMoves.size(), legalMoves);
    }

    private void log(Board board, int depth, int count) {
        List<String> moveHistory = board.getMoveHistory().stream().map(Notation::toNotation).toList();
        System.out.printf("perft(%s) -- %s: %s %n", depth, moveHistory, count);
    }

}
