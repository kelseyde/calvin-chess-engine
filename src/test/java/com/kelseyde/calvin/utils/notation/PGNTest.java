package com.kelseyde.calvin.utils.notation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Test;

public class PGNTest {

    @Test
    public void testPGN() {
        // Lasker vs Thomas, 1912
        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e6"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "f7", "f5"));
        board.makeMove(TestUtils.getLegalMove(board, "b1", "c3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));
        board.makeMove(TestUtils.getLegalMove(board, "c1", "g5"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "e7"));
        board.makeMove(TestUtils.getLegalMove(board, "g5", "f6"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "f6"));
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "f5", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "c3", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "b7", "b6"));
        board.makeMove(TestUtils.getLegalMove(board, "f3", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "e8", "g8"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "d3"));
        board.makeMove(TestUtils.getLegalMove(board, "c8", "b7"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "h5"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "e7"));
        board.makeMove(TestUtils.getLegalMove(board, "h5", "h7"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "h7"));
        board.makeMove(TestUtils.getLegalMove(board, "e4", "f6"));
        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));
        board.makeMove(TestUtils.getLegalMove(board, "e5", "g4"));
        board.makeMove(TestUtils.getLegalMove(board, "h6", "g5"));
        board.makeMove(TestUtils.getLegalMove(board, "h2", "h4"));
        board.makeMove(TestUtils.getLegalMove(board, "g5", "f4"));
        board.makeMove(TestUtils.getLegalMove(board, "g2", "g3"));
        board.makeMove(TestUtils.getLegalMove(board, "f4", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "d3", "e2"));
        board.makeMove(TestUtils.getLegalMove(board, "f3", "g2"));
        board.makeMove(TestUtils.getLegalMove(board, "h1", "h2"));
        board.makeMove(TestUtils.getLegalMove(board, "g2", "g1"));
        board.makeMove(TestUtils.getLegalMove(board, "e1", "d2"));
        System.out.println(PGN.toPGN(board));
    }

}