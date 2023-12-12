package com.kelseyde.calvin.board;

import com.kelseyde.calvin.utils.IllegalMoveException;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CastlingTest {

    @Test
    public void testSimpleKingsideCastling() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "b5"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "b4"));

        // white castles
        board.makeMove(TestUtils.getLegalMove(board, "e1", "g1"));

        // black castles
        board.makeMove(TestUtils.getLegalMove(board, "e8", "g8"));

    }

    @Test
    public void testSimpleQueensideCastling() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));
        board.makeMove(TestUtils.getLegalMove(board, "d7", "d5"));
        board.makeMove(TestUtils.getLegalMove(board, "b1", "c3"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "c6"));
        board.makeMove(TestUtils.getLegalMove(board, "c1", "f4"));
        board.makeMove(TestUtils.getLegalMove(board, "c8", "f5"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "d2"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "d7"));

        // white castles
        board.makeMove(TestUtils.getLegalMove(board, "e1", "c1"));

        // black castles
        board.makeMove(TestUtils.getLegalMove(board, "e8", "c8"));

    }

    @Test
    public void cannotCastleIfAllPiecesInTheWay() {

        Board board = new Board();

        // white tries to kingside castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e1", "g1")));

        // white makes legal move instead
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));

        // black tries to kingside castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e8", "g8")));

        // black makes legal move instead
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));

        // white tries to queenside castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e1", "c1")));

        // white makes legal move instead
        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));

        // black tries to queenside castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e8", "c8")));
    }

    @Test
    public void cannotKingsideCastleIfSomePiecesInTheWay() {

        Board board = new Board();

        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));

        // white tries to kingside castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e1", "g1")));

    }

    @Test
    public void cannotQueensideCastleIfSomePiecesInTheWay() {

        Board board = new Board();

        board.makeMove(TestUtils.getLegalMove(board, "b1", "c3"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));

        // white tries to kingside castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e1", "c1")));

    }

    @Test
    public void cannotKingsideCastleIfKingNotOnStartingSquare() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "b5"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "e7"));

        // white king moves
        board.makeMove(TestUtils.getLegalMove(board, "e1", "f1"));
        // black king moves
        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));

        // white tries to castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e1", "g1")));

        // white makes a legal move instead
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));

        // black tries to castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e8", "g8")));
    }

    @Test
    public void cannotQueensideCastleIfKingNotOnStartingSquare() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));
        board.makeMove(TestUtils.getLegalMove(board, "d7", "d5"));
        board.makeMove(TestUtils.getLegalMove(board, "b1", "c3"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "c6"));
        board.makeMove(TestUtils.getLegalMove(board, "c1", "f4"));
        board.makeMove(TestUtils.getLegalMove(board, "c8", "f5"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "d2"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "d7"));

        // white king moves
        board.makeMove(TestUtils.getLegalMove(board, "e1", "d1"));
        // black king moves
        board.makeMove(TestUtils.getLegalMove(board, "e8", "d8"));

        // white tries to castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e1", "c1")));

        // white makes a legal move instead
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));

        // black tries to castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e8", "c8")));

    }

    @Test
    public void cannotKingsideCastleIfKingHasMoved() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "b5"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "e7"));

        // white king moves
        board.makeMove(TestUtils.getLegalMove(board, "e1", "f1"));
        // black king moves
        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));

        // white king moves back
        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));
        // black king moves back
        board.makeMove(TestUtils.getLegalMove(board, "f8", "e8"));

        // white tries to castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e1", "g1")));

        // white makes a legal move instead
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));

        // black tries to castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e8", "g8")));
    }

    @Test
    public void cannotQueensideCastleIfKingHasMoved() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));
        board.makeMove(TestUtils.getLegalMove(board, "d7", "d5"));
        board.makeMove(TestUtils.getLegalMove(board, "b1", "c3"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "c6"));
        board.makeMove(TestUtils.getLegalMove(board, "c1", "f4"));
        board.makeMove(TestUtils.getLegalMove(board, "c8", "f5"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "d2"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "d7"));

        // white king moves
        board.makeMove(TestUtils.getLegalMove(board, "e1", "d1"));
        // black king moves
        board.makeMove(TestUtils.getLegalMove(board, "e8", "d8"));

        // white king moves back
        board.makeMove(TestUtils.getLegalMove(board, "d1", "e1"));
        // black king moves back
        board.makeMove(TestUtils.getLegalMove(board, "d8", "e8"));

        // white tries to castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e1", "c1")));

        // white makes a legal move instead
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));

        // black tries to castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e8", "c8")));

    }

    @Test
    public void cannotKingsideCastleIfRookHasMoved() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "b5"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "e7"));

        // white rook moves
        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));
        // black rook moves
        board.makeMove(TestUtils.getLegalMove(board, "h8", "g8"));

        // white rook moves back
        board.makeMove(TestUtils.getLegalMove(board, "g1", "h1"));
        // black rook moves back
        board.makeMove(TestUtils.getLegalMove(board, "g8", "h8"));

        // white tries to castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e1", "g1")));

        // white makes a legal move instead
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));

        // black tries to castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e8", "g8")));
    }

    @Test
    public void cannotQueensideCastleIfRookHasMoved() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));
        board.makeMove(TestUtils.getLegalMove(board, "d7", "d5"));
        board.makeMove(TestUtils.getLegalMove(board, "b1", "c3"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "c6"));
        board.makeMove(TestUtils.getLegalMove(board, "c1", "f4"));
        board.makeMove(TestUtils.getLegalMove(board, "c8", "f5"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "d2"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "d7"));

        // white rook moves
        board.makeMove(TestUtils.getLegalMove(board, "a1", "b1"));
        // black rook moves
        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));

        // white rook moves back
        board.makeMove(TestUtils.getLegalMove(board, "b1", "a1"));
        // black rook moves back
        board.makeMove(TestUtils.getLegalMove(board, "b8", "a8"));

        // white tries to castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e1", "c1")));

        // white makes a legal move instead
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));

        // black tries to castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e8", "c8")));

    }

    @Test
    public void cannotCastleIfKingsideRookIsCaptured() {

        String fen = "r1b1k2r/1p3p2/8/3n4/1P6/2Q5/4P3/6KR b kq - 0 9";
        Board board = FEN.toBoard(fen);

        // black rook captures white rook
        board.makeMove(TestUtils.getLegalMove(board, "h8", "h1"));

        System.out.println("second move");
        // white king captures back
        board.makeMove(TestUtils.getLegalMove(board, "g1", "h1"));

        // black tries to castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e8", "g8")));

    }

    @Test
    public void cannotCastleIfQueensideRookIsCaptured() {
        // TODO
    }

    @Test
    public void whiteCanStillKingsideCastleIfQueensideRookHasMoved() {
        // TODO
    }

    @Test
    public void blackCanStillQueensideCastleIfQueensideRookHasMoved() {
        // TODO
    }

}
