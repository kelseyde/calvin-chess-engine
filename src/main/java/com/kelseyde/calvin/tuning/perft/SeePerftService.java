package com.kelseyde.calvin.tuning.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.generation.MoveGenerator;
import com.kelseyde.calvin.search.moveordering.StaticExchangeEvaluator;

import java.util.List;

public class SeePerftService {

    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final StaticExchangeEvaluator see = new StaticExchangeEvaluator();

    public long perft(Board board, int depth) {
        List<Move> moves = moveGenerator.generateMoves(board);
        if (depth == 1) {
            return moves.size();
        }
        long totalMoveCount = 0;
        for (Move move : moves) {
            see.evaluate(board, move);
            board.makeMove(move);
            totalMoveCount += perft(board, depth - 1);
            board.unmakeMove();
        }
        return totalMoveCount;
    }

}
