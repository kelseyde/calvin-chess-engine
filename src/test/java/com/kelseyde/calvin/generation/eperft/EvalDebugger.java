package com.kelseyde.calvin.generation.eperft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.Evaluator;
import com.kelseyde.calvin.evaluation.SimpleEvaluator;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.Notation;
import org.junit.jupiter.api.Test;

public class EvalDebugger {

    private final Evaluator evaluator = new Evaluator(TestUtils.PRD_CONFIG);
    private final SimpleEvaluator simpleEvaluator = new SimpleEvaluator(TestUtils.PRD_CONFIG);

    @Test
    public void testDebugPosition() {

//        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
//        Board board = FEN.toBoard(fen);
//        evaluator.evaluate(board);
//        simpleEvaluator.evaluate(board);
//        board.makeMove(Notation.fromCombinedNotation("d2d3"));
//        evaluator.evaluate(board);
//        simpleEvaluator.evaluate(board);
//        board.makeMove(Notation.fromCombinedNotation("g8h6"));
//        evaluator.evaluate(board);
//        simpleEvaluator.evaluate(board);
//        board.makeMove(Notation.fromCombinedNotation("c1h6"));
//        evaluator.evaluate(board);
//        simpleEvaluator.evaluate(board);

        String fen = "rnbqkb1r/pppppppp/7B/8/8/3P4/PPP1PPPP/RN1QKBNR b KQkq - 0 2";
        Board board = FEN.toBoard(fen);
        evaluator.evaluate(board);
        simpleEvaluator.evaluate(board);

    }

}
