package com.kelseyde.calvin.board;

import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Stream;

public class KeyTest {

    @Test
    public void testSamePositionGeneratesSameKey() {

        Board board1 = FEN.startpos().toBoard();
        Board board2 = FEN.startpos().toBoard();
        Assertions.assertEquals(board1.getState().getKey(), board2.getState().getKey());

        Move e4 = new Move(12, 28, Move.PAWN_DOUBLE_MOVE_FLAG);
        board1.makeMove(e4);
        board2.makeMove(e4);

        Assertions.assertEquals(board1.getState().getKey(), board2.getState().getKey());

        board1.unmakeMove();
        board2.unmakeMove();

        Assertions.assertEquals(board1.getState().getKey(), board2.getState().getKey());
        Assertions.assertEquals(board1.getState().getKey(), FEN.startpos().toBoard().getState().getKey());

    }

    @Test
    public void testSamePositionEndgameGeneratesSameKey() {

        String fen = "k6K/1pp2P1P/p1p5/P7/8/8/5r2/2R5 w - - 1 51";

        Board board = FEN.parse(fen).toBoard();
        long firstZobrist1 = board.getState().getKey();

        board.makeMove(Move.fromUCI("h8g8"));
        long secondZobrist1 = board.getState().getKey();

        board.makeMove(Move.fromUCI("f2g2"));
        board.makeMove(Move.fromUCI("g8h8"));
        board.makeMove(Move.fromUCI("g2f2"));
        long firstZobrist2 = board.getState().getKey();

        board.makeMove(Move.fromUCI("h8g8"));
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

        Board board = FEN.parse(fen).toBoard();
        long z1 = board.getState().getKey();
        board.makeMove(Move.fromUCI("b8b3"));
        long z2 = board.getState().getKey();
        board.makeMove(Move.fromUCI("c3b3"));
        long z3 = board.getState().getKey();
        board.makeMove(Move.fromUCI("c7a5"));
        long z4 = board.getState().getKey();
        board.makeMove(Move.fromUCI("b3c3"));
        long z5 = board.getState().getKey();
        board.makeMove(Move.fromUCI("a5c7"));
        long z6 = board.getState().getKey();

        long distinctZobristCount = Stream.of(z1, z2, z3, z4, z5, z6)
                .distinct()
                .count();
        Assertions.assertEquals(6, distinctZobristCount);

    }

    @Test
    public void testPawnDoubleMove() {

        String fenBeforeMove = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        String fenAfterMove = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1";

        Board board1 = FEN.parse(fenBeforeMove).toBoard();
        board1.makeMove(Move.fromUCI("e2e4"));
        long zobrist1 = board1.getState().getKey();

        Board board2 = FEN.parse(fenAfterMove).toBoard();
        long zobrist2 = board2.getState().getKey();

        Assertions.assertEquals(zobrist1, zobrist2);

    }

    @Test
    public void testCapture() {

        String fenBeforeMove = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2";
        String fenAfterMove = "rnbqkbnr/ppp1pppp/8/3P4/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 2";

        Board board1 = FEN.parse(fenBeforeMove).toBoard();
        board1.makeMove(Move.fromUCI("e4d5"));
        long zobrist1 = board1.getState().getKey();

        Board board2 = FEN.parse(fenAfterMove).toBoard();
        long zobrist2 = board2.getState().getKey();

        Assertions.assertEquals(zobrist1, zobrist2);

    }

    @Test
    public void testCastlingRights() {

        String fenBeforeMove = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2";
        String fenAfterMove = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPPKPPP/RNBQ1BNR b kq - 1 2";

        Board board1 = FEN.parse(fenBeforeMove).toBoard();
        board1.makeMove(Move.fromUCI("e1e2"));
        long zobrist1 = board1.getState().getKey();

        Board board2 = FEN.parse(fenAfterMove).toBoard();
        long zobrist2 = board2.getState().getKey();

        Assertions.assertEquals(zobrist1, zobrist2);

    }

