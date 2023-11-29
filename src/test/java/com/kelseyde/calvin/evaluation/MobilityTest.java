package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.evaluation.score.Mobility;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class MobilityTest {

    private final EngineConfig config = TestUtils.TEST_CONFIG;

    @Test
    public void testTrappedKnight() {

        String fen = "6k1/8/8/8/2p5/3p4/8/N5K1 w - - 0 1";
        Board board = FEN.toBoard(fen);

        int mgScore = Mobility.score(config, board, true, 1);
        int egScore = Mobility.score(config, board, true, 0);

        Assertions.assertEquals(-32, mgScore);
        Assertions.assertEquals(-21, egScore);

    }

    @Test
    public void testKnightCannotMoveToPawnAttacks() {

        String fen = "5k2/1p3p2/8/8/3N4/8/8/6K1 w - - 0 1";
        Board board = FEN.toBoard(fen);

        int mgScore = Mobility.score(config, board, true, 1);
        int egScore = Mobility.score(config, board, true, 0);

        Assertions.assertEquals(14, mgScore);
        Assertions.assertEquals(9, egScore);

    }

    @Test
    public void testOctopusKnight() {

        String fen = "6k1/8/4N3/8/8/8/8/6K1 w - - 0 1";
        Board board = FEN.toBoard(fen);

        int mgScore = Mobility.score(config, board, true, 1);
        int egScore = Mobility.score(config, board, true, 0);

        Assertions.assertEquals(27, mgScore);
        Assertions.assertEquals(18, egScore);

    }

    @Test
    public void testTrappedBishop() {

        String fen = "6k1/8/8/7p/6pP/4KpP1/5P1B/8 w - - 0 1";
        Board board = FEN.toBoard(fen);

        int mgScore = Mobility.score(config, board, true, 1);
        int egScore = Mobility.score(config, board, true, 0);

        Assertions.assertEquals(-25, mgScore);
        Assertions.assertEquals(-65, egScore);

    }

    @Test
    public void testMonsterBishop() {

        String fen = "5k2/8/8/8/4B3/8/8/6K1 w - - 0 1";
        Board board = FEN.toBoard(fen);

        int mgScore = Mobility.score(config, board, true, 1);
        int egScore = Mobility.score(config, board, true, 0);

        Assertions.assertEquals(30, mgScore);
        Assertions.assertEquals(78, egScore);

    }

    @Test
    public void testBishopCannotMoveToPawnAttacks() {

        String fen = "5k2/1p3p2/8/3B4/8/8/8/6K1 w - - 0 1";
        Board board = FEN.toBoard(fen);

        int mgScore = Mobility.score(config, board, true, 1);
        int egScore = Mobility.score(config, board, true, 0);

        Assertions.assertEquals(13, mgScore);
        Assertions.assertEquals(34, egScore);

    }

    @Test
    public void testMonsterRook() {

        String fen = "5k2/8/8/4R3/8/8/8/6K1 w - - 0 1";
        Board board = FEN.toBoard(fen);

        int mgScore = Mobility.score(config, board, true, 1);
        int egScore = Mobility.score(config, board, true, 0);

        Assertions.assertEquals(40, mgScore);
        Assertions.assertEquals(69, egScore);

    }

    @Test
    public void testRookTrappedInCorner() {

        String fen = "5k2/8/8/8/8/8/PPP5/R1K5 w - - 0 1";
        Board board = FEN.toBoard(fen);

        int mgScore = Mobility.score(config, board, true, 1);
        int egScore = Mobility.score(config, board, true, 0);

        Assertions.assertEquals(-40, mgScore);
        Assertions.assertEquals(-68, egScore);

    }

    @Test
    public void testRookCannotMoveToPawnAttacks() {

        String fen = "5k2/2pPp3/1p3p2/1P1R1P2/3P4/8/8/2K5 w - - 0 1";
        Board board = FEN.toBoard(fen);

        int mgScore = Mobility.score(config, board, true, 1);
        int egScore = Mobility.score(config, board, true, 0);

        Assertions.assertEquals(-49, mgScore);
        Assertions.assertEquals(-84, egScore);

    }

    @Test
    public void testMonsterQueen() {

        String fen = "6k1/8/8/4q3/8/8/8/7K b - - 0 1";
        Board board = FEN.toBoard(fen);

        int mgScore = Mobility.score(config, board, false, 1);
        int egScore = Mobility.score(config, board, false, 0);

        Assertions.assertEquals(40, mgScore);
        Assertions.assertEquals(35, egScore);

    }

    @Test
    public void testTrappedQueen() {

        String fen = "q1k5/2P5/pPP5/3P4/8/8/8/7K b - - 0 1";
        Board board = FEN.toBoard(fen);

        int mgScore = Mobility.score(config, board, false, 1);
        int egScore = Mobility.score(config, board, false, 0);

        Assertions.assertEquals(-46, mgScore);
        Assertions.assertEquals(-40, egScore);

    }

    @Test
    public void testBishopsCanMoveThroughFriendlyPieces() {

        String fen = "7k/8/8/8/8/1B6/B7/7K w - - 0 1";
        Board board = FEN.toBoard(fen);

        int mgScore = Mobility.score(config, board, true, 1);
        int egScore = Mobility.score(config, board, true, 0);

        Assertions.assertEquals(5 + 13, mgScore);
        Assertions.assertEquals(12 + 34, egScore);

    }

    @Test
    public void testKasparovOctopusKnight() {

        String fen = "2r1r1k1/3n1p2/5q1p/3P1bp1/Np6/1P1n2P1/3Q1PBP/1N1R1RK1 w - - 0 27";
        Board board = FEN.toBoard(fen);

        /*
        wn1 = 3 (-7, -5)
        wn2 = 1 (-22, -15)
        wb1 = 5 (-4, -12)
        wr1 = 7 (0, 0)
        wr2 = 5 (-12, -21)
        wq1 = 10 (-10, -8)
         */

        int mgScore = Mobility.score(config, board, true, 1);
        int egScore = Mobility.score(config, board, true, 0);

        Assertions.assertEquals(-55, mgScore);
        Assertions.assertEquals(-61, egScore);

        /*
        bn1 = 6 (14, 9)
        bn2 = 6 (14, 9)
        bb1 = 10 (17, 45)
        br1 = 10 (17, 30)
        br2 = 10 (17, 30)
        bq1 = 16 (9, 8)
         */

        mgScore = Mobility.score(config, board, false, 1);
        egScore = Mobility.score(config, board, false, 0);

        Assertions.assertEquals(88, mgScore);
        Assertions.assertEquals(131, egScore);

    }

}
