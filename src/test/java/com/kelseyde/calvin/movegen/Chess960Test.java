package com.kelseyde.calvin.movegen;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Chess960Test {

    private static final List<String> FENS = List.of(
//            "nrkqrbbn/pppppppp/8/8/8/8/PPPPPPPP/NRKQRBBN w EBeb - 0 1"
//            "rkrnqnbb/pppppppp/8/8/8/8/PPPPPPPP/RKRNQNBB w CAca - 0 1"
//            "qrkbbnrn/pppppppp/8/8/8/8/PPPPPPPP/QRKBBNRN w GBgb - 0 1"
            "qnnbrkbr/pppppppp/8/8/8/8/PPPPPPPP/QNNBRKBR w KQkq - 0 1"
//            "nrbnkbqr/pppppppp/8/8/8/8/PPPPPPPP/NRBNKBQR w KQkq - 0 1"
    );

    private static final Searcher SEARCHER = TestUtils.SEARCHER;
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
    public void testFens() {

        UCI.Options.chess960 = true;

        for (String fen : FENS) {

            SEARCHER.clearHistory();
            SEARCHER.setPosition(Board.from(fen));
            SEARCHER.search(20, 0, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1);

        }

        UCI.Options.chess960 = false;


    }

    @Test
    public void badCastling() {

        String fen = "q2brkb1/pppnnpp1/3p3r/4p2p/4PP1P/2N4R/PPPP2PB/Q1NBRK2 w Qq - 1 7";
        Board board = Board.from(fen);
        List<Move> moves = MOVEGEN.generateMoves(board);
        System.out.println(moves.stream().map(Move::toUCI).toList());
        Assertions.assertFalse(moves.stream().anyMatch(m -> Move.toUCI(m).equals("f1e1")));

    }

    @Test
    public void badCastlingDebug() {

        Board board = Board.from("qnnbrkbr/pppppppp/8/8/8/8/PPPPPPPP/QNNBRKBR w KQkq - 0 1");
        board.makeMove(Move.fromUCI("h2h4"));
        board.makeMove(Move.fromUCI("h7h5"));
        board.makeMove(Move.fromUCI("h1h3"));
        board.makeMove(Move.fromUCI("e7e5"));
        board.makeMove(Move.fromUCI("e2e4"));
        board.makeMove(Move.fromUCI("h8h6"));
        board.makeMove(Move.fromUCI("b1c3"));
        board.makeMove(Move.fromUCI("c8e7"));
        board.makeMove(Move.fromUCI("g1h2"));
        board.makeMove(Move.fromUCI("d7d6"));
        board.makeMove(Move.fromUCI("f2f4"));
        board.makeMove(Move.fromUCI("b8d7"));
        List<Move> moves = MOVEGEN.generateMoves(board);
        System.out.println(moves.stream().map(Move::toUCI).toList());
        Assertions.assertFalse(moves.stream().anyMatch(m -> Move.toUCI(m).equals("f1e1")));

    }

    @Test
    public void testRookGoesToKingSquare() {

        Board board = Board.from("qnnbrk1r/ppppp1pp/5p2/3b4/3B4/5P2/PPPPP1PP/QNNBRK1R w KQkq - 2 3");

        Move target = Move.fromUCI("f1h1", Move.CASTLE_FLAG);
        assertMove(board, target, true);
        board.makeMove(target);
        assertKingAndRook(board, "f1", "g1", "h1", "f1", true);

        board.unmakeMove();
        assertKingAndRook(board, "g1", "f1", "f1", "h1", true);

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
        board.print();
        assertKingAndRook(board, "f1", "c1", "e1", "d1", true);

    }

    private void assertMove(Board board, Move move, boolean exists) {
        List<Move> moves = MOVEGEN.generateMoves(board);
        System.out.println(moves.stream().map(Move::toUCI).toList());
        Assertions.assertEquals(exists, moves.stream().anyMatch(m -> m.equals(move)));
    }

    private void assertKingAndRook(Board board, String kingFrom, String kingTo, String rookFrom, String rookTo, boolean white) {

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

        Assertions.assertFalse(Bits.contains(board.getKing(white), kingFromSq));
        Assertions.assertFalse(Bits.contains(board.getRooks(white), rookFromSq));
        Assertions.assertTrue(Bits.contains(board.getKing(white), kingToSq));
        Assertions.assertTrue(Bits.contains(board.getRooks(white), rookToSq));

    }

}