    @Test
    public void testEnPassant() {

        String fenBeforeCapture = "rnbqkb1r/ppp1pppp/5n2/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3";
        String fenAfterCapture = "rnbqkb1r/ppp1pppp/3P1n2/8/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3";

        Board board1 = FEN.parse(fenBeforeCapture).toBoard();
        board1.makeMove(Move.fromUCI("e5d6", Move.EN_PASSANT_FLAG));
        long zobrist1 = board1.getState().getKey();

        Board board2 = FEN.parse(fenAfterCapture).toBoard();
        long zobrist2 = board2.getState().getKey();

        Assertions.assertEquals(zobrist1, zobrist2);
    }

    @Test
    public void testCastling() {

        String fenBeforeCastle = "r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4";
        String fenAfterCastle = "r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQ1RK1 b kq - 5 4";

        Board board1 = FEN.parse(fenBeforeCastle).toBoard();
        board1.makeMove(Move.fromUCI("e1g1", Move.CASTLE_FLAG));
        long zobrist1 = board1.getState().getKey();

        Board board2 = FEN.parse(fenAfterCastle).toBoard();
        long zobrist2 = board2.getState().getKey();

        Assertions.assertEquals(zobrist1, zobrist2);

    }

    @Test
    public void testPromotion() {

        String fenBeforeCastle = "rnbqkb1r/pP3ppp/5n2/4p3/8/8/PPPP1PPP/RNBQKBNR w - - 0 7";
        String fenAfterCastle = "Qnbqkb1r/p4ppp/5n2/4p3/8/8/PPPP1PPP/RNBQKBNR b - - 0 7";

        Board board1 = FEN.parse(fenBeforeCastle).toBoard();
        board1.makeMove(Move.fromUCI("b7a8", Move.PROMOTE_TO_QUEEN_FLAG));
        long zobrist1 = board1.getState().getKey();

        Board board2 = FEN.parse(fenAfterCastle).toBoard();
        long zobrist2 = board2.getState().getKey();

        Assertions.assertEquals(zobrist1, zobrist2);

    }

    @Test
    public void testPawnZobristPromotion() {

        String fenBeforePromotion = "rnb1kb1r/pP2pppp/5n2/8/8/3q4/PPPP1PPP/RNBQKBNR w KQkq - 1 5";
        String fenAfterPromotion = "Qnb1kb1r/p3pppp/5n2/8/8/3q4/PPPP1PPP/RNBQKBNR b KQk - 0 5";

        Board board1 = FEN.parse(fenBeforePromotion).toBoard();
        board1.makeMove(Move.fromUCI("b7a8q", Move.PROMOTE_TO_QUEEN_FLAG));
        long zobrist1 = board1.getState().getPawnKey();

        Board board2 = FEN.parse(fenAfterPromotion).toBoard();
        long zobrist2 = board2.getState().getPawnKey();

        Assertions.assertEquals(zobrist1, zobrist2);

    }

    @Test
    public void testNonPawnNotUpdatedOnPawnMove() {

        Board board = FEN.startpos().toBoard();
        long[] keys = board.nonPawnKeys();

        board.makeMove(Move.fromUCI("e2e4"));
        long[] newKeys = board.nonPawnKeys();
        Assertions.assertArrayEquals(keys, newKeys);

        board.makeMove(Move.fromUCI("e7e5", Move.PAWN_DOUBLE_MOVE_FLAG));
        newKeys = board.nonPawnKeys();
        Assertions.assertArrayEquals(keys, newKeys);
        Assertions.assertArrayEquals(newKeys, Key.generateNonPawnKeys(board));

    }

    @Test
    public void testNonPawnCorrectSideUpdatedWhite() {

        Board board = FEN.startpos().toBoard();
        long[] keys = Arrays.copyOf(board.nonPawnKeys(), board.nonPawnKeys().length);

        board.makeMove(Move.fromUCI("g1f3"));
        long[] newKeys = board.nonPawnKeys();

        Assertions.assertEquals(keys[1], newKeys[1]);
        Assertions.assertNotEquals(keys[0], newKeys[0]);
        Assertions.assertArrayEquals(newKeys, Key.generateNonPawnKeys(board));

    }

