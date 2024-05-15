package com.kelseyde.calvin.tuning.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.MoveList;
import com.kelseyde.calvin.generation.MoveGenerator;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;

import java.util.List;

public class MPerftService {

    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final MoveOrderer moveOrderer = new MoveOrderer();

    public int perft(Board board, int depth) {
        MoveList moves = moveGenerator.generateMoves(board);
        moves = moveOrderer.orderMoves(board, moves, null, true, 0);
        if (depth == 1) {
            return moves.size();
        }
        int totalMoveCount = 0;
        while (moves.hasNext()) {
            Move move = moves.next();
            board.makeMove(move);
            totalMoveCount += perft(board, depth - 1);
            board.unmakeMove();
        }
        return totalMoveCount;
    }

}
