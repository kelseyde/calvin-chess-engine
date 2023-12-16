package com.kelseyde.calvin.tuning.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.EngineInitializer;
import com.kelseyde.calvin.evaluation.Evaluator;
import com.kelseyde.calvin.evaluation.SimpleEvaluator;
import com.kelseyde.calvin.generation.MoveGenerator;
import com.kelseyde.calvin.utils.notation.Notation;

import java.util.List;

public class EPerftService {

    private final MoveGenerator moveGenerator;
    private final Evaluator evaluator;
    private final SimpleEvaluator simpleEvaluator;

    public EPerftService(Board board) {
        moveGenerator = new MoveGenerator();
        evaluator = new Evaluator(EngineInitializer.loadDefaultConfig());
        simpleEvaluator = new SimpleEvaluator(EngineInitializer.loadDefaultConfig());
        evaluator.evaluate(board);
    }

    public void ePerft(Board board, int depth) {
        if (depth == 0) {
            return;
        }
        List<Move> moves = moveGenerator.generateMoves(board);
        for (Move move : moves) {
            board.makeMove(move);
            int eval1 = evaluator.evaluate(board);
            int eval2 = simpleEvaluator.evaluate(board);
            if (eval1 != eval2) {
                System.out.printf("%s %s != %s%n", Notation.toNotation(board.getMoveHistory()), eval1, eval2);
            }
            ePerft(board, depth - 1);
            board.unmakeMove();
        }
    }

}