    @Test
    public void testNonPawnCorrectSideUpdatedBlack() {

        Board board = FEN.startpos().toBoard();
        long[] keys = Arrays.copyOf(board.nonPawnKeys(), board.nonPawnKeys().length);

        board.makeMove(Move.fromUCI("e2e4", Move.PAWN_DOUBLE_MOVE_FLAG));
        board.makeMove(Move.fromUCI("g8f6"));
        long[] newKeys = board.nonPawnKeys();

        Assertions.assertEquals(keys[0], newKeys[0]);
        Assertions.assertNotEquals(keys[1], newKeys[1]);
        Assertions.assertArrayEquals(newKeys, Key.generateNonPawnKeys(board));

    }

    @Test
    public void testNonPawnCastling() {

        Board board = Board.from("rnbqk2r/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4");
        long[] keys = Arrays.copyOf(board.nonPawnKeys(), board.nonPawnKeys().length);

        board.makeMove(Move.fromUCI("e1g1", Move.CASTLE_FLAG));
        long[] newKeys = board.nonPawnKeys();

        Assertions.assertNotEquals(keys[0], newKeys[0]);
        Assertions.assertEquals(keys[1], newKeys[1]);
        Assertions.assertArrayEquals(newKeys, Key.generateNonPawnKeys(board));

    }

    @Test
    public void testNonPawnCapturePawn() {

        Board board = Board.from("rnbqkb1r/pppp1ppp/5n2/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3");
        long[] keys = Arrays.copyOf(board.nonPawnKeys(), board.nonPawnKeys().length);

        board.makeMove(Move.fromUCI("f3e5"));
        long[] newKeys = board.nonPawnKeys();

        Assertions.assertNotEquals(keys[0], newKeys[0]);
        Assertions.assertEquals(keys[1], newKeys[1]);
        Assertions.assertArrayEquals(newKeys, Key.generateNonPawnKeys(board));

    }

    @Test
    public void testNonPawnCaptureNonPawn() {

        Board board = Board.from("r1bqkbnr/pppppppp/2n5/4N3/8/8/PPPPPPPP/RNBQKB1R b KQkq - 3 2");
        long[] keys = Arrays.copyOf(board.nonPawnKeys(), board.nonPawnKeys().length);

        board.makeMove(Move.fromUCI("c6e5"));
        long[] newKeys = board.nonPawnKeys();

        Assertions.assertNotEquals(keys[1], newKeys[1]);
        Assertions.assertNotEquals(keys[0], newKeys[0]);
        Assertions.assertArrayEquals(newKeys, Key.generateNonPawnKeys(board));

    }

    @Test
    public void testNormalPromotion() {

        Board board = Board.from("r2qkbnr/pP1npppp/8/5b2/8/8/PPPP1PPP/RNBQKBNR w KQkq - 1 5");
        long[] keys = Arrays.copyOf(board.nonPawnKeys(), board.nonPawnKeys().length);

        board.makeMove(Move.fromUCI("b7b8q", Move.PROMOTE_TO_QUEEN_FLAG));
        long[] newKeys = board.nonPawnKeys();

        Assertions.assertNotEquals(keys[0], newKeys[0]);
        Assertions.assertEquals(keys[1], newKeys[1]);
        Assertions.assertArrayEquals(newKeys, Key.generateNonPawnKeys(board));

    }

    @Test
    public void testCapturePromotion() {

        Board board = Board.from("r2qkbnr/pP1npppp/8/5b2/8/8/PPPP1PPP/RNBQKBNR w KQkq - 1 5");
        long[] keys = Arrays.copyOf(board.nonPawnKeys(), board.nonPawnKeys().length);

        board.makeMove(Move.fromUCI("b7a8q", Move.PROMOTE_TO_QUEEN_FLAG));
        long[] newKeys = board.nonPawnKeys();

        Assertions.assertNotEquals(keys[0], newKeys[0]);
        Assertions.assertNotEquals(keys[1], newKeys[1]);
        Assertions.assertArrayEquals(newKeys, Key.generateNonPawnKeys(board));

    }

}