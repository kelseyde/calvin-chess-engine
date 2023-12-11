package com.kelseyde.calvin.board;

import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.Notation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

public class ZobristTest {

    @Test
    public void testSamePositionGeneratesSameKey() {

        Board board1 = new Board();
        Board board2 = new Board();
        Assertions.assertEquals(board1.getGameState().getZobristKey(), board2.getGameState().getZobristKey());

        Move e4 = Move.of(12, 28, Move.PAWN_DOUBLE_MOVE_FLAG);
        board1.makeMove(e4);
        board2.makeMove(e4);

        Assertions.assertEquals(board1.getGameState().getZobristKey(), board2.getGameState().getZobristKey());

        board1.unmakeMove();
        board2.unmakeMove();

        Assertions.assertEquals(board1.getGameState().getZobristKey(), board2.getGameState().getZobristKey());
        Assertions.assertEquals(board1.getGameState().getZobristKey(), new Board().getGameState().getZobristKey());

    }

    @Test
    public void testSamePositionEndgameGeneratesSameKey() {

        String fen = "k6K/1pp2P1P/p1p5/P7/8/8/5r2/2R5 w - - 1 51";

        Board board = FEN.toBoard(fen);
        long firstZobrist1 = board.getGameState().getZobristKey();

        board.makeMove(Notation.fromNotation("h8", "g8"));
        long secondZobrist1 = board.getGameState().getZobristKey();

        board.makeMove(Notation.fromNotation("f2", "g2"));
        board.makeMove(Notation.fromNotation("g8", "h8"));
        board.makeMove(Notation.fromNotation("g2", "f2"));
        long firstZobrist2 = board.getGameState().getZobristKey();

        board.makeMove(Notation.fromNotation("h8", "g8"));
        long secondZobrist2 = board.getGameState().getZobristKey();

        Assertions.assertEquals(firstZobrist1, firstZobrist2);
        Assertions.assertEquals(secondZobrist1, secondZobrist2);
        Assertions.assertNotEquals(firstZobrist1, secondZobrist1);
        Assertions.assertNotEquals(firstZobrist1, secondZobrist2);
        Assertions.assertNotEquals(firstZobrist2, secondZobrist1);
        Assertions.assertNotEquals(firstZobrist2, secondZobrist2);

    }

    @Test
    public void testCapturedPieceChangesZobrist() {

        String fen = "1rb3k1/p1q3pp/4pr2/5p2/2pP4/1PQ3P1/4PPBP/2R1K2R b K - 0 21";

        Board board = FEN.toBoard(fen);
        long z1 = board.getGameState().getZobristKey();
        board.makeMove(Notation.fromNotation("b8", "b3"));
        long z2 = board.getGameState().getZobristKey();
        board.makeMove(Notation.fromNotation("c3", "b3"));
        long z3 = board.getGameState().getZobristKey();
        board.makeMove(Notation.fromNotation("c7", "a5"));
        long z4 = board.getGameState().getZobristKey();
        board.makeMove(Notation.fromNotation("b3", "c3"));
        long z5 = board.getGameState().getZobristKey();
        board.makeMove(Notation.fromNotation("a5", "c7"));
        long z6 = board.getGameState().getZobristKey();
        System.out.println(z1);
        System.out.println(z2);
        System.out.println(z3);
        System.out.println(z4);
        System.out.println(z5);
        System.out.println(z6);

        long distinctZobristCount = Stream.of(z1, z2, z3, z4, z5, z6)
                .distinct()
                .count();
        Assertions.assertEquals(6, distinctZobristCount);

    }

}