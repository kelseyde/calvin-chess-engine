package com.kelseyde.calvin.movegeneration.drawcalculator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.movegeneration.result.GameResult;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByRepetitionTest {
    
    private final ResultCalculator resultEvaluator = new ResultCalculator();

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

        GameResult result = resultEvaluator.calculateResult(board);
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
        GameResult result = resultEvaluator.calculateResult(board);
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
        GameResult result = resultEvaluator.calculateResult(board);
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
        GameResult result = resultEvaluator.calculateResult(board);
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
        // position of pieces repeated twice, but en passant rights different
        board.makeMove(TestUtils.getLegalMove(board, "f6", "g8"));

        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));

        board.makeMove(TestUtils.getLegalMove(board, "f3", "g1"));
        // position of pieces repeated thrice, but en passant rights different
        board.makeMove(TestUtils.getLegalMove(board, "f6", "g8"));

        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_REPETITION, result);

    }

    @Test
    public void testGameExampleDrawRepetition() {

        String fen = "7r/4b1p1/8/3BkP2/4N3/8/PPn2PP1/1R1R2K1 b - - 0 26";
        Board board = FEN.fromFEN(fen);

        board.makeMove(NotationUtils.fromNotation("h8", "b8"));
        board.makeMove(NotationUtils.fromNotation("e4", "c3"));
        board.makeMove(NotationUtils.fromNotation("e7", "c5"));
        board.makeMove(NotationUtils.fromNotation("c3", "e4"));
        board.makeMove(NotationUtils.fromNotation("c5", "e7"));
        board.makeMove(NotationUtils.fromNotation("e4", "c3"));
        board.makeMove(NotationUtils.fromNotation("e7", "c5"));
        board.makeMove(NotationUtils.fromNotation("c3", "e4"));

        GameResult result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.IN_PROGRESS, result);

        board.makeMove(NotationUtils.fromNotation("c5", "e7"));

        result = resultEvaluator.calculateResult(board);
        Assertions.assertEquals(GameResult.DRAW_BY_REPETITION, result);

    }

}
