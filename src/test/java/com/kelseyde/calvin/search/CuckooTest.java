package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CuckooTest {

    @Test
    public void testNoUpcomingRepetition() {

        Board board = Board.from(FEN.STARTPOS);
        board.makeMove(Move.fromUCI("e2e4"));
        board.makeMove(Move.fromUCI("e7e5"));
        board.makeMove(Move.fromUCI("g1f3"));
        board.makeMove(Move.fromUCI("b8c6"));
        board.makeMove(Move.fromUCI("f1b5"));
        board.makeMove(Move.fromUCI("g8f6"));
        board.makeMove(Move.fromUCI("b1c3"));

        Assertions.assertFalse(board.hasUpcomingRepetition(0));

    }

    @Test
    public void testUpcomingRepetition() {

        Board board = Board.from(FEN.STARTPOS);
        board.makeMove(Move.fromUCI("e2e4"));
        board.makeMove(Move.fromUCI("e7e5"));
        board.makeMove(Move.fromUCI("g1f3"));
        board.makeMove(Move.fromUCI("b8c6"));
        board.makeMove(Move.fromUCI("f1b5"));
        board.makeMove(Move.fromUCI("g8f6"));
        board.makeMove(Move.fromUCI("b1c3"));
        board.makeMove(Move.fromUCI("f6g8"));

        Assertions.assertTrue(board.hasUpcomingRepetition(0));

    }

}