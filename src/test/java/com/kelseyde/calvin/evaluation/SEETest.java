package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.search.SEE;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SEETest {

    @Test
    public void testSimpleCapturePawn() {

        String fen = "4k3/8/8/3p4/4P3/8/8/4K3 w - - 0 1";
        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("e4d5");

        Assertions.assertTrue(SEE.see(board, move, 100));
        Assertions.assertFalse(SEE.see(board, move, 101));

    }

    @Test
    public void testSimpleCaptureKnight() {

        String fen = "4k3/8/8/3p4/4N3/8/8/4K3 b - - 0 1";
        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("d5e4");

        Assertions.assertTrue(SEE.see(board, move, 320));
        Assertions.assertFalse(SEE.see(board, move, 321));

    }

    @Test
    public void testSimpleCaptureBishop() {

        String fen = "4k3/8/8/3b4/4P3/8/8/4K3 w - - 0 1";
        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("e4d5");

        Assertions.assertTrue(SEE.see(board, move, 330));
        Assertions.assertFalse(SEE.see(board, move, 331));

    }

    @Test
    public void testSimpleCaptureRook() {

        String fen = "4k3/8/3n4/8/4R3/8/8/4K3 b - - 0 1";
        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("d6e4");

        Assertions.assertTrue(SEE.see(board, move, 500));
        Assertions.assertFalse(SEE.see(board, move, 501));

    }

    @Test
    public void testSimpleCaptureQueen() {

        String fen = "4k3/8/8/3q4/4K3/8/8/8 w - - 0 1";
        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("e4d5");

        Assertions.assertTrue(SEE.see(board, move, 900));
        Assertions.assertFalse(SEE.see(board, move, 901));

    }

    @Test
    public void testLoseQueenForPawn() {

        String fen = "4k3/8/4p3/3p4/4Q3/8/8/4K3 w - - 0 1";
        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("e4d5");

        Assertions.assertTrue(SEE.see(board, move, -800));
        Assertions.assertFalse(SEE.see(board, move, -799));

    }

    @Test
    public void testWinPieceQueenChoosesNotToRecapture() {

        String fen = "3rk3/2n5/8/3B4/3Q4/8/8/4K3 b - - 0 1";
        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("c7d5");

        Assertions.assertTrue(SEE.see(board, move, 330));
        Assertions.assertFalse(SEE.see(board, move, 331));

    }

    @Test
    public void testXRayedPiecesCountToo() {

        String fen = "3rk3/3r1b2/8/3p4/8/1B6/B2R4/3RK3 w - - 0 1";

        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("b3d5");

        Assertions.assertTrue(SEE.see(board, move, 100));
        Assertions.assertFalse(SEE.see(board, move, 101));

    }


    @Test
    public void testOrderOfCapturesMatters() {

        String fen = "5k2/2n5/4p3/3r4/2Q1P3/1B6/8/5K2 w - - 0 1";

        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("e4d5");
        Assertions.assertTrue(SEE.see(board, move, 400));
        Assertions.assertFalse(SEE.see(board, move, 401));

        move = Move.fromUCI("c4d5");
        Assertions.assertTrue(SEE.see(board, move, -300));
        Assertions.assertFalse(SEE.see(board, move, -299));

    }

    @Test
    public void testMultipleXraysAndSkewers() {

        String fen = "b2rk3/1BnRN3/2brp3/3p4/2BRP3/1b1rNb2/b2R2b1/3QK3 w - - 0 1";

        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("e4d5");


        Assertions.assertTrue(SEE.see(board, move, 0));
        Assertions.assertFalse(SEE.see(board, move, 1));

    }

}