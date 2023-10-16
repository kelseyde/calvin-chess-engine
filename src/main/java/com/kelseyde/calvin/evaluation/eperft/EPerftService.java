package com.kelseyde.calvin.evaluation.eperft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.evaluation.Evaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;

import java.util.List;

public class EPerftService {

    private final MoveGenerator moveGenerator;
    private final Evaluator boardEvaluator;

    public EPerftService(Board board) {
        moveGenerator = new MoveGenerator();
        boardEvaluator = new Evaluator(board);
    }

    public void ePerft(Board board, int depth) {
        if (depth == 0) {
            return;
        }
        List<Move> moves = moveGenerator.generateLegalMoves(board, false);
        for (Move move : moves) {
            board.makeMove(move);
            boardEvaluator.makeMove(move);
            ePerft(board, depth - 1);
            boardEvaluator.unmakeMove();
            board.unmakeMove();
        }
    }

}
