package com.kelseyde.calvin.board;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MoveTest {

    @Test
    public void testStartSquareAndEndSquare() {

        Move move = new Move(2, 4);
        Assertions.assertEquals(2, move.getStartSquare());
        Assertions.assertEquals(4, move.getEndSquare());

        move = new Move(63, 1);
        Assertions.assertEquals(63, move.getStartSquare());
        Assertions.assertEquals(1, move.getEndSquare());

        move = new Move(0, 17);
        Assertions.assertEquals(0, move.getStartSquare());
        Assertions.assertEquals(17, move.getEndSquare());

        move = new Move(24, 47);
        Assertions.assertEquals(24, move.getStartSquare());
        Assertions.assertEquals(47, move.getEndSquare());

    }

    @Test
    public void testPromotionPieceType() {

        Move move = new Move(45, 63, Move.PROMOTE_TO_QUEEN_FLAG);
        Assertions.assertEquals(PieceType.QUEEN, move.getPromotionPieceType());
        Assertions.assertTrue(move.isPromotion());
        Assertions.assertFalse(move.isCastling());
        Assertions.assertFalse(move.isPawnDoubleMove());
        Assertions.assertFalse(move.isEnPassant());

        move = new Move(45, 63, Move.PROMOTE_TO_ROOK_FLAG);
        Assertions.assertEquals(PieceType.ROOK, move.getPromotionPieceType());
        Assertions.assertTrue(move.isPromotion());
        Assertions.assertFalse(move.isCastling());
        Assertions.assertFalse(move.isPawnDoubleMove());
        Assertions.assertFalse(move.isEnPassant());

        move = new Move(45, 63, Move.PROMOTE_TO_BISHOP_FLAG);
        Assertions.assertEquals(PieceType.BISHOP, move.getPromotionPieceType());
        Assertions.assertTrue(move.isPromotion());
        Assertions.assertFalse(move.isCastling());
        Assertions.assertFalse(move.isPawnDoubleMove());
        Assertions.assertFalse(move.isEnPassant());

        move = new Move(45, 63, Move.PROMOTE_TO_KNIGHT_FLAG);
        Assertions.assertEquals(PieceType.KNIGHT, move.getPromotionPieceType());
        Assertions.assertTrue(move.isPromotion());
        Assertions.assertFalse(move.isCastling());
        Assertions.assertFalse(move.isPawnDoubleMove());
        Assertions.assertFalse(move.isEnPassant());

    }

    @Test
    public void testEnPassant() {
        Move move = new Move(45, 63, Move.EN_PASSANT_FLAG);
        Assertions.assertTrue(move.isEnPassant());
        Assertions.assertFalse(move.isCastling());
        Assertions.assertFalse(move.isPawnDoubleMove());
        Assertions.assertFalse(move.isPromotion());
    }

    @Test
    public void testPawnDoubleMove() {
        Move move = new Move(45, 63, Move.PAWN_DOUBLE_MOVE_FLAG);
        Assertions.assertFalse(move.isEnPassant());
        Assertions.assertFalse(move.isCastling());
        Assertions.assertTrue(move.isPawnDoubleMove());
        Assertions.assertFalse(move.isPromotion());
    }

    @Test
    public void testCastling() {
        Move move = new Move(45, 63, Move.CASTLE_FLAG);
        Assertions.assertFalse(move.isEnPassant());
        Assertions.assertTrue(move.isCastling());
        Assertions.assertFalse(move.isPawnDoubleMove());
        Assertions.assertFalse(move.isPromotion());
    }

    @Test
    public void testEquality() {
        Move m1 = new Move(2, 4);
        Move m2 = new Move(60, 62);
        Move m3 = new Move(7, 57, Move.PAWN_DOUBLE_MOVE_FLAG);
        Move m4 = new Move(24, 34, Move.EN_PASSANT_FLAG);
        Move m5 = new Move(11, 38, Move.PROMOTE_TO_QUEEN_FLAG);

        Assertions.assertNotEquals(m1, m2);
        Assertions.assertNotEquals(m1, m3);
        Assertions.assertNotEquals(m1, m4);
        Assertions.assertNotEquals(m1, m5);
        Assertions.assertNotEquals(m2, m3);
        Assertions.assertNotEquals(m2, m4);
        Assertions.assertNotEquals(m2, m5);
        Assertions.assertNotEquals(m4, m3);
        Assertions.assertNotEquals(m4, m5);

        Assertions.assertEquals(m1, m1);
        Assertions.assertEquals(m2, m2);
        Assertions.assertEquals(m3, m3);
        Assertions.assertEquals(m4, m4);
        Assertions.assertEquals(m5, m5);
    }

}
