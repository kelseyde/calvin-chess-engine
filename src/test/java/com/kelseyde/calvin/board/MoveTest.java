package com.kelseyde.calvin.board;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MoveTest {

    @Test
    public void testStartSquareAndEndSquare() {

        Move move = Move.of(2, 4);
        Assertions.assertEquals(2, move.getStartSquare());
        Assertions.assertEquals(4, move.getEndSquare());

        move = Move.of(63, 1);
        Assertions.assertEquals(63, move.getStartSquare());
        Assertions.assertEquals(1, move.getEndSquare());

        move = Move.of(0, 17);
        Assertions.assertEquals(0, move.getStartSquare());
        Assertions.assertEquals(17, move.getEndSquare());

        move = Move.of(24, 47);
        Assertions.assertEquals(24, move.getStartSquare());
        Assertions.assertEquals(47, move.getEndSquare());

    }

    @Test
    public void testPromotionPieceType() {

        Move move = Move.of(45, 63, Move.PROMOTE_TO_QUEEN_FLAG);
        Assertions.assertEquals(Piece.QUEEN, move.getPromotionPieceType());
        Assertions.assertTrue(move.isPromotion());
        Assertions.assertFalse(move.isCastling());
        Assertions.assertFalse(move.isPawnDoubleMove());
        Assertions.assertFalse(move.isEnPassant());

        move = Move.of(45, 63, Move.PROMOTE_TO_ROOK_FLAG);
        Assertions.assertEquals(Piece.ROOK, move.getPromotionPieceType());
        Assertions.assertTrue(move.isPromotion());
        Assertions.assertFalse(move.isCastling());
        Assertions.assertFalse(move.isPawnDoubleMove());
        Assertions.assertFalse(move.isEnPassant());

        move = Move.of(45, 63, Move.PROMOTE_TO_BISHOP_FLAG);
        Assertions.assertEquals(Piece.BISHOP, move.getPromotionPieceType());
        Assertions.assertTrue(move.isPromotion());
        Assertions.assertFalse(move.isCastling());
        Assertions.assertFalse(move.isPawnDoubleMove());
        Assertions.assertFalse(move.isEnPassant());

        move = Move.of(45, 63, Move.PROMOTE_TO_KNIGHT_FLAG);
        Assertions.assertEquals(Piece.KNIGHT, move.getPromotionPieceType());
        Assertions.assertTrue(move.isPromotion());
        Assertions.assertFalse(move.isCastling());
        Assertions.assertFalse(move.isPawnDoubleMove());
        Assertions.assertFalse(move.isEnPassant());

    }

    @Test
    public void testEnPassant() {
        Move move = Move.of(45, 63, Move.EN_PASSANT_FLAG);
        Assertions.assertTrue(move.isEnPassant());
        Assertions.assertFalse(move.isCastling());
        Assertions.assertFalse(move.isPawnDoubleMove());
        Assertions.assertFalse(move.isPromotion());
    }

    @Test
    public void testPawnDoubleMove() {
        Move move = Move.of(45, 63, Move.PAWN_DOUBLE_MOVE_FLAG);
        Assertions.assertFalse(move.isEnPassant());
        Assertions.assertFalse(move.isCastling());
        Assertions.assertTrue(move.isPawnDoubleMove());
        Assertions.assertFalse(move.isPromotion());
    }

    @Test
    public void testCastling() {
        Move move = Move.of(45, 63, Move.CASTLE_FLAG);
        Assertions.assertFalse(move.isEnPassant());
        Assertions.assertTrue(move.isCastling());
        Assertions.assertFalse(move.isPawnDoubleMove());
        Assertions.assertFalse(move.isPromotion());
    }

}
