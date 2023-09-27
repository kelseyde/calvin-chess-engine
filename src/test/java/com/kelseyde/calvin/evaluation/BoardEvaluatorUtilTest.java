package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class BoardEvaluatorUtilTest {

    @Test
    @Disabled
    public void testEvaluateBoard() {

        String fen = "r1b1k2r/1p3ppp/8/3np3/1P6/1Q4P1/4PP1P/bN2K1NR w Kkq - 0 4";
        Board board = FEN.fromFEN(fen);

        BoardEvaluator boardEvaluator = new CombinedBoardEvaluator();
        System.out.println(boardEvaluator.evaluate(board));
    }

}
