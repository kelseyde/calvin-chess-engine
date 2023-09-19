package com.kelseyde.calvin.movegeneration.drawcalculator;

import com.kelseyde.calvin.board.result.DrawType;
import com.kelseyde.calvin.board.Game;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.result.DrawResult;
import com.kelseyde.calvin.board.result.GameResult;
import com.kelseyde.calvin.utils.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByRepetitionTest {

    @Test
    public void testSimpleDrawByRepetition() {

        Game game = new Game();
        game.makeMove(move("g1", "f3"));
        game.makeMove(move("g8", "f6"));

        game.makeMove(move("f3", "g1"));
        game.makeMove(move("f6", "g8"));

        game.makeMove(move("g1", "f3"));
        game.makeMove(move("g8", "f6"));

        game.makeMove(move("f3", "g1"));
        game.makeMove(move("f6", "g8"));

        game.makeMove(move("g1", "f3"));
        game.makeMove(move("g8", "f6"));

        game.makeMove(move("f3", "g1"));
        GameResult result = game.makeMove(move("f6", "g8"));

        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        Assertions.assertEquals(DrawType.THREEFOLD_REPETITION, ((DrawResult) result).getDrawType());

    }

    @Test
    public void testNotRepetitionIfCastlingRightsAreDifferent() {

        Game game = new Game();
        game.makeMove(move("e2", "e4"));
        game.makeMove(move("e7", "e5"));

        game.makeMove(move("g1", "f3"));
        game.makeMove(move("g8", "f6"));

        game.makeMove(move("f1", "e2"));
        game.makeMove(move("f8", "e7"));

        // king can castle, moves instead
        game.makeMove(move("e1", "f1"));
        game.makeMove(move("e8", "f8"));

        game.makeMove(move("f1", "e1"));
        game.makeMove(move("f8", "e8"));

        // position of pieces repeated twice, but now no castling rights
        game.makeMove(move("e1", "f1"));
        game.makeMove(move("e8", "f8"));

        game.makeMove(move("f1", "e1"));
        GameResult result = game.makeMove(move("f8", "e8"));

        // position of pieces repeated thrice, but on the first occurrence castling rights were different
        Assertions.assertNotEquals(GameResult.ResultType.DRAW, result.getResultType());

    }

    @Test
    public void testRepetitionDependingOnCastlingRights() {

        Game game = new Game();
        game.makeMove(move("e2", "e4"));
        game.makeMove(move("e7", "e5"));

        game.makeMove(move("g1", "f3"));
        game.makeMove(move("g8", "f6"));

        game.makeMove(move("f1", "e2"));
        game.makeMove(move("f8", "e7"));

        // king can castle, moves instead
        game.makeMove(move("e1", "f1"));
        game.makeMove(move("e8", "f8"));

        game.makeMove(move("f1", "e1"));
        game.makeMove(move("f8", "e8"));

        // position of pieces repeated twice, but different castling rights
        game.makeMove(move("e1", "f1"));
        game.makeMove(move("e8", "f8"));

        game.makeMove(move("f1", "e1"));
        game.makeMove(move("f8", "e8"));

        // position of pieces repeated thrice, castling rights repeated 2 times
        game.makeMove(move("e1", "f1"));
        game.makeMove(move("e8", "f8"));

        game.makeMove(move("f1", "e1"));
        GameResult result = game.makeMove(move("f8", "e8"));

        // position of pieces repeated four times, three times with same castling rights
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        Assertions.assertEquals(DrawType.THREEFOLD_REPETITION, ((DrawResult) result).getDrawType());

    }

    @Test
    public void testNotRepetitionIfEnPassantRightsAreDifferent() {

        Game game = new Game();
        game.makeMove(move("e2", "e4"));
        game.makeMove(move("e7", "e6"));

        game.makeMove(move("e4", "e5"));
        game.makeMove(move("d7", "d5"));

        // white can en passant on d6
        game.makeMove(move("g1", "f3"));
        game.makeMove(move("g8", "f6"));

        game.makeMove(move("f3", "g1"));
        game.makeMove(move("f6", "g8"));

        // position of pieces repeated twice, but en passant rights different
        game.makeMove(move("g1", "f3"));
        game.makeMove(move("g8", "f6"));

        game.makeMove(move("f3", "g1"));
        GameResult result = game.makeMove(move("f6", "g8"));

        // position of pieces repeated thrice, but en passant rights different
        Assertions.assertNotEquals(GameResult.ResultType.DRAW, result.getResultType());

    }

    @Test
    public void testRepetitionDependingOnEnPassantRights() {

        Game game = new Game();
        game.makeMove(move("e2", "e4"));
        game.makeMove(move("e7", "e6"));

        game.makeMove(move("e4", "e5"));
        game.makeMove(move("d7", "d5"));

        // white can en passant on d6
        game.makeMove(move("g1", "f3"));
        game.makeMove(move("g8", "f6"));

        game.makeMove(move("f3", "g1"));
        game.makeMove(move("f6", "g8"));

        // position of pieces repeated twice, but en passant rights different
        game.makeMove(move("g1", "f3"));
        game.makeMove(move("g8", "f6"));

        game.makeMove(move("f3", "g1"));
        game.makeMove(move("f6", "g8"));

        // position of pieces repeated thrice, but en passant rights different
        game.makeMove(move("g1", "f3"));
        game.makeMove(move("g8", "f6"));

        game.makeMove(move("f3", "g1"));
        GameResult result = game.makeMove(move("f6", "g8"));

        // position of pieces repeated four times, three times with same en passant rights
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        Assertions.assertEquals(DrawType.THREEFOLD_REPETITION, ((DrawResult) result).getDrawType());

    }

    private Move move(String startSquare, String endSquare) {
        return MoveUtils.fromNotation(startSquare, endSquare);
    }

}
