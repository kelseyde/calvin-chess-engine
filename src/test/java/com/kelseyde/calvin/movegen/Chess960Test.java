package com.kelseyde.calvin.movegen;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.uci.UCI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Chess960Test {

    private static final MoveGenerator MOVEGEN = new MoveGenerator();

    @BeforeEach
    public void beforeEach() {
        UCI.Options.chess960 = true;
    }

    @AfterEach
    public void afterEach() {
        UCI.Options.chess960 = false;
    }

    @Test
    public void testRookGoesToKingSquare() {

        Board board = Board.from("qnnbrk1r/ppppp1pp/5p2/3b4/3B4/5P2/PPPPP1PP/QNNBRK1R w KQkq - 2 3");

        Move target = Move.fromUCI("f1h1", Move.CASTLE_FLAG);
        assertMove(board, target, true);
        board.makeMove(target);
        assertKingAndRook(board, "f1", "g1", "h1", "f1");

        board.unmakeMove();
        assertKingAndRook(board, "g1", "f1", "f1", "h1");

    }

    @Test
    public void testRookGoesToKingSquareBlocked() {

        Board board = Board.from("qnnbrkbr/pppppppp/8/8/8/8/PPPPPPPP/QNNBRKBR w KQkq - 0 1");

        Move target = Move.fromUCI("f1h1", Move.CASTLE_FLAG);
        assertMove(board, target, false);

    }

    @Test
    public void testQueensideRookOnKingside() {

        Board board = Board.from("qn2rkbr/ppbppppp/1np5/8/8/1NP5/PPBPPPPP/QN2RKBR w KQkq - 2 4");

        Move target = Move.fromUCI("f1e1", Move.CASTLE_FLAG);
        assertMove(board, target, true);
        board.makeMove(target);
        assertKingAndRook(board, "f1", "c1", "e1", "d1");
        board.unmakeMove();
        assertKingAndRook(board, "c1", "f1", "d1", "e1");

    }

    @Test
    public void testQueensideRookOnKingsideTwo() {

        Board board = Board.from("bbqnr1nQ/1ppppp1p/8/p7/5k2/PP2N3/2PPPP1P/1B2RKNR w KQ - 2 8");

        Move target = Move.fromUCI("f1e1", Move.CASTLE_FLAG);
        assertMove(board, target, true);
        board.makeMove(target);
        assertKingAndRook(board, "f1", "c1", "e1", "d1");
        board.unmakeMove();
        assertKingAndRook(board, "c1", "f1", "d1", "e1");

    }

    @Test
    public void testQueensideRookOnKingsideCastleKingside() {

        Board board = Board.from("bnqbrk1r/pppppppp/5n2/8/8/5N2/PPPPPPPP/BNQBRK1R w KQkq - 2 2");

        Move target = Move.fromUCI("f1h1", Move.CASTLE_FLAG);
        assertMove(board, target, true);
        board.makeMove(target);
        assertKingAndRook(board, "f1", "g1", "h1", "f1");
        board.unmakeMove();
        assertKingAndRook(board, "g1", "f1", "f1", "h1");

    }

    @Test
    public void testQueensideRookOnKingsideBlocked() {

        Board board = Board.from("nbb1rkrn/pp1ppppp/1qp5/8/8/1QP5/PP1PPPPP/NBB1RKRN w KQkq - 2 3");

        Move target = Move.fromUCI("f1e1", Move.CASTLE_FLAG);
        assertMove(board, target, false);

    }

    @Test
    public void testKingsideRookOnQueenside() {

        Board board = Board.from("nbbqr2n/ppp2kr1/3ppppp/8/8/3PPPN1/PPPBBQPP/NRKR4 w KQ - 2 8");

        Move target = Move.fromUCI("c1d1", Move.CASTLE_FLAG);
        assertMove(board, target, true);
        board.makeMove(target);
        assertKingAndRook(board, "c1", "g1", "d1", "f1");
        board.unmakeMove();
        assertKingAndRook(board, "g1", "c1", "f1", "d1");
    }

    @Test
    public void testKingsideRookOnQueensideBlocked() {

        Board board = Board.from("nbbqr1rn/ppp2k2/3ppppp/8/8/3PPPN1/PPPBB1PP/NRKR2Q1 w KQ - 0 7");

        Move target = Move.fromUCI("c1d1", Move.CASTLE_FLAG);
        assertMove(board, target, false);
    }

    @Test
    public void testDontGetConfusedBetweenKingsideQueenside() {

        Board board = Board.from("bqnbrk1r/pppppppp/5n2/8/8/5N2/PPPPPPPP/BQNBRK1R w KQkq - 2 2");
        Move kingside = Move.fromUCI("f1h1", Move.CASTLE_FLAG);
        Move queenside = Move.fromUCI("f1e1", Move.CASTLE_FLAG);
        assertMove(board, kingside, true);
        assertMove(board, queenside, false);

    }
    
	@Test
	void testKingDoesntMoveCastling() {
		// Rook is attacked, but the castling is legal
		final Board board = Board.from("nrk2rnb/pp1ppppp/6b1/q1p5/3P2Q1/1N3N2/1P2PPPP/1RK1BR1B w KQkq - 2 10");
		final Move move = Move.fromUCI("c1b1", Move.CASTLE_FLAG);
		assertMove(board, move, true);
	}

	@Test
	void testPinnedRookCastling() {
		// Test castling where king seems safe ... but is not because it does not move and the rook does not defend it anymore
		final Board board = Board.from("nrk1brnb/pp1ppppp/8/2p5/3P4/1N1Q1N2/1PP1PPPP/qRK1BR1B w KQkq - 2 10");
		final Move move = Move.fromUCI("c1b1", Move.CASTLE_FLAG);
		assertMove(board, move, false);
	}

    private void assertMove(Board board, Move move, boolean exists) {
        List<Move> moves = MOVEGEN.generateMoves(board);
        Assertions.assertEquals(exists, moves.stream().anyMatch(m -> m.equals(move)));
		Assertions.assertEquals(exists, MOVEGEN.isLegal(board, move));
    }

    private void assertKingAndRook(Board board, String kingFrom, String kingTo, String rookFrom, String rookTo) {

        int kingFromSq = Square.fromNotation(kingFrom);
        int kingToSq = Square.fromNotation(kingTo);
        int rookFromSq = Square.fromNotation(rookFrom);
        int rookToSq = Square.fromNotation(rookTo);

        if (kingFromSq != rookToSq) {
            Assertions.assertNull(board.pieceAt(kingFromSq));
        }
        Assertions.assertEquals(Piece.KING, board.pieceAt(kingToSq));

        if (rookFromSq != kingToSq) {
            Assertions.assertNull(board.pieceAt(rookFromSq));
        }
        Assertions.assertEquals(Piece.ROOK, board.pieceAt(rookToSq));

        Assertions.assertFalse(Bits.contains(board.getKing(true), kingFromSq));
        Assertions.assertFalse(Bits.contains(board.getRooks(true), rookFromSq));
        Assertions.assertTrue(Bits.contains(board.getKing(true), kingToSq));
        Assertions.assertTrue(Bits.contains(board.getRooks(true), rookToSq));

    }

}
