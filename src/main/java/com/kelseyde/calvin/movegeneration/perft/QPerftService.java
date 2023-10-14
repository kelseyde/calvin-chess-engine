package com.kelseyde.calvin.movegeneration.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegeneration.MoveGenerator;

public class QPerftService {

    private final MoveGenerator moveGenerator = new MoveGenerator();

    public int perft(Board board, int depth) {
        Move[] moves = moveGenerator.generateLegalMoves(board, true);
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
