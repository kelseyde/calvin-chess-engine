package com.kelseyde.calvin.nnue;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.evaluation.hce.Evaluator;
import com.kelseyde.calvin.evaluation.nnue.NNUE;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.Notation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NNUETest {

    @Test
    public void testBenchmark() {

        String startpos = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        benchmark("startpos", startpos);

//        String lostPos = "rnbqkbnr/pppppppp/8/8/8/8/8/3QK3 w kq - 0 1";
//        benchmark("lostPos", lostPos);
//
//        String wonPos = "rn2k1nr/ppp2ppp/8/4P3/1BP3b1/8/PP3KPP/RN1q1BR1 w kq - 0 10";
//        benchmark("wonPos", wonPos);

    }

    private void benchmark(String name, String fen) {
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);
        Evaluator hce = new Evaluator(TestUtils.PRD_CONFIG);
        System.out.printf("%s %s hce %s nnue %s%n", name, fen, hce.evaluate(board), nnue.evaluate(board));
    }

}