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

        int score = SEE.see(board, move);

        Assertions.assertEquals(100, score);

    }

    @Test
    public void testSimpleCaptureKnight() {

        String fen = "4k3/8/8/3p4/4N3/8/8/4K3 b - - 0 1";
        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("d5e4");

        int score = SEE.see(board, move);

        Assertions.assertEquals(320, score);

    }

    @Test
    public void testSimpleCaptureBishop() {

        String fen = "4k3/8/8/3b4/4P3/8/8/4K3 w - - 0 1";
        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("e4d5");

        int score = SEE.see(board, move);

        Assertions.assertEquals(330, score);

    }

    @Test
    public void testSimpleCaptureRook() {

        String fen = "4k3/8/3n4/8/4R3/8/8/4K3 b - - 0 1";
        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("d6e4");

        int score = SEE.see(board, move);

        Assertions.assertEquals(500, score);

    }

    @Test
    public void testSimpleCaptureQueen() {

        String fen = "4k3/8/8/3q4/4K3/8/8/8 w - - 0 1";
        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("e4d5");

        int score = SEE.see(board, move);

        Assertions.assertEquals(900, score);

    }

    @Test
    public void testLoseQueenForPawn() {

        String fen = "4k3/8/4p3/3p4/4Q3/8/8/4K3 w - - 0 1";
        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("e4d5");

        int score = SEE.see(board, move);

        Assertions.assertEquals(-800, score);

    }

    @Test
    public void testWinPieceQueenChoosesNotToRecapture() {

        String fen = "3rk3/2n5/8/3B4/3Q4/8/8/4K3 b - - 0 1";
        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("c7d5");

        int score = SEE.see(board, move);

        Assertions.assertEquals(330, score);

    }

    @Test
    public void testXRayedPiecesCountToo() {

        String fen = "3rk3/3r1b2/8/3p4/8/1B6/B2R4/3RK3 w - - 0 1";

        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("b3d5");

        int score = SEE.see(board, move);

        Assertions.assertEquals(100, score);

    }

    @Test
    public void testMultipleRookXrays() {

        String fen = "5k2/8/8/8/rRrRp3/8/8/5K2 w - - 0 1";

        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("d4e4");

        int score = SEE.see(board, move);

        Assertions.assertEquals(-400, score);

        move = Move.fromUCI("d4c4");

        score = SEE.see(board, move);

        Assertions.assertEquals(500, score);

    }

    @Test
    public void testOrderOfCapturesMatters() {

        String fen = "5k2/2n5/4p3/3r4/2Q1P3/1B6/8/5K2 w - - 0 1";

        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("e4d5");

        int score = SEE.see(board, move);

        Assertions.assertEquals(400, score);

        move = Move.fromUCI("c4d5");

        score = SEE.see(board, move);

        Assertions.assertEquals(-300, score);

    }

    @Test
    public void testMultipleXraysAndSkewers() {

        String fen = "b2rk3/1BnRN3/2brp3/3p4/2BRP3/1b1rNb2/b2R2b1/3QK3 w - - 0 1";

        Board board = FEN.toBoard(fen);
        Move move = Move.fromUCI("e4d5");

        int score = SEE.see(board, move);

        Assertions.assertEquals(0, score);

    }

}