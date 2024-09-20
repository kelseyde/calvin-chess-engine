package com.kelseyde.calvin.board;

import com.kelseyde.calvin.utils.FEN;
import com.kelseyde.calvin.utils.Notation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

public class ZobristTest {

    @Test
    public void testSamePositionGeneratesSameKey() {

        Board board1 = new Board();
        Board board2 = new Board();
        Assertions.assertEquals(board1.getState().getKey(), board2.getState().getKey());

        Move e4 = new Move(12, 28, Move.PAWN_DOUBLE_MOVE_FLAG);
        board1.makeMove(e4);
        board2.makeMove(e4);

        Assertions.assertEquals(board1.getState().getKey(), board2.getState().getKey());

        board1.unmakeMove();
        board2.unmakeMove();

        Assertions.assertEquals(board1.getState().getKey(), board2.getState().getKey());
        Assertions.assertEquals(board1.getState().getKey(), new Board().getState().getKey());

    }

    @Test
    public void testSamePositionEndgameGeneratesSameKey() {

        String fen = "k6K/1pp2P1P/p1p5/P7/8/8/5r2/2R5 w - - 1 51";

        Board board = FEN.toBoard(fen);
        long firstZobrist1 = board.getState().getKey();

        board.makeMove(Notation.fromNotation("h8", "g8"));
        long secondZobrist1 = board.getState().getKey();

        board.makeMove(Notation.fromNotation("f2", "g2"));
        board.makeMove(Notation.fromNotation("g8", "h8"));
        board.makeMove(Notation.fromNotation("g2", "f2"));
        long firstZobrist2 = board.getState().getKey();

        board.makeMove(Notation.fromNotation("h8", "g8"));
        long secondZobrist2 = board.getState().getKey();

        Assertions.assertEquals(firstZobrist1, firstZobrist2);
        Assertions.assertEquals(secondZobrist1, secondZobrist2);
        Assertions.assertNotEquals(firstZobrist1, secondZobrist1);
        Assertions.assertNotEquals(firstZobrist1, secondZobrist2);
        Assertions.assertNotEquals(firstZobrist2, secondZobrist1);
        Assertions.assertNotEquals(firstZobrist2, secondZobrist2);

    }

    @Test
    public void testCapturedPieceChangesZobrist() {

        String fen = "1rb3k1/p1q3pp/4pr2/5p2/2pP4/1PQ3P1/4PPBP/2R1K2R b K - 0 21";

        Board board = FEN.toBoard(fen);
        long z1 = board.getState().getKey();
        board.makeMove(Notation.fromNotation("b8", "b3"));
        long z2 = board.getState().getKey();
        board.makeMove(Notation.fromNotation("c3", "b3"));
        long z3 = board.getState().getKey();
        board.makeMove(Notation.fromNotation("c7", "a5"));
        long z4 = board.getState().getKey();
        board.makeMove(Notation.fromNotation("b3", "c3"));
        long z5 = board.getState().getKey();
        board.makeMove(Notation.fromNotation("a5", "c7"));
        long z6 = board.getState().getKey();
        System.out.println(z1);
        System.out.println(z2);
        System.out.println(z3);
        System.out.println(z4);
        System.out.println(z5);
        System.out.println(z6);

        long distinctZobristCount = Stream.of(z1, z2, z3, z4, z5, z6)
                .distinct()
                .count();
        Assertions.assertEquals(6, distinctZobristCount);

    }

    @Test
    public void testPawnDoubleMove() {

        String fenBeforeMove = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        String fenAfterMove = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1";

        Board board1 = FEN.toBoard(fenBeforeMove);
        board1.makeMove(Notation.fromNotation("e2", "e4"));
        long zobrist1 = board1.getState().getKey();

        Board board2 = FEN.toBoard(fenAfterMove);
        long zobrist2 = board2.getState().getKey();

        Assertions.assertEquals(zobrist1, zobrist2);

    }

