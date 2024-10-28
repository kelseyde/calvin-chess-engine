package com.kelseyde.calvin.board;

import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.utils.IllegalMoveException;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class BoardTest {

    @Test
    public void testSimpleMakeMove() {
        Board board = Board.from(FEN.STARTPOS);
        board.makeMove(Move.fromUCI("e2e4"));
        Assertions.assertNull(board.pieceAt(Bits.Square.fromNotation("e2")));
        Assertions.assertEquals(Piece.PAWN, board.pieceAt(Bits.Square.fromNotation("e4")));
    }

    @Test
    public void testBoardHistoryPreservedMultipleMoves() {
        Board board = Board.from(FEN.STARTPOS);
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "c6"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "b5"));
        board.makeMove(TestUtils.getLegalMove(board, "a7", "a6"));
        board.makeMove(TestUtils.getLegalMove(board, "b5", "a4"));

        int moves = 0;
        for (int i = 0; i < board.ply(); i++) {
            if (board.moves()[i] != null) {
                moves++;
            }
        }
        Assertions.assertEquals(7, moves);
        Assertions.assertEquals(7, board.ply());

    }

    @Test
    public void testBoardHistoryPreservesCastlingRights() {

        Board board = Board.from(FEN.STARTPOS);
        Assertions.assertTrue(board.state().isKingsideCastlingAllowed(true));
        Assertions.assertTrue(board.state().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.state().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.state().isQueensideCastlingAllowed(false));

        board.makeMove(TestUtils.getLegalMove(board, "e2", "e3"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e6"));

        Assertions.assertTrue(board.state().isKingsideCastlingAllowed(true));
        Assertions.assertTrue(board.state().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.state().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.state().isQueensideCastlingAllowed(false));

        board.makeMove(TestUtils.getLegalMove(board, "e1", "e2"));

        Assertions.assertFalse(board.state().isKingsideCastlingAllowed(true));
        Assertions.assertFalse(board.state().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.state().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.state().isQueensideCastlingAllowed(false));

        Assertions.assertTrue(board.states()[board.ply() - 1].isKingsideCastlingAllowed(true));
        Assertions.assertTrue(board.states()[board.ply() - 1].isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.states()[board.ply() - 1].isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.states()[board.ply() - 1].isQueensideCastlingAllowed(false));

        board.makeMove(TestUtils.getLegalMove(board, "f7", "f6"));

        Assertions.assertFalse(board.state().isKingsideCastlingAllowed(true));
        Assertions.assertFalse(board.state().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.state().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.state().isQueensideCastlingAllowed(false));

        Assertions.assertFalse(board.states()[board.ply() - 1].isKingsideCastlingAllowed(true));
        Assertions.assertFalse(board.states()[board.ply() - 1].isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.states()[board.ply() - 1].isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.states()[board.ply() - 1].isQueensideCastlingAllowed(false));

    }

    @Test
    public void testUnmakeMoveRestoresCapturedPieces() {

        Board board = Board.from(FEN.STARTPOS);
        board.makeMove(new Move(12, 28));
        board.makeMove(new Move(51, 35));
        board.makeMove(new Move(28, 35));

        Set<Integer> whitePiecePositions = getPiecePositions(board, true);
        Assertions.assertEquals(Set.of(35, 8, 9, 10, 11, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7), whitePiecePositions);

        Set<Integer> blackPiecePositions = getPiecePositions(board, false);
        Assertions.assertEquals(Set.of(56, 57, 58, 59, 60, 61, 62, 63, 48, 49, 50, 52, 53, 54, 55), blackPiecePositions);

        board.unmakeMove();

        whitePiecePositions = getPiecePositions(board, true);
        Assertions.assertEquals(Set.of(28, 8, 9, 10, 11, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7), whitePiecePositions);

        blackPiecePositions = getPiecePositions(board, false);
        Assertions.assertEquals(Set.of(35, 56, 57, 58, 59, 60, 61, 62, 63, 48, 49, 50, 52, 53, 54, 55), blackPiecePositions);

    }

    @Test
    public void testUnmakeMoveHandlesTurnSwitching() {

        Board board = Board.from(FEN.STARTPOS);
        Assertions.assertTrue(board.isWhite());

        board.makeMove(new Move(12, 28));
        Assertions.assertFalse(board.isWhite());

        board.makeMove(new Move(51, 35));
        Assertions.assertTrue(board.isWhite());

        board.makeMove(new Move(28, 35));
        Assertions.assertFalse(board.isWhite());

        board.unmakeMove();
        Assertions.assertTrue(board.isWhite());

    }

    @Test
    public void testUnmakeMoveHandlesCastling() {

        Board board = Board.from(FEN.STARTPOS);
        board.makeMove(new Move(12, 28));
        board.makeMove(new Move(52, 44));
        board.makeMove(new Move(6, 21));
        board.makeMove(new Move(62, 45));
        board.makeMove(new Move(5, 12));
        board.makeMove(new Move(61, 52));
        // castles
        board.makeMove(new Move(4, 6, Move.CASTLE_FLAG));
        Assertions.assertFalse(board.isWhite());
        Assertions.assertFalse(board.state().isKingsideCastlingAllowed(true));
        Assertions.assertFalse(board.state().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.state().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.state().isQueensideCastlingAllowed(false));

        board.unmakeMove();
        Assertions.assertTrue(board.isWhite());
        Assertions.assertTrue(board.state().isKingsideCastlingAllowed(true));
        Assertions.assertTrue(board.state().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.state().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.state().isQueensideCastlingAllowed(false));

    }

    @Test
    public void testRookCannotJumpToOtherSide() {

        String fen = "r1b1k2r/1p3p2/8/8/1n6/2Q5/4P2p/5KNR w kq - 0 1";
        Board board = FEN.toBoard(fen);
        board.makeMove(TestUtils.getLegalMove(board, "c3", "b4"));
        Move queenPromotion = Move.fromUCI("h2g1q");
        board.makeMove(TestUtils.getLegalMove(board, queenPromotion));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "g1"));
        board.makeMove(TestUtils.getLegalMove(board, "h8", "h1"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "h1"));
        Assertions.assertThrows(IllegalMoveException.class, () ->
                board.makeMove(TestUtils.getLegalMove(board, "h8", "h1")));
    }

    @Test
    public void testMakeNullMoveChangesSideToMove() {

        Board board = FEN.toBoard("rn1qkb1r/ppp2ppp/3p1n2/8/2BPPpb1/5N2/PPP3PP/RNBQK2R w KQkq - 1 6");
        long initialZobrist = board.state().getKey();
        Assertions.assertTrue(board.isWhite());
        board.makeNullMove();
        Assertions.assertFalse(board.isWhite());
        Board board2 = FEN.toBoard("rn1qkb1r/ppp2ppp/3p1n2/8/2BPPpb1/5N2/PPP3PP/RNBQK2R b KQkq - 1 6");
        Assertions.assertEquals(board.state().getKey(), board2.state().getKey());
        board.unmakeNullMove();
        Assertions.assertTrue(board.isWhite());
        Assertions.assertEquals(initialZobrist, board.state().getKey());

    }

    @Test
    public void testUnmakeMoveResetsEnPassantFile() {

        Board board = FEN.toBoard("r1bqkbnr/ppp1pppp/2n5/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3");
        long initialZobrist = board.state().getKey();
        Assertions.assertEquals(3, board.state().getEnPassantFile());
        board.makeNullMove();
        Assertions.assertEquals(-1, board.state().getEnPassantFile());
        board.unmakeNullMove();
        Assertions.assertEquals(3, board.state().getEnPassantFile());
        Assertions.assertEquals(initialZobrist, board.state().getKey());

    }

    @Test
    public void testUnmakeMoveResetsFiftyMoveCounter() {

        Board board = FEN.toBoard("8/4n3/2kn4/8/3B4/5K2/8/8 w - - 4 3");
        long initialZobrist = board.state().getKey();
        Assertions.assertEquals(4, board.state().getHalfMoveClock());
        board.makeNullMove();
        Assertions.assertEquals(0, board.state().getHalfMoveClock());
        board.unmakeNullMove();
        Assertions.assertEquals(4, board.state().getHalfMoveClock());
        Assertions.assertEquals(initialZobrist, board.state().getKey());

    }

    @Test
    public void testPromotionWithFilters() {

        String fen = "8/k5P1/8/8/8/8/5rr1/7K w - - 0 1";
        Board board = FEN.toBoard(fen);

        MoveGenerator moveGenerator = new MoveGenerator();

        Assertions.assertEquals(0, moveGenerator.generateMoves(board, MoveGenerator.MoveFilter.QUIET).size());
        Assertions.assertEquals(4, moveGenerator.generateMoves(board, MoveGenerator.MoveFilter.NOISY).size());

    }

    @Test
    public void testUnmakeCastling() {

        Board board = Board.from(FEN.STARTPOS);
        board.makeMove(Move.fromUCI("e2e4"));
        board.makeMove(Move.fromUCI("d7d5"));
        board.makeMove(Move.fromUCI("g1f3"));
        board.makeMove(Move.fromUCI("b8c6"));
        board.makeMove(Move.fromUCI("f1b5"));
        board.makeMove(Move.fromUCI("c8g4"));
        board.makeMove(Move.fromUCI("e1g1", Move.CASTLE_FLAG));
        board.makeMove(Move.fromUCI("d8d7"));
        board.makeMove(Move.fromUCI("f1e1"));
        board.makeMove(Move.fromUCI("e8c8", Move.CASTLE_FLAG));

        Board board2 = FEN.toBoard("2kr1bnr/pppqpppp/2n5/1B1p4/4P1b1/5N2/PPPP1PPP/RNBQR1K1 w - - 8 6");
        Assertions.assertEquals(board.whitePieces(), board2.whitePieces());
        Assertions.assertEquals(board.blackPieces(), board2.blackPieces());
        Assertions.assertEquals(board.rooks(), board2.rooks());

        board.unmakeMove();
        board.unmakeMove();
        board.unmakeMove();
        board.unmakeMove();
        board.unmakeMove();
        board.unmakeMove();
        board.unmakeMove();
        board.unmakeMove();
        board.unmakeMove();
        board.unmakeMove();

        Board board3 = Board.from(FEN.STARTPOS);
        Assertions.assertEquals(board.whitePieces(), board3.whitePieces());
        Assertions.assertEquals(board.blackPieces(), board3.blackPieces());
        Assertions.assertEquals(board.rooks(), board3.rooks());

    }

    private Set<Integer> getPiecePositions(Board board, boolean whiteToMove) {
        Set<Integer> positions = new HashSet<>();
        if (whiteToMove) {
            long whitePieces = board.whitePieces();
            while (whitePieces != 0) {
                int position = Bits.next(whitePieces);
                positions.add(position);
                whitePieces = Bits.pop(whitePieces);
            }
        } else {
            long blackPieces = board.blackPieces();
            while (blackPieces != 0) {
                int position = Bits.next(blackPieces);
                positions.add(position);
                blackPieces = Bits.pop(blackPieces);
            }
        }
        return positions;
    }


}
