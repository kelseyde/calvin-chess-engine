package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.material.MaterialEvaluator;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GamePhaseCalculatorTest {

    private final MaterialEvaluator materialCalculator = new MaterialEvaluator();

    @Test
    public void testStartingPosition() {

        Board board = new Board();

        float value = score(board);

        Assertions.assertEquals(1, value);

    }

    @Test
    public void testOnlyKingsLeft() {

        String fen = "4k3/8/8/8/8/8/8/4K3 w - - 0 1";
        Board board = FEN.fromFEN(fen);

        float value = score(board);

        Assertions.assertEquals(0, value);

    }

    @Test
    public void testOnlyKingAndPawnsLeft() {

        String fen = "4k3/pppppppp/8/8/8/8/PPPPPPPP/4K3 w - - 0 1";
        Board board = FEN.fromFEN(fen);

        float value = score(board);

        Assertions.assertEquals(0, value);

    }

    @Test
    public void testRook() {

        String fen = "1kr5/8/8/8/8/8/8/5KR1 w - - 0 1";
        Board board = FEN.fromFEN(fen);

        float value = score(board);

        // 0 + (1000 / 6400) = 0.15625

        Assertions.assertEquals(0.15625f, value);

    }

    @Test
    public void testMinorPieces() {

        String fen = "8/8/3bpk2/4Np2/3P1P2/4K3/8/8 w - - 0 1";
        Board board = FEN.fromFEN(fen);

        float value = score(board);

        // 0 + ((320 + 330) / 6400) = 0.1015625

        Assertions.assertEquals(0.1015625f, value);

    }

    @Test
    public void testMiddlegame() {

        String fen = "3q1rk1/2pb1pp1/p1p4p/3pP3/3P1P2/1Q6/PP1B2PP/2R3K1 w - - 0 1";
        Board board = FEN.fromFEN(fen);

        float value = score(board);

        // 0 + (1800 + 1000 + 660) / 6400 = 0.540625
        Assertions.assertEquals(0.540625f, value);

    }

    private float score(Board board) {
        return (materialCalculator.calculate(board, true).phase() + materialCalculator.calculate(board, false).phase()) / 2;
    }

}