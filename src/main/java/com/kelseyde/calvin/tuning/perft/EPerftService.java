package com.kelseyde.calvin.tuning.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.MoveList;
import com.kelseyde.calvin.engine.EngineInitializer;
import com.kelseyde.calvin.evaluation.Evaluator;
import com.kelseyde.calvin.generation.MoveGenerator;

import java.util.List;

public class EPerftService {

    private final MoveGenerator moveGenerator;
    private final Evaluator evaluator;

    public EPerftService(Board board) {
        moveGenerator = new MoveGenerator();
        evaluator = new Evaluator(EngineInitializer.loadDefaultConfig());
        evaluator.evaluate(board);
    }

    public void ePerft(Board board, int depth) {
        if (depth == 0) {
            return;
        }
        MoveList moves = moveGenerator.generateMoves(board);
        while (moves.hasNext()) {
            Move move = moves.next();
            board.makeMove(move);
            evaluator.evaluate(board);
            ePerft(board, depth - 1);
            board.unmakeMove();
        }
    }

}
