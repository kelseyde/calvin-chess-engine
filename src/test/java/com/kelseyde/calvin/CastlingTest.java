package com.kelseyde.calvin;

import com.kelseyde.calvin.model.game.Game;
import com.kelseyde.calvin.utils.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CastlingTest {

    @Test
    public void testSimpleKingsideCastling() {

        Game game = new Game();
        game.handleMove(MoveUtils.fromNotation("e2", "e4"));
        game.handleMove(MoveUtils.fromNotation("e7", "e5"));
        game.handleMove(MoveUtils.fromNotation("g1", "f3"));
        game.handleMove(MoveUtils.fromNotation("g8", "f6"));
        game.handleMove(MoveUtils.fromNotation("f1", "b5"));
        game.handleMove(MoveUtils.fromNotation("f8", "b4"));

        // white castles
        game.handleMove(MoveUtils.fromNotation("e1", "g1"));

        // black castles
        game.handleMove(MoveUtils.fromNotation("e8", "g8"));

    }

    @Test
    public void testSimpleQueensideCastling() {

        Game game = new Game();
        game.handleMove(MoveUtils.fromNotation("d2", "d4"));
        game.handleMove(MoveUtils.fromNotation("d7", "d5"));
        game.handleMove(MoveUtils.fromNotation("b1", "c3"));
        game.handleMove(MoveUtils.fromNotation("b8", "c6"));
        game.handleMove(MoveUtils.fromNotation("c1", "f4"));
        game.handleMove(MoveUtils.fromNotation("c8", "f5"));
        game.handleMove(MoveUtils.fromNotation("d1", "d2"));
        game.handleMove(MoveUtils.fromNotation("d8", "d7"));

        // white castles
        game.handleMove(MoveUtils.fromNotation("e1", "c1"));

        // black castles
        game.handleMove(MoveUtils.fromNotation("e8", "c8"));

    }

    @Test
    public void cannotCastleIfPiecesInTheWay() {

        Game game = new Game();

        // white tries to kingside castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e1", "g1")));

        // white makes legal move instead
        game.handleMove(MoveUtils.fromNotation("e2", "e4"));

        // black tries to kingside castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e8", "g8")));

        // black makes legal move instead
        game.handleMove(MoveUtils.fromNotation("e7", "e5"));

        // white tries to queenside castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e1", "c1")));

        // white makes legal move instead
        game.handleMove(MoveUtils.fromNotation("d2", "d4"));

        // black tries to queenside castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e8", "c8")));
    }

    @Test
    public void cannotKingsideCastleIfKingNotOnStartingSquare() {

        Game game = new Game();
        game.handleMove(MoveUtils.fromNotation("e2", "e4"));
        game.handleMove(MoveUtils.fromNotation("e7", "e5"));
        game.handleMove(MoveUtils.fromNotation("g1", "f3"));
        game.handleMove(MoveUtils.fromNotation("g8", "f6"));
        game.handleMove(MoveUtils.fromNotation("f1", "b5"));
        game.handleMove(MoveUtils.fromNotation("f8", "e7"));

        // white king moves
        game.handleMove(MoveUtils.fromNotation("e1", "f1"));
        // black king moves
        game.handleMove(MoveUtils.fromNotation("e8", "f8"));

        // white tries to castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e1", "g1")));

        // white makes a legal move instead
        game.handleMove(MoveUtils.fromNotation("a2", "a3"));

        // black tries to castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e8", "g8")));
    }

    @Test
    public void cannotQueensideCastleIfKingNotOnStartingSquare() {

        Game game = new Game();
        game.handleMove(MoveUtils.fromNotation("d2", "d4"));
        game.handleMove(MoveUtils.fromNotation("d7", "d5"));
        game.handleMove(MoveUtils.fromNotation("b1", "c3"));
        game.handleMove(MoveUtils.fromNotation("b8", "c6"));
        game.handleMove(MoveUtils.fromNotation("c1", "f4"));
        game.handleMove(MoveUtils.fromNotation("c8", "f5"));
        game.handleMove(MoveUtils.fromNotation("d1", "d2"));
        game.handleMove(MoveUtils.fromNotation("d8", "d7"));

        // white king moves
        game.handleMove(MoveUtils.fromNotation("e1", "d1"));
        // black king moves
        game.handleMove(MoveUtils.fromNotation("e8", "d8"));

        // white tries to castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e1", "c1")));

        // white makes a legal move instead
        game.handleMove(MoveUtils.fromNotation("a2", "a3"));

        // black tries to castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e8", "c8")));

    }

    @Test
    public void cannotKingsideCastleIfKingHasMoved() {

        Game game = new Game();
        game.handleMove(MoveUtils.fromNotation("e2", "e4"));
        game.handleMove(MoveUtils.fromNotation("e7", "e5"));
        game.handleMove(MoveUtils.fromNotation("g1", "f3"));
        game.handleMove(MoveUtils.fromNotation("g8", "f6"));
        game.handleMove(MoveUtils.fromNotation("f1", "b5"));
        game.handleMove(MoveUtils.fromNotation("f8", "e7"));

        // white king moves
        game.handleMove(MoveUtils.fromNotation("e1", "f1"));
        // black king moves
        game.handleMove(MoveUtils.fromNotation("e8", "f8"));

        // white king moves back
        game.handleMove(MoveUtils.fromNotation("f1", "e1"));
        // black king moves back
        game.handleMove(MoveUtils.fromNotation("f8", "e8"));

        // white tries to castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e1", "g1")));

        // white makes a legal move instead
        game.handleMove(MoveUtils.fromNotation("a2", "a3"));

        // black tries to castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e8", "g8")));
    }

    @Test
    public void cannotQueensideCastleIfKingHasMoved() {

        Game game = new Game();
        game.handleMove(MoveUtils.fromNotation("d2", "d4"));
        game.handleMove(MoveUtils.fromNotation("d7", "d5"));
        game.handleMove(MoveUtils.fromNotation("b1", "c3"));
        game.handleMove(MoveUtils.fromNotation("b8", "c6"));
        game.handleMove(MoveUtils.fromNotation("c1", "f4"));
        game.handleMove(MoveUtils.fromNotation("c8", "f5"));
        game.handleMove(MoveUtils.fromNotation("d1", "d2"));
        game.handleMove(MoveUtils.fromNotation("d8", "d7"));

        // white king moves
        game.handleMove(MoveUtils.fromNotation("e1", "d1"));
        // black king moves
        game.handleMove(MoveUtils.fromNotation("e8", "d8"));

        // white king moves back
        game.handleMove(MoveUtils.fromNotation("d1", "e1"));
        // black king moves back
        game.handleMove(MoveUtils.fromNotation("d8", "e8"));

        // white tries to castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e1", "c1")));

        // white makes a legal move instead
        game.handleMove(MoveUtils.fromNotation("a2", "a3"));

        // black tries to castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e8", "c8")));

    }

    @Test
    public void cannotKingsideCastleIfRookHasMoved() {

        Game game = new Game();
        game.handleMove(MoveUtils.fromNotation("e2", "e4"));
        game.handleMove(MoveUtils.fromNotation("e7", "e5"));
        game.handleMove(MoveUtils.fromNotation("g1", "f3"));
        game.handleMove(MoveUtils.fromNotation("g8", "f6"));
        game.handleMove(MoveUtils.fromNotation("f1", "b5"));
        game.handleMove(MoveUtils.fromNotation("f8", "e7"));

        // white rook moves
        game.handleMove(MoveUtils.fromNotation("h1", "g1"));
        // black rook moves
        game.handleMove(MoveUtils.fromNotation("h8", "g8"));

        // white rook moves back
        game.handleMove(MoveUtils.fromNotation("g1", "h1"));
        // black rook moves back
        game.handleMove(MoveUtils.fromNotation("g8", "h8"));

        // white tries to castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e1", "g1")));

        // white makes a legal move instead
        game.handleMove(MoveUtils.fromNotation("a2", "a3"));

        // black tries to castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e8", "g8")));
    }

    @Test
    public void cannotQueensideCastleIfRookHasMoved() {

        Game game = new Game();
        game.handleMove(MoveUtils.fromNotation("d2", "d4"));
        game.handleMove(MoveUtils.fromNotation("d7", "d5"));
        game.handleMove(MoveUtils.fromNotation("b1", "c3"));
        game.handleMove(MoveUtils.fromNotation("b8", "c6"));
        game.handleMove(MoveUtils.fromNotation("c1", "f4"));
        game.handleMove(MoveUtils.fromNotation("c8", "f5"));
        game.handleMove(MoveUtils.fromNotation("d1", "d2"));
        game.handleMove(MoveUtils.fromNotation("d8", "d7"));

        // white rook moves
        game.handleMove(MoveUtils.fromNotation("a1", "b1"));
        // black rook moves
        game.handleMove(MoveUtils.fromNotation("a8", "b8"));

        // white rook moves back
        game.handleMove(MoveUtils.fromNotation("b1", "a1"));
        // black rook moves back
        game.handleMove(MoveUtils.fromNotation("b8", "a8"));

        // white tries to castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e1", "c1")));

        // white makes a legal move instead
        game.handleMove(MoveUtils.fromNotation("a2", "a3"));

        // black tries to castle
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> game.handleMove(MoveUtils.fromNotation("e8", "c8")));

    }

}
