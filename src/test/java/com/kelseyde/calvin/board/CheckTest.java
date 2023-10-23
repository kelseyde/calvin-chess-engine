package com.kelseyde.calvin.board;

import com.kelseyde.calvin.utils.IllegalMoveException;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CheckTest {

    @Test
    public void checkBlocksOtherMoves() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "h5"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "h4"));

        // check
        board.makeMove(TestUtils.getLegalMove(board, "h5", "f7"));

        // try to ignore check with other moves
        Assertions.assertThrows(IllegalMoveException.class,
                () -> board.makeMove(TestUtils.getLegalMove(board, "a7", "a6")));

    }

    @Test
    public void cannotMovePinnedPawn() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "h5"));

        // black tries to move pinned f-pawn
        Assertions.assertThrows(IllegalMoveException.class,
                () -> board.makeMove(TestUtils.getLegalMove(board, "f7", "f6")));

    }

    @Test
    public void cannotEnPassantWithPinnedPawn() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));
        board.makeMove(TestUtils.getLegalMove(board, "e5", "d4"));
        board.makeMove(TestUtils.getLegalMove(board, "e4", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "e7"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "d7", "d5"));

        // black tries to en-passant with pinned e-pawn
        Assertions.assertThrows(IllegalMoveException.class,
                () -> board.makeMove(TestUtils.getLegalMove(board, "e5", "d6")));

    }

    @Test
    public void cannotMovePinnedKnight() {
        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "f7", "f5"));
        board.makeMove(TestUtils.getLegalMove(board, "e4", "f5"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e6"));
        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));
        board.makeMove(TestUtils.getLegalMove(board, "e6", "f5"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "e2"));
        // block check with knight
        board.makeMove(TestUtils.getLegalMove(board, "g8", "e7"));
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a4"));
        //try moving pinned knight
        Assertions.assertThrows(IllegalMoveException.class,
                () -> board.makeMove(TestUtils.getLegalMove(board, "e7", "g8")));
    }

    @Test
    public void cannotMovePinnedBishop() {
        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "f7", "f5"));
        board.makeMove(TestUtils.getLegalMove(board, "e4", "f5"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e6"));
        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));
        board.makeMove(TestUtils.getLegalMove(board, "e6", "f5"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "e2"));
        // block check with bishop
        board.makeMove(TestUtils.getLegalMove(board, "f8", "e7"));
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a4"));
        //try moving pinned bishop
        Assertions.assertThrows(IllegalMoveException.class,
                () -> board.makeMove(TestUtils.getLegalMove(board, "e7", "f8")));
    }

    @Test
    public void cannotMovePinnedQueen() {
        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "f7", "f5"));
        board.makeMove(TestUtils.getLegalMove(board, "e4", "f5"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e6"));
        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));
        board.makeMove(TestUtils.getLegalMove(board, "e6", "f5"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "e2"));
        // block check with queen
        board.makeMove(TestUtils.getLegalMove(board, "d8", "e7"));
        board.makeMove(TestUtils.getLegalMove(board, "a2", "a4"));
        //try moving pinned queen
        Assertions.assertThrows(IllegalMoveException.class,
                () -> board.makeMove(TestUtils.getLegalMove(board, "e7", "d8")));
    }

    @Test
    public void cannotMoveFromCheckIntoAnotherCheck() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "h5"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "h4"));

        // check
        board.makeMove(TestUtils.getLegalMove(board, "h5", "f7"));

        // try to move to another checked square
        Assertions.assertThrows(IllegalMoveException.class,
                () -> board.makeMove(TestUtils.getLegalMove(board, "e8", "e7")));

    }

    @Test
    public void canCaptureUnprotectedCheckingPiece() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "h5"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "h4"));

        // check
        board.makeMove(TestUtils.getLegalMove(board, "h5", "f7"));
        // capture checking queen
        board.makeMove(TestUtils.getLegalMove(board, "e8", "f7"));

    }

    @Test
    public void cannotCaptureProtectedCheckingPieceWithKing() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "c4"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "c6"));
        // wayward queen!
        board.makeMove(TestUtils.getLegalMove(board, "d1", "h5"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "e7"));

        // check
        board.makeMove(TestUtils.getLegalMove(board, "h5", "f7"));
        // try capturing checking queen with king
        Assertions.assertThrows(IllegalMoveException.class,
                () -> board.makeMove(TestUtils.getLegalMove(board, "e8", "f7")));

    }

    @Test
    public void canCaptureProtectedCheckingPieceWithOtherPiece() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "c4"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "c6"));
        // wayward queen!
        board.makeMove(TestUtils.getLegalMove(board, "d1", "h5"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "e7"));

        // check
        board.makeMove(TestUtils.getLegalMove(board, "h5", "f7"));
        // try capturing checking queen with queen
        board.makeMove(TestUtils.getLegalMove(board, "e7", "f7"));

    }

    @Test
    public void cannotCastleOutOfCheck() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e6"));
        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));
        board.makeMove(TestUtils.getLegalMove(board, "d7", "d5"));
        board.makeMove(TestUtils.getLegalMove(board, "e4", "d5"));
        board.makeMove(TestUtils.getLegalMove(board, "e6", "d5"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "d3"));
        board.makeMove(TestUtils.getLegalMove(board, "f8", "d6"));

        // check
        board.makeMove(TestUtils.getLegalMove(board, "d1", "e2"));

        // try to castle out of check
        Assertions.assertThrows(IllegalMoveException.class,
                () -> board.makeMove(TestUtils.getLegalMove(board, "e8", "g8")));

    }

    @Test
    public void cannotCastleThroughCheck() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "f2", "f4"));
        board.makeMove(TestUtils.getLegalMove(board, "b7", "b6"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "a6"));
        board.makeMove(TestUtils.getLegalMove(board, "c8", "a6"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "d7", "d6"));

        // try to castle through the bishop check
        Assertions.assertThrows(IllegalMoveException.class,
                () -> board.makeMove(TestUtils.getLegalMove(board, "e1", "g1")));

    }

    @Test
    public void cannotCastleQueensideThroughKnightCheck() {

        String fen = "r3k2r/p1ppqpb1/bnN1pnp1/3P4/1p2P3/2N2Q1p/PPPBBPPP/R3K2R b KQkq - 1 1";
        Board board = FEN.fromFEN(fen);

        // try to castle through the bishop check
        Assertions.assertThrows(IllegalMoveException.class,
                () -> board.makeMove(TestUtils.getLegalMove(board, "e8", "c8")));

    }

    @Test
    public void cannotStepKingBackOnRayOfCheckingOrthogonalSlider() {

        String fen = "8/8/8/2k5/2r2K2/8/8/8 w - - 0 1";
        Board board = FEN.fromFEN(fen);

        // try to step away on same rank as rook
        Assertions.assertThrows(IllegalMoveException.class,
                () -> board.makeMove(TestUtils.getLegalMove(board, "f4", "g4")));

    }

    @Test
    public void cannotStepKingBackOnRayOfCheckingDiagonalSlider() {

        String fen = "8/8/3b4/2k5/5K2/8/8/8 w - - 0 1";
        Board board = FEN.fromFEN(fen);

        // try to step away on same diagonal as bishop
        Assertions.assertThrows(IllegalMoveException.class,
                () -> board.makeMove(TestUtils.getLegalMove(board, "f4", "g3")));

    }

    @Test
    public void cannotEnPassantIntoCheck() {

        // fen = en passant funhouse fen
        String fen = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ";
        Board board = FEN.fromFEN(fen);

        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        Assertions.assertThrows(IllegalMoveException.class,
                () -> board.makeMove(TestUtils.getLegalMove(board, "f4", "e3")));

    }

    @Test
    public void canEnPassantCheckingPawn() {

        String fen = "8/2p5/3p4/1P4r1/1K3p1k/8/4P1P1/1R6 b - - 3 2";

        Board board = FEN.fromFEN(fen);

        board.makeMove(TestUtils.getLegalMove(board, "c7", "c5"));
        board.makeMove(TestUtils.getLegalMove(board, "b5", "c6"));

    }

    @Test
    public void testMustBlockQueenCheck() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "d7", "d5"));
        board.makeMove(TestUtils.getLegalMove(board, "f3", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "d5", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "b5"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "d7"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "e2"));
        board.makeMove(TestUtils.getLegalMove(board, "d8", "e7"));
        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));
        board.makeMove(TestUtils.getLegalMove(board, "c7", "c6"));
        board.makeMove(TestUtils.getLegalMove(board, "e5", "c6"));
        board.makeMove(TestUtils.getLegalMove(board, "b7", "c6"));
        board.makeMove(TestUtils.getLegalMove(board, "b5", "c6"));
        board.makeMove(TestUtils.getLegalMove(board, "c8", "b7"));
        board.makeMove(TestUtils.getLegalMove(board, "c6", "b7"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "b4"));
        board.makeMove(TestUtils.getLegalMove(board, "b1", "c3"));
        board.makeMove(TestUtils.getLegalMove(board, "b4", "b7"));
        board.makeMove(TestUtils.getLegalMove(board, "c3", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "b7", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));

        Assertions.assertThrows(IllegalMoveException.class,
                () -> TestUtils.getLegalMove(board, "g8", "f6"));

    }

}
