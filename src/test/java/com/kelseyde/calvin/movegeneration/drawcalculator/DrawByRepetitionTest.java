package com.kelseyde.calvin.movegeneration.drawcalculator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultEvaluator;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByRepetitionTest {
    
    private final ResultEvaluator resultEvaluator = new ResultEvaluator();

    @Test
    public void testSimpleDrawByRepetition() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));

        board.makeMove(TestUtils.getLegalMove(board, "f3", "g1"));
        board.makeMove(TestUtils.getLegalMove(board, "f6", "g8"));

        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));

        board.makeMove(TestUtils.getLegalMove(board, "f3", "g1"));
        board.makeMove(TestUtils.getLegalMove(board, "f6", "g8"));

        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));

        board.makeMove(TestUtils.getLegalMove(board, "f3", "g1"));
        board.makeMove(TestUtils.getLegalMove(board, "f6", "g8"));

        GameResult result = resultEvaluator.getResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_REPETITION, result);

    }

    @Test
    public void testNotRepetitionIfCastlingRightsAreDifferent() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));

        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));

        board.makeMove(TestUtils.getLegalMove(board, "f1", "e2"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "e7"));

        // king can castle, moves instead
        board.makeMove(TestUtils.getLegalMove(board, "e1", "f1"));
        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));

        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "e8"));

        // position of pieces repeated twice, but now no castling rights
        board.makeMove(TestUtils.getLegalMove(board, "e1", "f1"));
        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));

        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "e8"));

        // position of pieces repeated thrice, but on the first occurrence castling rights were different
        GameResult result = resultEvaluator.getResult(board);
        Assertions.assertNotEquals(GameResult.DRAW_BY_REPETITION, result);

    }

    @Test
    public void testRepetitionDependingOnCastlingRights() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));

        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));

        board.makeMove(TestUtils.getLegalMove(board, "f1", "e2"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "e7"));

        // king can castle, moves instead
        board.makeMove(TestUtils.getLegalMove(board, "e1", "f1"));
        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));

        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "e8"));

        // position of pieces repeated twice, but different castling rights
        board.makeMove(TestUtils.getLegalMove(board, "e1", "f1"));
        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));

        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "e8"));

        // position of pieces repeated thrice, castling rights repeated 2 times
        board.makeMove(TestUtils.getLegalMove(board, "e1", "f1"));
        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));

        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "e8"));

        // position of pieces repeated four times, three times with same castling rights
        GameResult result = resultEvaluator.getResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_REPETITION, result);

    }

    @Test
    public void testNotRepetitionIfEnPassantRightsAreDifferent() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e6"));

        board.makeMove(TestUtils.getLegalMove(board, "e4", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "d7", "d5"));
        // white can en passant on d6
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));

        board.makeMove(TestUtils.getLegalMove(board, "f3", "g1"));
        board.makeMove(TestUtils.getLegalMove(board, "f6", "g8"));

        // position of pieces repeated twice, but en passant rights different
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));

        board.makeMove(TestUtils.getLegalMove(board, "f3", "g1"));
        board.makeMove(TestUtils.getLegalMove(board, "f6", "g8"));

        // position of pieces repeated thrice, but en passant rights different
        GameResult result = resultEvaluator.getResult(board);
        Assertions.assertEquals(GameResult.IN_PROGRESS, result);

    }

    @Test
    public void testRepetitionDependingOnEnPassantRights() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e6"));

        board.makeMove(TestUtils.getLegalMove(board, "e4", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "d7", "d5"));

        // white can en passant on d6
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));

        board.makeMove(TestUtils.getLegalMove(board, "f3", "g1"));
        board.makeMove(TestUtils.getLegalMove(board, "f6", "g8"));

        // position of pieces repeated twice, but en passant rights different
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));

        board.makeMove(TestUtils.getLegalMove(board, "f3", "g1"));
        board.makeMove(TestUtils.getLegalMove(board, "f6", "g8"));

        // position of pieces repeated thrice, but en passant rights different
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        GameResult result = resultEvaluator.getResult(board);
        Assertions.assertEquals(GameResult.IN_PROGRESS, result);
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));

        board.makeMove(TestUtils.getLegalMove(board, "f3", "g1"));
        board.makeMove(TestUtils.getLegalMove(board, "f6", "g8"));

        // position of pieces repeated four times, three times with same en passant rights
        result = resultEvaluator.getResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_REPETITION, result);

    }

}
