package com.kelseyde.calvin.service.generator;

import com.kelseyde.calvin.model.game.Game;
import com.kelseyde.calvin.utils.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CheckTest {

    @Test
    public void checkBlocksOtherMoves() {

        Game game = new Game();
        game.makeMove(MoveUtils.fromNotation("e2", "e4"));
        game.makeMove(MoveUtils.fromNotation("e7", "e5"));
        game.makeMove(MoveUtils.fromNotation("d1", "h5"));
        game.makeMove(MoveUtils.fromNotation("d8", "h4"));

        // check
        game.makeMove(MoveUtils.fromNotation("h5", "f7"));

        // try to ignore check with other moves
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                game.makeMove(MoveUtils.fromNotation("a7", "a6")));

    }

    @Test
    public void cannotMovePinnedPawn() {

        Game game = new Game();
        game.makeMove(MoveUtils.fromNotation("e2", "e4"));
        game.makeMove(MoveUtils.fromNotation("e7", "e5"));
        game.makeMove(MoveUtils.fromNotation("d1", "h5"));

        // black tries to move pinned f-pawn
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                game.makeMove(MoveUtils.fromNotation("f7", "f6")));

    }

    @Test
    public void cannotEnPassantWithPinnedPawn() {

        Game game = new Game();
        game.makeMove(MoveUtils.fromNotation("e2", "e4"));
        game.makeMove(MoveUtils.fromNotation("e7", "e5"));
        game.makeMove(MoveUtils.fromNotation("d2", "d4"));
        game.makeMove(MoveUtils.fromNotation("e5", "d4"));
        game.makeMove(MoveUtils.fromNotation("e4", "e5"));
        game.makeMove(MoveUtils.fromNotation("d8", "e7"));
        game.makeMove(MoveUtils.fromNotation("g1", "f3"));
        game.makeMove(MoveUtils.fromNotation("d7", "d5"));

        // black tries to en-passant with pinned e-pawn
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                game.makeMove(MoveUtils.fromNotation("e5", "d6")));

    }

    @Test
    public void cannotMovePinnedKnight() {
        Game game = new Game();
        game.makeMove(MoveUtils.fromNotation("e2", "e4"));
        game.makeMove(MoveUtils.fromNotation("f7", "f5"));
        game.makeMove(MoveUtils.fromNotation("e4", "f5"));
        game.makeMove(MoveUtils.fromNotation("e7", "e6"));
        game.makeMove(MoveUtils.fromNotation("d2", "d4"));
        game.makeMove(MoveUtils.fromNotation("e6", "f5"));
        game.makeMove(MoveUtils.fromNotation("d1", "e2"));
        // block check with knight
        game.makeMove(MoveUtils.fromNotation("g8", "e7"));
        game.makeMove(MoveUtils.fromNotation("a2", "a4"));
        //try moving pinned knight
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                game.makeMove(MoveUtils.fromNotation("e7", "g8")));
    }

    @Test
    public void cannotMovePinnedBishop() {
        Game game = new Game();
        game.makeMove(MoveUtils.fromNotation("e2", "e4"));
        game.makeMove(MoveUtils.fromNotation("f7", "f5"));
        game.makeMove(MoveUtils.fromNotation("e4", "f5"));
        game.makeMove(MoveUtils.fromNotation("e7", "e6"));
        game.makeMove(MoveUtils.fromNotation("d2", "d4"));
        game.makeMove(MoveUtils.fromNotation("e6", "f5"));
        game.makeMove(MoveUtils.fromNotation("d1", "e2"));
        // block check with bishop
        game.makeMove(MoveUtils.fromNotation("f8", "e7"));
        game.makeMove(MoveUtils.fromNotation("a2", "a4"));
        //try moving pinned bishop
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                game.makeMove(MoveUtils.fromNotation("e7", "f8")));
    }

    @Test
    public void cannotMovePinnedQueen() {
        Game game = new Game();
        game.makeMove(MoveUtils.fromNotation("e2", "e4"));
        game.makeMove(MoveUtils.fromNotation("f7", "f5"));
        game.makeMove(MoveUtils.fromNotation("e4", "f5"));
        game.makeMove(MoveUtils.fromNotation("e7", "e6"));
        game.makeMove(MoveUtils.fromNotation("d2", "d4"));
        game.makeMove(MoveUtils.fromNotation("e6", "f5"));
        game.makeMove(MoveUtils.fromNotation("d1", "e2"));
        // block check with queen
        game.makeMove(MoveUtils.fromNotation("d8", "e7"));
        game.makeMove(MoveUtils.fromNotation("a2", "a4"));
        //try moving pinned queen
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                game.makeMove(MoveUtils.fromNotation("e7", "d8")));
    }

    @Test
    public void cannotMoveFromCheckIntoAnotherCheck() {

        Game game = new Game();
        game.makeMove(MoveUtils.fromNotation("e2", "e4"));
        game.makeMove(MoveUtils.fromNotation("e7", "e5"));
        game.makeMove(MoveUtils.fromNotation("d1", "h5"));
        game.makeMove(MoveUtils.fromNotation("d8", "h4"));

        // check
        game.makeMove(MoveUtils.fromNotation("h5", "f7"));

        // try to move to another checked square
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                game.makeMove(MoveUtils.fromNotation("e8", "e7")));

    }

    @Test
    public void canCaptureUnprotectedCheckingPiece() {

        Game game = new Game();
        game.makeMove(MoveUtils.fromNotation("e2", "e4"));
        game.makeMove(MoveUtils.fromNotation("e7", "e5"));
        game.makeMove(MoveUtils.fromNotation("d1", "h5"));
        game.makeMove(MoveUtils.fromNotation("d8", "h4"));

        // check
        game.makeMove(MoveUtils.fromNotation("h5", "f7"));
        // capture checking queen
        game.makeMove(MoveUtils.fromNotation("e8", "f7"));

    }

    @Test
    public void cannotCaptureProtectedCheckingPieceWithKing() {

        Game game = new Game();
        game.makeMove(MoveUtils.fromNotation("e2", "e4"));
        game.makeMove(MoveUtils.fromNotation("e7", "e5"));
        game.makeMove(MoveUtils.fromNotation("f1", "c4"));
        game.makeMove(MoveUtils.fromNotation("b8", "c6"));
        // wayward queen!
        game.makeMove(MoveUtils.fromNotation("d1", "h5"));
        game.makeMove(MoveUtils.fromNotation("d8", "e7"));

        // check
        game.makeMove(MoveUtils.fromNotation("h5", "f7"));
        // try capturing checking queen with king
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.makeMove(MoveUtils.fromNotation("e8", "f7")));

    }

    @Test
    public void canCaptureProtectedCheckingPieceWithOtherPiece() {

        Game game = new Game();
        game.makeMove(MoveUtils.fromNotation("e2", "e4"));
        game.makeMove(MoveUtils.fromNotation("e7", "e5"));
        game.makeMove(MoveUtils.fromNotation("f1", "c4"));
        game.makeMove(MoveUtils.fromNotation("b8", "c6"));
        // wayward queen!
        game.makeMove(MoveUtils.fromNotation("d1", "h5"));
        game.makeMove(MoveUtils.fromNotation("d8", "e7"));

        // check
        game.makeMove(MoveUtils.fromNotation("h5", "f7"));
        // try capturing checking queen with queen
        game.makeMove(MoveUtils.fromNotation("e7", "f7"));

    }

    @Test
    public void cannotCastleOutOfCheck() {

        Game game = new Game();
        game.makeMove(MoveUtils.fromNotation("e2", "e4"));
        game.makeMove(MoveUtils.fromNotation("e7", "e6"));
        game.makeMove(MoveUtils.fromNotation("d2", "d4"));
        game.makeMove(MoveUtils.fromNotation("d7", "d5"));
        game.makeMove(MoveUtils.fromNotation("e4", "d5"));
        game.makeMove(MoveUtils.fromNotation("e6", "d5"));
        game.makeMove(MoveUtils.fromNotation("g1", "f3"));
        game.makeMove(MoveUtils.fromNotation("g8", "f6"));
        game.makeMove(MoveUtils.fromNotation("f1", "d3"));
        game.makeMove(MoveUtils.fromNotation("f8", "d6"));

        // check
        game.makeMove(MoveUtils.fromNotation("d1", "e2"));

        // try to castle out of check
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.makeMove(MoveUtils.fromNotation("e8", "g8")));

    }

    @Test
    public void cannotCastleThroughCheck() {

        Game game = new Game();
        game.makeMove(MoveUtils.fromNotation("e2", "e4"));
        game.makeMove(MoveUtils.fromNotation("e7", "e5"));
        game.makeMove(MoveUtils.fromNotation("f2", "f4"));
        game.makeMove(MoveUtils.fromNotation("b7", "b6"));
        game.makeMove(MoveUtils.fromNotation("f1", "a6"));
        game.makeMove(MoveUtils.fromNotation("c8", "a6"));
        game.makeMove(MoveUtils.fromNotation("g1", "f3"));
        game.makeMove(MoveUtils.fromNotation("d7", "d6"));

        // try to castle through the bishop check
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.makeMove(MoveUtils.fromNotation("e1", "g1")));

    }

}
