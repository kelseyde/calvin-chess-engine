package com.kelseyde.calvin.service.drawcalculator;

import com.kelseyde.calvin.model.game.*;
import com.kelseyde.calvin.utils.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByRepetitionTest {

    @Test
    public void testSimpleDrawByRepetition() {

        Game game = new Game();
        game.executeAction(move("g1", "f3"));
        game.executeAction(move("g8", "f6"));

        game.executeAction(move("f3", "g1"));
        game.executeAction(move("f6", "g8"));

        game.executeAction(move("g1", "f3"));
        game.executeAction(move("g8", "f6"));

        game.executeAction(move("f3", "g1"));
        game.executeAction(move("f6", "g8"));

        game.executeAction(move("g1", "f3"));
        game.executeAction(move("g8", "f6"));

        game.executeAction(move("f3", "g1"));
        ActionResult result = game.executeAction(move("f6", "g8"));

        Assertions.assertTrue(result.isDraw());
        Assertions.assertEquals(DrawType.THREEFOLD_REPETITION, result.getDrawType());

    }

    @Test
    public void testNotRepetitionIfCastlingRightsAreDifferent() {

        Game game = new Game();
        game.executeAction(move("e2", "e4"));
        game.executeAction(move("e7", "e5"));

        game.executeAction(move("g1", "f3"));
        game.executeAction(move("g8", "f6"));

        game.executeAction(move("f1", "e2"));
        game.executeAction(move("f8", "e7"));

        // king can castle, moves instead
        game.executeAction(move("e1", "f1"));
        game.executeAction(move("e8", "f8"));

        game.executeAction(move("f1", "e1"));
        game.executeAction(move("f8", "e8"));

        // position of pieces repeated twice, but now no castling rights
        game.executeAction(move("e1", "f1"));
        game.executeAction(move("e8", "f8"));

        game.executeAction(move("f1", "e1"));
        ActionResult result = game.executeAction(move("f8", "e8"));

        // position of pieces repeated thrice, but on the first occurrence castling rights were different
        Assertions.assertFalse(result.isDraw());

    }

    @Test
    public void testRepetitionDependingOnCastlingRights() {

        Game game = new Game();
        game.executeAction(move("e2", "e4"));
        game.executeAction(move("e7", "e5"));

        game.executeAction(move("g1", "f3"));
        game.executeAction(move("g8", "f6"));

        game.executeAction(move("f1", "e2"));
        game.executeAction(move("f8", "e7"));

        // king can castle, moves instead
        game.executeAction(move("e1", "f1"));
        game.executeAction(move("e8", "f8"));

        game.executeAction(move("f1", "e1"));
        game.executeAction(move("f8", "e8"));

        // position of pieces repeated twice, but different castling rights
        game.executeAction(move("e1", "f1"));
        game.executeAction(move("e8", "f8"));

        game.executeAction(move("f1", "e1"));
        game.executeAction(move("f8", "e8"));

        // position of pieces repeated thrice, castling rights repeated 2 times
        game.executeAction(move("e1", "f1"));
        game.executeAction(move("e8", "f8"));

        game.executeAction(move("f1", "e1"));
        ActionResult result = game.executeAction(move("f8", "e8"));

        // position of pieces repeated four times, three times with same castling rights
        Assertions.assertTrue(result.isDraw());
        Assertions.assertEquals(DrawType.THREEFOLD_REPETITION, result.getDrawType());

    }

    @Test
    public void testNotRepetitionIfEnPassantRightsAreDifferent() {

        Game game = new Game();
        game.executeAction(move("e2", "e4"));
        game.executeAction(move("e7", "e6"));

        game.executeAction(move("e4", "e5"));
        game.executeAction(move("d7", "d5"));

        // white can en passant on d6
        game.executeAction(move("g1", "f3"));
        game.executeAction(move("g8", "f6"));

        game.executeAction(move("f3", "g1"));
        game.executeAction(move("f6", "g8"));

        // position of pieces repeated twice, but en passant rights different
        game.executeAction(move("g1", "f3"));
        game.executeAction(move("g8", "f6"));

        game.executeAction(move("f3", "g1"));
        ActionResult result = game.executeAction(move("f6", "g8"));

        // position of pieces repeated thrice, but en passant rights different
        Assertions.assertFalse(result.isDraw());

    }

    @Test
    public void testRepetitionDependingOnEnPassantRights() {

        Game game = new Game();
        game.executeAction(move("e2", "e4"));
        game.executeAction(move("e7", "e6"));

        game.executeAction(move("e4", "e5"));
        game.executeAction(move("d7", "d5"));

        // white can en passant on d6
        game.executeAction(move("g1", "f3"));
        game.executeAction(move("g8", "f6"));

        game.executeAction(move("f3", "g1"));
        game.executeAction(move("f6", "g8"));

        // position of pieces repeated twice, but en passant rights different
        game.executeAction(move("g1", "f3"));
        game.executeAction(move("g8", "f6"));

        game.executeAction(move("f3", "g1"));
        game.executeAction(move("f6", "g8"));

        // position of pieces repeated thrice, but en passant rights different
        game.executeAction(move("g1", "f3"));
        game.executeAction(move("g8", "f6"));

        game.executeAction(move("f3", "g1"));
        ActionResult result = game.executeAction(move("f6", "g8"));

        // position of pieces repeated four times, three times with same en passant rights
        Assertions.assertTrue(result.isDraw());
        Assertions.assertEquals(DrawType.THREEFOLD_REPETITION, result.getDrawType());

    }

    private GameAction move(String startSquare, String endSquare) {
        return GameAction.builder()
                .actionType(ActionType.MOVE)
                .move(MoveUtils.fromNotation(startSquare, endSquare))
                .build();
    }

}
