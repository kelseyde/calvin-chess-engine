package com.kelseyde.calvin.movegeneration.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.search.moveordering.MoveOrderer;

public class MPerftService {

    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final MoveOrderer moveOrderer = new MoveOrderer();

    public int perft(Board board, int depth) {
        Move[] moves = moveGenerator.generateLegalMoves(board, false);
        moves = moveOrderer.orderMoves(board, moves, null, true, 0);
        if (depth == 1) {
            return moves.length;
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
