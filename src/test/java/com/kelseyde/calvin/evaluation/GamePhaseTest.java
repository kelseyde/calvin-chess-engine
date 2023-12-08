package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GamePhaseTest {

    @Test
    public void testStartingPosition() {

        Board board = new Board();

        float value = score(board);

        Assertions.assertEquals(1, value);

    }

    @Test
    public void testOnlyKingsLeft() {

        String fen = "4k3/8/8/8/8/8/8/4K3 w - - 0 1";
        Board board = FEN.toBoard(fen);

        float value = score(board);

        Assertions.assertEquals(0, value);

    }

    @Test
    public void testOnlyKingAndPawnsLeft() {

        String fen = "4k3/pppppppp/8/8/8/8/PPPPPPPP/4K3 w - - 0 1";
        Board board = FEN.toBoard(fen);

        float value = score(board);

        Assertions.assertEquals(0, value);

    }

    @Test
    public void testRook() {

        String fen = "1kr5/8/8/8/8/8/8/5KR1 w - - 0 1";
        Board board = FEN.toBoard(fen);

        float value = score(board);

        // total phase = (10 * 4) + (10 * 4) + (20 * 4) + (45 * 2) = 250
        // current = (20 * 2) = 150
        // phase = (40 / 250) = 0.6
        Assertions.assertEquals(0.16, value, 0.01);

    }

    @Test
    public void testMinorPieces() {

        String fen = "8/8/3bpk2/4Np2/3P1P2/4K3/8/8 w - - 0 1";
        Board board = FEN.toBoard(fen);

        float value = score(board);

        // total phase = (10 * 4) + (10 * 4) + (20 * 4) + (45 * 2) = 250
        // current = (10 * 2) = 20
        // phase = (20 / 250) = 0.08
        Assertions.assertEquals(0.08f, value, 0.01);

    }

    @Test
    public void testMiddlegame() {

        String fen = "3q1rk1/2pb1pp1/p1p4p/3pP3/3P1P2/1Q6/PP1B2PP/2R3K1 w - - 0 1";
        Board board = FEN.toBoard(fen);

        float value = score(board);

        // total phase = (10 * 4) + (10 * 4) + (20 * 4) + (45 * 2) = 250
        // current = (10 * 2) + (20 * 2) + (45 * 2) = 150
        // phase = (150 / 250) = 0.6
        Assertions.assertEquals(0.6, value, 0.05);

    }

    private float score(Board board) {
        Material whiteMaterial = Material.fromBoard(board, true);
        Material blackMaterial = Material.fromBoard(board, false);
        return Phase.fromMaterial(whiteMaterial, blackMaterial);
    }

}