package com.kelseyde.calvin.board;

import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.move.MoveType;
import com.kelseyde.calvin.board.piece.PieceType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ZobristKeyTest {

    @Test
    public void testSamePositionGeneratesSameKey() {

        Board board1 = new Board();
        Board board2 = new Board();
        Assertions.assertEquals(board1.getCurrentGameState().getZobristKey(), board2.getCurrentGameState().getZobristKey());

        Move e4 = Move.builder().startSquare(12).endSquare(28).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build();
        board1.makeMove(e4);
        board2.makeMove(e4);

        Assertions.assertEquals(board1.getCurrentGameState().getZobristKey(), board2.getCurrentGameState().getZobristKey());

        board1.unmakeMove();
        board2.unmakeMove();

        Assertions.assertEquals(board1.getCurrentGameState().getZobristKey(), board2.getCurrentGameState().getZobristKey());
        Assertions.assertEquals(board1.getCurrentGameState().getZobristKey(), new Board().getCurrentGameState().getZobristKey());

    }

}