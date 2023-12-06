package com.kelseyde.calvin.tuning.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.engine.EngineInitializer;
import com.kelseyde.calvin.evaluation.Evaluator;
import com.kelseyde.calvin.evaluation.EvaluatorCopy;
import com.kelseyde.calvin.generation.MoveGenerator;
import com.kelseyde.calvin.utils.notation.PGN;

import java.util.List;

public class EPerftService {

    private final MoveGenerator moveGenerator;
    private final Evaluator evaluator;
    private final EvaluatorCopy evaluatorCopy;

    public EPerftService(Board board) {
        moveGenerator = new MoveGenerator();
        evaluator = new Evaluator(EngineInitializer.loadDefaultConfig());
        evaluatorCopy = new EvaluatorCopy(EngineInitializer.loadDefaultConfig());
        evaluator.evaluate(board);
    }

    public void ePerft(Board board, int depth) {
        if (depth == 0) {
            return;
        }
        List<Move> moves = moveGenerator.generateMoves(board);
        for (Move move : moves) {
            board.makeMove(move);
            int eval = evaluator.evaluate(board);
            int otherEval = evaluatorCopy.evaluate(board);
            int diff = Math.abs(eval - otherEval);
            if (diff > 50) {
                System.out.printf("%s / %s : %s %n", eval, otherEval, PGN.toPGN(board));
            }
            ePerft(board, depth - 1);
            board.unmakeMove();
        }
    }

}
