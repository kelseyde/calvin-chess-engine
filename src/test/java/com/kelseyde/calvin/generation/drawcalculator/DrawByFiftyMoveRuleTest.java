package com.kelseyde.calvin.generation.drawcalculator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.evaluation.Result;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrawByFiftyMoveRuleTest {

    @Test
    public void testDrawByFiftyMovesSinceWhiteCapture() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(Piece.KING, false, 0);
        board.toggleSquare(Piece.KNIGHT, false, 28);
        board.toggleSquare(Piece.KNIGHT, false, 36);
        board.toggleSquare(Piece.QUEEN, false, 35);

        board.toggleSquare(Piece.KING, true, 63);
        board.toggleSquare(Piece.KNIGHT, true, 18);
        board.setPieceList(BoardUtils.calculatePieceList(board));

        board.setWhiteToMove(true);

        // black knight captures white queen
        board.makeMove(TestUtils.getLegalMove(board, "c3", "d5"));

        // kings walk about until 50 move rule reached
        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));
        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));

        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));
        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));

        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));
        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));

        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));
        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));

        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));
        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));

        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));
        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));

        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));
        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));

        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));
        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));

        board.makeMove(TestUtils.getLegalMove(board, "b8", "c8"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f1"));

        board.makeMove(TestUtils.getLegalMove(board, "c8", "d8"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));

        board.makeMove(TestUtils.getLegalMove(board, "d8", "e8"));
        board.makeMove(TestUtils.getLegalMove(board, "e1", "d1"));

        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "c1"));

        board.makeMove(TestUtils.getLegalMove(board, "f8", "g8"));
        board.makeMove(TestUtils.getLegalMove(board, "c1", "b1"));

        board.makeMove(TestUtils.getLegalMove(board, "g8", "h8"));
        board.makeMove(TestUtils.getLegalMove(board, "b1", "a1"));

        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));
        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));

        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));

        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));
        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));

        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));
        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));

        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));
        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));

        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));
        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));

        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));
        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));

        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));
        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));

        board.makeMove(TestUtils.getLegalMove(board, "g1", "f1"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "c8"));

        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));
        board.makeMove(TestUtils.getLegalMove(board, "c8", "d8"));

        board.makeMove(TestUtils.getLegalMove(board, "e1", "d1"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "e8"));

        board.makeMove(TestUtils.getLegalMove(board, "d1", "c1"));
        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));

        board.makeMove(TestUtils.getLegalMove(board, "c1", "b1"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "g8"));

        Assertions.assertFalse(Result.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "b1", "a1"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "h8"));

        // position now repeated once
        Assertions.assertTrue(Result.isEffectiveDraw(board));

        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));
        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));

        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));
        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));

        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));
        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));

        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));
        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));

        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));
        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));

        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));
        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));

        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));
        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));

        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));
        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));

        board.makeMove(TestUtils.getLegalMove(board, "b8", "c8"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f1"));

        board.makeMove(TestUtils.getLegalMove(board, "c8", "d8"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));

        board.makeMove(TestUtils.getLegalMove(board, "d8", "e8"));
        board.makeMove(TestUtils.getLegalMove(board, "e1", "d1"));

        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "c1"));

        board.makeMove(TestUtils.getLegalMove(board, "f8", "g8"));
        board.makeMove(TestUtils.getLegalMove(board, "c1", "b1"));

        board.makeMove(TestUtils.getLegalMove(board, "g8", "h8"));
        board.makeMove(TestUtils.getLegalMove(board, "b1", "a1"));

        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));
        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));

        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));

        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));
        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));

        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));
        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));

        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));
        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));

        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));
        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));

        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));
        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));

        // 50 move rule not yet reached
        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));
        Assertions.assertFalse(Result.isFiftyMoveRule(board));

        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));
        // 50 move rule reached
        Assertions.assertTrue(Result.isFiftyMoveRule(board));

    }

    @Test
    public void testDrawByFiftyMovesSinceWhitePawnMove() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(Piece.KING, false, 0);
        board.toggleSquare(Piece.PAWN, false, 36);

        board.toggleSquare(Piece.KING, true, 63);
        board.toggleSquare(Piece.PAWN, true, 12);
        board.setPieceList(BoardUtils.calculatePieceList(board));

        board.setWhiteToMove(true);

        // white pawn makes last possible pawn move
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));

        // kings walk about until 50 move rule reached
        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));
        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));

        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));
        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));

        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));
        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));

        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));
        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));

        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));
        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));

        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));
        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));

        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));
        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));

        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));
        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));

        board.makeMove(TestUtils.getLegalMove(board, "b8", "c8"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f1"));

        board.makeMove(TestUtils.getLegalMove(board, "c8", "d8"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));

        board.makeMove(TestUtils.getLegalMove(board, "d8", "e8"));
        board.makeMove(TestUtils.getLegalMove(board, "e1", "d1"));

        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "c1"));

        board.makeMove(TestUtils.getLegalMove(board, "f8", "g8"));
        board.makeMove(TestUtils.getLegalMove(board, "c1", "b1"));

        board.makeMove(TestUtils.getLegalMove(board, "g8", "h8"));
        board.makeMove(TestUtils.getLegalMove(board, "b1", "a1"));

        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));
        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));

        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));

        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));
        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));

        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));
        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));

        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));
        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));

        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));
        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));

        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));
        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));

        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));
        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));

        board.makeMove(TestUtils.getLegalMove(board, "g1", "f1"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "c8"));

        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));
        board.makeMove(TestUtils.getLegalMove(board, "c8", "d8"));

        board.makeMove(TestUtils.getLegalMove(board, "e1", "d1"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "e8"));

        board.makeMove(TestUtils.getLegalMove(board, "d1", "c1"));
        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));

        board.makeMove(TestUtils.getLegalMove(board, "c1", "b1"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "g8"));

        board.makeMove(TestUtils.getLegalMove(board, "b1", "a1"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "h8"));

        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));
        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));

        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));
        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));

        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));
        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));

        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));
        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));

        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));
        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));

        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));
        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));

        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));
        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));

        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));
        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));

        board.makeMove(TestUtils.getLegalMove(board, "b8", "c8"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f1"));

        board.makeMove(TestUtils.getLegalMove(board, "c8", "d8"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));

        board.makeMove(TestUtils.getLegalMove(board, "d8", "e8"));
        board.makeMove(TestUtils.getLegalMove(board, "e1", "d1"));

        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "c1"));

        board.makeMove(TestUtils.getLegalMove(board, "f8", "g8"));
        board.makeMove(TestUtils.getLegalMove(board, "c1", "b1"));

        board.makeMove(TestUtils.getLegalMove(board, "g8", "h8"));
        board.makeMove(TestUtils.getLegalMove(board, "b1", "a1"));

        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));
        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));

        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));

        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));
        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));

        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));
        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));

        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));
        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));

        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));
        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));

        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));
        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));

        // 50 move rule not yet reached
        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));
        Assertions.assertFalse(Result.isFiftyMoveRule(board));

        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));
        // 50 move rule reached
        Assertions.assertTrue(Result.isFiftyMoveRule(board));

    }

    @Test
    public void testDrawByFiftyMovesSinceBlackCapture() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(Piece.KING, true, 0);
        board.toggleSquare(Piece.KNIGHT, true, 28);
        board.toggleSquare(Piece.KNIGHT, true, 36);
        board.toggleSquare(Piece.QUEEN, true, 35);

        board.toggleSquare(Piece.KING, false, 63);
        board.toggleSquare(Piece.KNIGHT, false, 18);
        board.setPieceList(BoardUtils.calculatePieceList(board));

        board.setWhiteToMove(false);

        // black knight captures white queen
        board.makeMove(TestUtils.getLegalMove(board, "c3", "d5"));

        // kings walk about until 50 move rule reached
        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));
        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));

        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));
        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));

        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));
        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));

        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));
        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));

        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));
        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));

        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));
        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));

        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));
        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));

        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));
        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));

        board.makeMove(TestUtils.getLegalMove(board, "b8", "c8"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f1"));

        board.makeMove(TestUtils.getLegalMove(board, "c8", "d8"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));

        board.makeMove(TestUtils.getLegalMove(board, "d8", "e8"));
        board.makeMove(TestUtils.getLegalMove(board, "e1", "d1"));

        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "c1"));

        board.makeMove(TestUtils.getLegalMove(board, "f8", "g8"));
        board.makeMove(TestUtils.getLegalMove(board, "c1", "b1"));

        board.makeMove(TestUtils.getLegalMove(board, "g8", "h8"));
        board.makeMove(TestUtils.getLegalMove(board, "b1", "a1"));

        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));
        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));

        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));

        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));
        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));

        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));
        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));

        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));
        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));

        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));
        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));

        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));
        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));

        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));
        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));

        board.makeMove(TestUtils.getLegalMove(board, "g1", "f1"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "c8"));

        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));
        board.makeMove(TestUtils.getLegalMove(board, "c8", "d8"));

        board.makeMove(TestUtils.getLegalMove(board, "e1", "d1"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "e8"));

        board.makeMove(TestUtils.getLegalMove(board, "d1", "c1"));
        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));

        board.makeMove(TestUtils.getLegalMove(board, "c1", "b1"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "g8"));

        board.makeMove(TestUtils.getLegalMove(board, "b1", "a1"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "h8"));

        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));
        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));

        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));
        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));

        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));
        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));

        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));
        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));

        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));
        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));

        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));
        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));

        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));
        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));

        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));
        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));

        board.makeMove(TestUtils.getLegalMove(board, "b8", "c8"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f1"));

        board.makeMove(TestUtils.getLegalMove(board, "c8", "d8"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));

        board.makeMove(TestUtils.getLegalMove(board, "d8", "e8"));
        board.makeMove(TestUtils.getLegalMove(board, "e1", "d1"));

        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "c1"));

        board.makeMove(TestUtils.getLegalMove(board, "f8", "g8"));
        board.makeMove(TestUtils.getLegalMove(board, "c1", "b1"));

        board.makeMove(TestUtils.getLegalMove(board, "g8", "h8"));
        board.makeMove(TestUtils.getLegalMove(board, "b1", "a1"));

        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));
        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));

        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));

        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));
        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));

        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));
        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));

        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));
        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));

        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));
        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));

        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));
        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));

        // 50 move rule not yet reached
        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));

        Assertions.assertFalse(Result.isFiftyMoveRule(board));

        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));
        // 50 move rule reached
        Assertions.assertTrue(Result.isFiftyMoveRule(board));

    }

    @Test
    public void testDrawByFiftyMovesSinceBlackPawnMove() {

        Board board = TestUtils.emptyBoard();
        board.toggleSquare(Piece.KING, true, 0);
        board.toggleSquare(Piece.PAWN, true, 28);

        board.toggleSquare(Piece.KING, false, 63);
        board.toggleSquare(Piece.PAWN, false, 52);
        board.setPieceList(BoardUtils.calculatePieceList(board));

        board.setWhiteToMove(false);

        // black pawn makes last possible pawn move
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));

        // kings walk about until 50 move rule reached
        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));
        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));

        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));
        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));

        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));
        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));

        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));
        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));

        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));
        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));

        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));
        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));

        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));
        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));

        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));
        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));

        board.makeMove(TestUtils.getLegalMove(board, "b8", "c8"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f1"));

        board.makeMove(TestUtils.getLegalMove(board, "c8", "d8"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));

        board.makeMove(TestUtils.getLegalMove(board, "d8", "e8"));
        board.makeMove(TestUtils.getLegalMove(board, "e1", "d1"));

        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "c1"));

        board.makeMove(TestUtils.getLegalMove(board, "f8", "g8"));
        board.makeMove(TestUtils.getLegalMove(board, "c1", "b1"));

        board.makeMove(TestUtils.getLegalMove(board, "g8", "h8"));
        board.makeMove(TestUtils.getLegalMove(board, "b1", "a1"));

        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));
        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));

        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));

        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));
        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));

        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));
        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));

        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));
        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));

        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));
        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));

        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));
        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));

        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));
        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));

        board.makeMove(TestUtils.getLegalMove(board, "g1", "f1"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "c8"));

        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));
        board.makeMove(TestUtils.getLegalMove(board, "c8", "d8"));

        board.makeMove(TestUtils.getLegalMove(board, "e1", "d1"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "e8"));

        board.makeMove(TestUtils.getLegalMove(board, "d1", "c1"));
        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));

        board.makeMove(TestUtils.getLegalMove(board, "c1", "b1"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "g8"));

        board.makeMove(TestUtils.getLegalMove(board, "b1", "a1"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "h8"));

        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));
        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));

        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));
        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));

        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));
        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));

        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));
        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));

        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));
        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));

        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));
        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));

        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));
        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));

        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));
        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));

        board.makeMove(TestUtils.getLegalMove(board, "b8", "c8"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f1"));

        board.makeMove(TestUtils.getLegalMove(board, "c8", "d8"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "e1"));

        board.makeMove(TestUtils.getLegalMove(board, "d8", "e8"));
        board.makeMove(TestUtils.getLegalMove(board, "e1", "d1"));

        board.makeMove(TestUtils.getLegalMove(board, "e8", "f8"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "c1"));

        board.makeMove(TestUtils.getLegalMove(board, "f8", "g8"));
        board.makeMove(TestUtils.getLegalMove(board, "c1", "b1"));

        board.makeMove(TestUtils.getLegalMove(board, "g8", "h8"));
        board.makeMove(TestUtils.getLegalMove(board, "b1", "a1"));

        board.makeMove(TestUtils.getLegalMove(board, "h8", "h7"));
        board.makeMove(TestUtils.getLegalMove(board, "a1", "a2"));

        board.makeMove(TestUtils.getLegalMove(board, "h7", "h6"));
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a3"));

        board.makeMove(TestUtils.getLegalMove(board, "h6", "h5"));
        board.makeMove(TestUtils.getLegalMove(board, "a3", "a4"));

        board.makeMove(TestUtils.getLegalMove(board, "h5", "h4"));
        board.makeMove(TestUtils.getLegalMove(board, "a4", "a5"));

        board.makeMove(TestUtils.getLegalMove(board, "h4", "h3"));
        board.makeMove(TestUtils.getLegalMove(board, "a5", "a6"));

        board.makeMove(TestUtils.getLegalMove(board, "h3", "h2"));
        board.makeMove(TestUtils.getLegalMove(board, "a6", "a7"));

        board.makeMove(TestUtils.getLegalMove(board, "h2", "h1"));
        board.makeMove(TestUtils.getLegalMove(board, "a7", "a8"));

        // 50 move rule not yet reached
        board.makeMove(TestUtils.getLegalMove(board, "h1", "g1"));
        Assertions.assertFalse(Result.isFiftyMoveRule(board));

        // 50 move rule reached
        board.makeMove(TestUtils.getLegalMove(board, "a8", "b8"));
        Assertions.assertTrue(Result.isFiftyMoveRule(board));

    }

}
