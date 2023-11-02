package com.kelseyde.calvin.evaluation.eperft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.evaluation.SimpleEvaluator;
import com.kelseyde.calvin.movegeneration.MoveGenerator;

import java.util.List;

public class EPerftService {

    private final MoveGenerator moveGenerator;
    private final SimpleEvaluator evaluator;

    public EPerftService(Board board) {
        moveGenerator = new MoveGenerator();
        evaluator = new SimpleEvaluator(board);
    }

    public void ePerft(Board board, int depth) {
        if (depth == 0) {
            return;
        }
        List<Move> moves = moveGenerator.generateMoves(board, false);
        for (Move move : moves) {
            board.makeMove(move);
            ePerft(board, depth - 1);
            evaluator.get();
            board.unmakeMove();
        }
    }

}
