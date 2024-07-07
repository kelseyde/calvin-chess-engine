package com.kelseyde.calvin.nnue;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.evaluation.nnue.NNUE;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.Notation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class NNUETest {

    @Test
    public void testStartpos() {

        Board board = new Board();
        NNUE nnue = new NNUE(board);
        System.out.println(nnue.evaluate(board));

    }

    @Test
    public void testRuyLopez() {

        String fen = "r1bqkbnr/1ppp1ppp/p1n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 4";
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);
        System.out.println(nnue.evaluate(board));

    }

    @Test
    public void testBlackToMove() {

        String fen = "r1bqkbnr/1ppp1ppp/2n5/1p6/4P2P/5NPR/P1P1KP2/q1BQ4 b kq - 0 9";
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);
        System.out.println(nnue.evaluate(board));

    }

    @Test
    public void testMakeUnmakeMove() {

        Board board = new Board();
        NNUE nnue = new NNUE(board);

        Move m1 = Notation.fromCombinedNotation("e2e4");
        nnue.makeMove(board, m1);
        board.makeMove(m1);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        Move m2 = Notation.fromCombinedNotation("e7e5");
        nnue.makeMove(board, m2);
        board.makeMove(m2);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        Move m3 = Notation.fromCombinedNotation("g1f3");
        nnue.makeMove(board, m3);
        board.makeMove(m3);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        Move m4 = Notation.fromCombinedNotation("b8c6");
        nnue.makeMove(board, m4);
        board.makeMove(m4);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        Move m5 = Notation.fromCombinedNotation("f1b5");
        nnue.makeMove(board, m5);
        board.makeMove(m5);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        Move m6 = Notation.fromCombinedNotation("a7a6");
        nnue.makeMove(board, m6);
        board.makeMove(m6);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        Move m7 = Notation.fromCombinedNotation("b5a4");
        nnue.makeMove(board, m7);
        board.makeMove(m7);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

    }

    @Test
    public void testCastling() {

        Board board = new Board();
        NNUE nnue = new NNUE(board);

        Move m1 = Notation.fromCombinedNotation("e2e4");
        nnue.makeMove(board, m1);
        board.makeMove(m1);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        Move m2 = Notation.fromCombinedNotation("e7e5");
        nnue.makeMove(board, m2);
        board.makeMove(m2);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        Move m3 = Notation.fromCombinedNotation("g1f3");
        nnue.makeMove(board, m3);
        board.makeMove(m3);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        Move m4 = Notation.fromCombinedNotation("g8f6");
        nnue.makeMove(board, m4);
        board.makeMove(m4);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        Move m5 = Notation.fromCombinedNotation("f1e2");
        nnue.makeMove(board, m5);
        board.makeMove(m5);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        Move m6 = Notation.fromCombinedNotation("f8e7");
        nnue.makeMove(board, m6);
        board.makeMove(m6);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        Move m7 = Notation.fromCombinedNotation("e1g1");
        nnue.makeMove(board, m7);
        board.makeMove(m7);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        Move m8 = Notation.fromCombinedNotation("e8g8");
        nnue.makeMove(board, m8);
        board.makeMove(m8);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

    }

    @Test
    public void testEnPassant() {

        String fen = "rnbqkb1r/ppp1pppp/5n2/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3";
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);

        Move enPassant = Notation.fromNotation("e5", "d6", Move.EN_PASSANT_FLAG);
        nnue.makeMove(board, enPassant);
        board.makeMove(enPassant);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

    }

    @Test
    public void testPromotion() {

        String fen = "rnb1kb1r/pP2pppp/5n2/8/8/3q4/PPPP1PPP/RNBQKBNR w KQkq - 1 5";
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);

        Move promotion = Notation.fromNotation("b7", "a8", Move.PROMOTE_TO_KNIGHT_FLAG);
        nnue.makeMove(board, promotion);
        board.makeMove(promotion);
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

        nnue.unmakeMove();
        board.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(board), new NNUE(board).evaluate(board));

    }


    @Test
    public void testSimpleEvals() {

        Board board = new Board();
        NNUE nnue = new NNUE(board);
        System.out.println(nnue.evaluate(board));

        String fen = "rn1qk1nr/ppp2ppp/8/4P3/1BP5/8/PP3KPP/RN1b1BR1 w kq - 0 10";
        board = FEN.toBoard(fen);
        nnue = new NNUE(board);
        System.out.println(nnue.evaluate(board));

    }

    @Test
    public void testCorrectFeatureIndices() {
        String fen = "4b3/2k4P/2p4R/3r4/4K3/8/8/N7 w - - 0 1";
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);
    }

}