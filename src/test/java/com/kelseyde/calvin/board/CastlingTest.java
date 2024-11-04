package com.kelseyde.calvin.board;

import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.utils.IllegalMoveException;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CastlingTest {

    @Test
    public void testSimpleKingsideCastling() {

        Board board = Board.from(FEN.STARTPOS);
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
    public void testChess960KingsideCastling() {

        UCI.Options.chess960 = true;

        Board board = Board.from(FEN.STARTPOS);
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "b5"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "b4"));

        // white castles
        board.makeMove(TestUtils.getLegalMove(board, "e1", "h1"));

        // black castles
        board.makeMove(TestUtils.getLegalMove(board, "e8", "h8"));

        UCI.Options.chess960 = false;

    }

    @Test
    public void testSimpleQueensideCastling() {

        Board board = Board.from(FEN.STARTPOS);
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
    public void testChess960QueensideCastling() {

        UCI.Options.chess960 = true;

        Board board = Board.from(FEN.STARTPOS);
        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));
        board.makeMove(TestUtils.getLegalMove(board, "d7", "d5"));
        board.makeMove(TestUtils.getLegalMove(board, "b1", "c3"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "c6"));
        board.makeMove(TestUtils.getLegalMove(board, "c1", "f4"));
        board.makeMove(TestUtils.getLegalMove(board, "c8", "f5"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "d2"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "d7"));

        // white castles
        board.makeMove(TestUtils.getLegalMove(board, "e1", "a1"));

        // black castles
        board.makeMove(TestUtils.getLegalMove(board, "e8", "a8"));

        UCI.Options.chess960 = false;

    }

    @Test
    public void cannotCastleIfAllPiecesInTheWay() {

        Board board = Board.from(FEN.STARTPOS);

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

        Board board = Board.from(FEN.STARTPOS);

        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));

        // white tries to kingside castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e1", "g1")));

    }

    @Test
    public void cannotQueensideCastleIfSomePiecesInTheWay() {

        Board board = Board.from(FEN.STARTPOS);

        board.makeMove(TestUtils.getLegalMove(board, "b1", "c3"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));

        // white tries to kingside castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e1", "c1")));

    }

    @Test
    public void cannotKingsideCastleIfKingNotOnStartingSquare() {

        Board board = Board.from(FEN.STARTPOS);
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

        Board board = Board.from(FEN.STARTPOS);
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

        Board board = Board.from(FEN.STARTPOS);
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

        Board board = Board.from(FEN.STARTPOS);
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

        Board board = Board.from(FEN.STARTPOS);
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

        Board board = Board.from(FEN.STARTPOS);
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

        // white king captures back
        board.makeMove(TestUtils.getLegalMove(board, "g1", "h1"));

        // black tries to castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e8", "g8")));

    }

    @Test
    public void testCannotCastleIfDestinationSquareAttacked() {

        Board board = Board.from("rnbqkbn1/pppppppp/8/6r1/1B6/5N2/PPPPPP1P/RNBQK2R w KQq - 0 1");

        // white tries to castle
        Assertions.assertThrows(IllegalMoveException.class, () -> board.makeMove(TestUtils.getLegalMove(board, "e1", "g1")));

    }

    @Test
    public void testCastleRightSquareEncoding() {

        // including 64, representing no-square
        for (int square = 0; square < 64; square++) {
            Assertions.assertEquals(square, Castling.decode(Castling.encode(square)));
        }

    }

    @Test
    public void castleRightsEmpty() {

        int rights = Castling.empty();
        Assertions.assertFalse(Castling.kingsideAllowed(rights, true));
        Assertions.assertFalse(Castling.kingsideAllowed(rights, false));
        Assertions.assertFalse(Castling.queensideAllowed(rights, true));
        Assertions.assertFalse(Castling.queensideAllowed(rights, false));

    }

    @Test
    public void castleRightsStartpos() {

        int r = Castling.empty();
        r = Castling.setRook(r, true, true, 7);
        Assertions.assertEquals(7, Castling.getRook(r, true, true));

        Board board = Board.from(FEN.STARTPOS);
        int rights = board.getState().getRights();
        Assertions.assertTrue(Castling.kingsideAllowed(rights, true));
        Assertions.assertTrue(Castling.kingsideAllowed(rights, false));
        Assertions.assertTrue(Castling.queensideAllowed(rights, true));
        Assertions.assertTrue(Castling.queensideAllowed(rights, false));

        Assertions.assertEquals(0, Castling.getRook(rights, false, true));
        Assertions.assertEquals(7, Castling.getRook(rights, true, true));
        Assertions.assertEquals(56, Castling.getRook(rights, false, false));
        Assertions.assertEquals(63, Castling.getRook(rights, true, false));

    }

    @Test
    public void updateCastleRights() {

        int rights = Castling.empty();

        for (int i = 0; i < 64; i++) {
            rights = Castling.setRook(rights, true, true, i);
            Assertions.assertEquals(i, Castling.getRook(rights, true, true));
            rights = Castling.setRook(rights, false, true, i);
            Assertions.assertEquals(i, Castling.getRook(rights, false, true));
            rights = Castling.setRook(rights, true, false, i);
            Assertions.assertEquals(i, Castling.getRook(rights, true, false));
            rights = Castling.setRook(rights, false, false, i);
            Assertions.assertEquals(i, Castling.getRook(rights, false, false));
        }

    }

    @Test
    public void shredderFen() {
        Board board = Board.from("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w HAha - 0 1");
        Assertions.assertTrue(Castling.kingsideAllowed(board.getState().getRights(), true));
        Assertions.assertTrue(Castling.queensideAllowed(board.getState().getRights(), true));
        Assertions.assertTrue(Castling.kingsideAllowed(board.getState().getRights(), false));
        Assertions.assertTrue(Castling.queensideAllowed(board.getState().getRights(), false));

        board = Board.from("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w HAh - 0 1");
        Assertions.assertTrue(Castling.kingsideAllowed(board.getState().getRights(), true));
        Assertions.assertTrue(Castling.queensideAllowed(board.getState().getRights(), true));
        Assertions.assertTrue(Castling.kingsideAllowed(board.getState().getRights(), false));
        Assertions.assertFalse(Castling.queensideAllowed(board.getState().getRights(), false));

        board = Board.from("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w HA - 0 1");
        Assertions.assertTrue(Castling.kingsideAllowed(board.getState().getRights(), true));
        Assertions.assertTrue(Castling.queensideAllowed(board.getState().getRights(), true));
        Assertions.assertFalse(Castling.kingsideAllowed(board.getState().getRights(), false));
        Assertions.assertFalse(Castling.queensideAllowed(board.getState().getRights(), false));

        board = Board.from("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w H - 0 1");
        Assertions.assertTrue(Castling.kingsideAllowed(board.getState().getRights(), true));
        Assertions.assertFalse(Castling.queensideAllowed(board.getState().getRights(), true));
        Assertions.assertFalse(Castling.kingsideAllowed(board.getState().getRights(), false));
        Assertions.assertFalse(Castling.queensideAllowed(board.getState().getRights(), false));

        board = Board.from("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1");
        Assertions.assertFalse(Castling.kingsideAllowed(board.getState().getRights(), true));
        Assertions.assertFalse(Castling.queensideAllowed(board.getState().getRights(), true));
        Assertions.assertFalse(Castling.kingsideAllowed(board.getState().getRights(), false));
        Assertions.assertFalse(Castling.queensideAllowed(board.getState().getRights(), false));
    }

}
