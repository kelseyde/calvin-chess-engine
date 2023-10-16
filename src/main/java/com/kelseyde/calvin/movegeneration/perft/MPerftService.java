package com.kelseyde.calvin.movegeneration.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;

import java.util.List;

public class MPerftService {

    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final MoveOrderer moveOrderer = new MoveOrderer();

    public int perft(Board board, int depth) {
        List<Move> moves = moveGenerator.generateMoves(board, false);
        moves = moveOrderer.orderMoves(board, moves, null, true, 0);
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

}
