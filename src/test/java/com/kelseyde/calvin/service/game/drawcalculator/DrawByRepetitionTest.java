package com.kelseyde.calvin.service.game.drawcalculator;

import com.kelseyde.calvin.model.game.DrawType;
import com.kelseyde.calvin.model.game.Game;
import com.kelseyde.calvin.model.game.result.DrawResult;
import com.kelseyde.calvin.model.game.result.GameResult;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.utils.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByRepetitionTest {

    @Test
    public void testSimpleDrawByRepetition() {

        Game game = new Game();
        game.playMove(move("g1", "f3"));
        game.playMove(move("g8", "f6"));

        game.playMove(move("f3", "g1"));
        game.playMove(move("f6", "g8"));

        game.playMove(move("g1", "f3"));
        game.playMove(move("g8", "f6"));

        game.playMove(move("f3", "g1"));
        game.playMove(move("f6", "g8"));

        game.playMove(move("g1", "f3"));
        game.playMove(move("g8", "f6"));

        game.playMove(move("f3", "g1"));
        GameResult result = game.playMove(move("f6", "g8"));

        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        Assertions.assertEquals(DrawType.THREEFOLD_REPETITION, ((DrawResult) result).getDrawType());

    }

    @Test
    public void testNotRepetitionIfCastlingRightsAreDifferent() {

        Game game = new Game();
        game.playMove(move("e2", "e4"));
        game.playMove(move("e7", "e5"));

        game.playMove(move("g1", "f3"));
        game.playMove(move("g8", "f6"));

        game.playMove(move("f1", "e2"));
        game.playMove(move("f8", "e7"));

        // king can castle, moves instead
        game.playMove(move("e1", "f1"));
        game.playMove(move("e8", "f8"));

        game.playMove(move("f1", "e1"));
        game.playMove(move("f8", "e8"));

        // position of pieces repeated twice, but now no castling rights
        game.playMove(move("e1", "f1"));
        game.playMove(move("e8", "f8"));

        game.playMove(move("f1", "e1"));
        GameResult result = game.playMove(move("f8", "e8"));

        // position of pieces repeated thrice, but on the first occurrence castling rights were different
        Assertions.assertNotEquals(GameResult.ResultType.DRAW, result.getResultType());

    }

    @Test
    public void testRepetitionDependingOnCastlingRights() {

        Game game = new Game();
        game.playMove(move("e2", "e4"));
        game.playMove(move("e7", "e5"));

        game.playMove(move("g1", "f3"));
        game.playMove(move("g8", "f6"));

        game.playMove(move("f1", "e2"));
        game.playMove(move("f8", "e7"));

        // king can castle, moves instead
        game.playMove(move("e1", "f1"));
        game.playMove(move("e8", "f8"));

        game.playMove(move("f1", "e1"));
        game.playMove(move("f8", "e8"));

        // position of pieces repeated twice, but different castling rights
        game.playMove(move("e1", "f1"));
        game.playMove(move("e8", "f8"));

        game.playMove(move("f1", "e1"));
        game.playMove(move("f8", "e8"));

        // position of pieces repeated thrice, castling rights repeated 2 times
        game.playMove(move("e1", "f1"));
        game.playMove(move("e8", "f8"));

        game.playMove(move("f1", "e1"));
        GameResult result = game.playMove(move("f8", "e8"));

        // position of pieces repeated four times, three times with same castling rights
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        Assertions.assertEquals(DrawType.THREEFOLD_REPETITION, ((DrawResult) result).getDrawType());

    }

    @Test
    public void testNotRepetitionIfEnPassantRightsAreDifferent() {

        Game game = new Game();
        game.playMove(move("e2", "e4"));
        game.playMove(move("e7", "e6"));

        game.playMove(move("e4", "e5"));
        game.playMove(move("d7", "d5"));

        // white can en passant on d6
        game.playMove(move("g1", "f3"));
        game.playMove(move("g8", "f6"));

        game.playMove(move("f3", "g1"));
        game.playMove(move("f6", "g8"));

        // position of pieces repeated twice, but en passant rights different
        game.playMove(move("g1", "f3"));
        game.playMove(move("g8", "f6"));

        game.playMove(move("f3", "g1"));
        GameResult result = game.playMove(move("f6", "g8"));

        // position of pieces repeated thrice, but en passant rights different
        Assertions.assertNotEquals(GameResult.ResultType.DRAW, result.getResultType());

    }

    @Test
    public void testRepetitionDependingOnEnPassantRights() {

        Game game = new Game();
        game.playMove(move("e2", "e4"));
        game.playMove(move("e7", "e6"));

        game.playMove(move("e4", "e5"));
        game.playMove(move("d7", "d5"));

        // white can en passant on d6
        game.playMove(move("g1", "f3"));
        game.playMove(move("g8", "f6"));

        game.playMove(move("f3", "g1"));
        game.playMove(move("f6", "g8"));

        // position of pieces repeated twice, but en passant rights different
        game.playMove(move("g1", "f3"));
        game.playMove(move("g8", "f6"));

        game.playMove(move("f3", "g1"));
        game.playMove(move("f6", "g8"));

        // position of pieces repeated thrice, but en passant rights different
        game.playMove(move("g1", "f3"));
        game.playMove(move("g8", "f6"));

        game.playMove(move("f3", "g1"));
        GameResult result = game.playMove(move("f6", "g8"));

        // position of pieces repeated four times, three times with same en passant rights
        Assertions.assertEquals(GameResult.ResultType.DRAW, result.getResultType());
        Assertions.assertEquals(DrawType.THREEFOLD_REPETITION, ((DrawResult) result).getDrawType());

    }

    private Move move(String startSquare, String endSquare) {
        return MoveUtils.fromNotation(startSquare, endSquare);
    }

}
