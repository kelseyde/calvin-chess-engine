package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class BoardEvaluatorUtilTest {

    @Test
    public void testEvaluateBoard() {

        String fen = "r1b1k2r/1p3ppp/8/3np3/1P6/1Q4P1/4PP1P/bN2K1NR w Kkq - 0 4";
        Board board = FEN.fromFEN(fen);

        Evaluator boardEvaluator = new Evaluator(board);
        System.out.println(boardEvaluator.get());
    }

    @Test
    public void testPawnOrKnightOnFirstMove() {

        String fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
        Board board = FEN.fromFEN(fen);

        Evaluator boardEvaluator = new Evaluator(board);
        System.out.println(boardEvaluator.get());

        fen = "rnbqkbnr/pppppppp/8/8/8/2N5/PPPPPPPP/R1BQKBNR b KQkq - 1 1";
        board = FEN.fromFEN(fen);

        System.out.println(boardEvaluator.get());

    }

}
