package com.kelseyde.calvin.tuning.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.generation.MoveGenerator;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;

import java.util.Arrays;
import java.util.List;

public class MPerftService {

    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final MoveOrderer moveOrderer = new MoveOrderer();

    public int perft(Board board, int depth) {
        Move[] moves = moveGenerator.generateMoves(board);
        List<Move> orderMoves = moveOrderer.orderMoves(board, Arrays.asList(moves), null, 0);
        if (depth == 1) {
            return orderMoves.size();
        }
        int totalMoveCount = 0;
        for (Move move : orderMoves) {
            board.makeMove(move);
            totalMoveCount += perft(board, depth - 1);
            board.unmakeMove();
        }
        return totalMoveCount;
    }

}