    @Test
    public void testCapture() {

        String fenBeforeMove = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2";
        String fenAfterMove = "rnbqkbnr/ppp1pppp/8/3P4/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 2";

        Board board1 = FEN.toBoard(fenBeforeMove);
        board1.makeMove(Notation.fromNotation("e4", "d5"));
        long zobrist1 = board1.getState().getKey();

        Board board2 = FEN.toBoard(fenAfterMove);
        long zobrist2 = board2.getState().getKey();

        Assertions.assertEquals(zobrist1, zobrist2);

    }

    @Test
    public void testCastlingRights() {

        String fenBeforeMove = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2";
        String fenAfterMove = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPPKPPP/RNBQ1BNR b kq - 1 2";

        Board board1 = FEN.toBoard(fenBeforeMove);
        board1.makeMove(Notation.fromNotation("e1", "e2"));
        long zobrist1 = board1.getState().getKey();

        Board board2 = FEN.toBoard(fenAfterMove);
        long zobrist2 = board2.getState().getKey();

        Assertions.assertEquals(zobrist1, zobrist2);

    }

    @Test
    public void testEnPassant() {

        String fenBeforeCapture = "rnbqkb1r/ppp1pppp/5n2/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3";
        String fenAfterCapture = "rnbqkb1r/ppp1pppp/3P1n2/8/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3";

        Board board1 = FEN.toBoard(fenBeforeCapture);
        board1.makeMove(Notation.fromNotation("e5", "d6", Move.EN_PASSANT_FLAG));
        long zobrist1 = board1.getState().getKey();

        Board board2 = FEN.toBoard(fenAfterCapture);
        long zobrist2 = board2.getState().getKey();

        Assertions.assertEquals(zobrist1, zobrist2);
    }

    @Test
    public void testCastling() {

        String fenBeforeCastle = "r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4";
        String fenAfterCastle = "r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQ1RK1 b kq - 5 4";

        Board board1 = FEN.toBoard(fenBeforeCastle);
        board1.makeMove(Notation.fromNotation("e1", "g1", Move.CASTLE_FLAG));
        long zobrist1 = board1.getState().getKey();

        Board board2 = FEN.toBoard(fenAfterCastle);
        long zobrist2 = board2.getState().getKey();

        Assertions.assertEquals(zobrist1, zobrist2);

    }

    @Test
    public void testPromotion() {

        String fenBeforeCastle = "rnbqkb1r/pP3ppp/5n2/4p3/8/8/PPPP1PPP/RNBQKBNR w - - 0 7";
        String fenAfterCastle = "Qnbqkb1r/p4ppp/5n2/4p3/8/8/PPPP1PPP/RNBQKBNR b - - 0 7";

        Board board1 = FEN.toBoard(fenBeforeCastle);
        board1.makeMove(Notation.fromNotation("b7", "a8", Move.PROMOTE_TO_QUEEN_FLAG));
        long zobrist1 = board1.getState().getKey();

        Board board2 = FEN.toBoard(fenAfterCastle);
        long zobrist2 = board2.getState().getKey();

        Assertions.assertEquals(zobrist1, zobrist2);

    }

    @Test
    public void testPawnZobristPromotion() {

        String fenBeforePromotion = "rnb1kb1r/pP2pppp/5n2/8/8/3q4/PPPP1PPP/RNBQKBNR w KQkq - 1 5";
        String fenAfterPromotion = "Qnb1kb1r/p3pppp/5n2/8/8/3q4/PPPP1PPP/RNBQKBNR b KQk - 0 5";

        Board board1 = FEN.toBoard(fenBeforePromotion);
        board1.makeMove(Notation.fromNotation("b7", "a8", Move.PROMOTE_TO_QUEEN_FLAG));
        long zobrist1 = board1.getState().getPawnKey();

        Board board2 = FEN.toBoard(fenAfterPromotion);
        long zobrist2 = board2.getState().getPawnKey();

        Assertions.assertEquals(zobrist1, zobrist2);

    }

}