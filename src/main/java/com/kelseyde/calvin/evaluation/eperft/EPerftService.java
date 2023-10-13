package com.kelseyde.calvin.evaluation.eperft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.evaluation.BoardEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;

public class EPerftService {

    private final MoveGenerator moveGenerator;
    private final BoardEvaluator boardEvaluator;

    public EPerftService(Board board) {
        moveGenerator = new MoveGenerator();
        boardEvaluator = new BoardEvaluator(board);
    }

    public void ePerft(Board board, int depth) {
        if (depth == 0) {
            return;
        }
        Move[] moves = moveGenerator.generateLegalMoves(board, false);
        for (Move move : moves) {
            board.makeMove(move);
            ePerft(board, depth - 1);
            boardEvaluator.evaluate(board);
            board.unmakeMove();
        }
    }

}
