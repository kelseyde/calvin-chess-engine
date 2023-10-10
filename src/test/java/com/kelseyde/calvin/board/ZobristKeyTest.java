package com.kelseyde.calvin.board;

import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ZobristKeyTest {

    @Test
    public void testSamePositionGeneratesSameKey() {

        Board board1 = new Board();
        Board board2 = new Board();
        Assertions.assertEquals(board1.getGameState().getZobristKey(), board2.getGameState().getZobristKey());

        Move e4 = new Move(12, 28, Move.PAWN_DOUBLE_MOVE_FLAG);
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

        Board board = FEN.fromFEN(fen);
        long firstZobrist1 = board.getGameState().getZobristKey();

        board.makeMove(NotationUtils.fromNotation("h8", "g8"));
        long secondZobrist1 = board.getGameState().getZobristKey();

        board.makeMove(NotationUtils.fromNotation("f2", "g2"));
        board.makeMove(NotationUtils.fromNotation("g8", "h8"));
        board.makeMove(NotationUtils.fromNotation("g2", "f2"));
        long firstZobrist2 = board.getGameState().getZobristKey();

        board.makeMove(NotationUtils.fromNotation("h8", "g8"));
        long secondZobrist2 = board.getGameState().getZobristKey();

        Assertions.assertEquals(firstZobrist1, firstZobrist2);
        Assertions.assertEquals(secondZobrist1, secondZobrist2);
        Assertions.assertNotEquals(firstZobrist1, secondZobrist1);
        Assertions.assertNotEquals(firstZobrist1, secondZobrist2);
        Assertions.assertNotEquals(firstZobrist2, secondZobrist1);
        Assertions.assertNotEquals(firstZobrist2, secondZobrist2);

    }

}