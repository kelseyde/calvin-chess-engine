package com.kelseyde.calvin.board;

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

}