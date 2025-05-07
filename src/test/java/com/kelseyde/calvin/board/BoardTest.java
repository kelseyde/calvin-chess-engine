package com.kelseyde.calvin.board;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.uci.UCI;
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
        Board board = FEN.startpos().toBoard();
        board.makeMove(Move.fromUCI("e2e4"));
        Assertions.assertNull(board.pieceAt(Square.fromNotation("e2")));
        Assertions.assertEquals(Piece.PAWN, board.pieceAt(Square.fromNotation("e4")));
    }

    @Test
    public void testBoardHistoryPreservedMultipleMoves() {
        Board board = FEN.startpos().toBoard();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "c6"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "b5"));
        board.makeMove(TestUtils.getLegalMove(board, "a7", "a6"));
        board.makeMove(TestUtils.getLegalMove(board, "b5", "a4"));

        int moves = 0;
        for (int i = 0; i < board.getPly(); i++) {
            if (board.getMoves()[i] != null) {
                moves++;
            }
        }
        Assertions.assertEquals(7, moves);
        Assertions.assertEquals(7, board.getPly());

    }

    @Test
    public void testBoardHistoryPreservesCastlingRights() {

        Board board = FEN.startpos().toBoard();
        Assertions.assertTrue(Castling.kingsideAllowed(board.getState().rights, true));
        Assertions.assertTrue(Castling.queensideAllowed(board.getState().rights, true));
        Assertions.assertTrue(Castling.kingsideAllowed(board.getState().rights, false));
        Assertions.assertTrue(Castling.queensideAllowed(board.getState().rights, false));

        board.makeMove(TestUtils.getLegalMove(board, "e2", "e3"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e6"));

        Assertions.assertTrue(Castling.kingsideAllowed(board.getState().rights, true));
        Assertions.assertTrue(Castling.queensideAllowed(board.getState().rights, true));
        Assertions.assertTrue(Castling.kingsideAllowed(board.getState().rights, false));
        Assertions.assertTrue(Castling.queensideAllowed(board.getState().rights, false));

        board.makeMove(TestUtils.getLegalMove(board, "e1", "e2"));

        Assertions.assertFalse(Castling.kingsideAllowed(board.getState().rights, true));
        Assertions.assertFalse(Castling.queensideAllowed(board.getState().rights, true));
        Assertions.assertTrue(Castling.kingsideAllowed(board.getState().rights, false));
        Assertions.assertTrue(Castling.queensideAllowed(board.getState().rights, false));

        Assertions.assertTrue(Castling.kingsideAllowed(board.getStates()[board.getPly() - 1].rights, true));
        Assertions.assertTrue(Castling.queensideAllowed(board.getStates()[board.getPly() - 1].rights, true));
        Assertions.assertTrue(Castling.kingsideAllowed(board.getStates()[board.getPly() - 1].rights, false));
        Assertions.assertTrue(Castling.queensideAllowed(board.getStates()[board.getPly() - 1].rights, false));

        board.makeMove(TestUtils.getLegalMove(board, "f7", "f6"));

        Assertions.assertFalse(Castling.kingsideAllowed(board.getState().rights, true));
        Assertions.assertFalse(Castling.queensideAllowed(board.getState().rights, true));
        Assertions.assertTrue(Castling.kingsideAllowed(board.getState().rights, false));
        Assertions.assertTrue(Castling.queensideAllowed(board.getState().rights, false));

        Assertions.assertFalse(Castling.kingsideAllowed(board.getStates()[board.getPly() - 1].rights, true));
        Assertions.assertFalse(Castling.queensideAllowed(board.getStates()[board.getPly() - 1].rights, true));
        Assertions.assertTrue(Castling.kingsideAllowed(board.getStates()[board.getPly() - 1].rights, false));
        Assertions.assertTrue(Castling.queensideAllowed(board.getStates()[board.getPly() - 1].rights, false));

    }

    @Test
    public void testUnmakeMoveRestoresCapturedPieces() {

        Board board = FEN.startpos().toBoard();
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

        Board board = FEN.startpos().toBoard();
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

        Board board = FEN.startpos().toBoard();
        board.makeMove(new Move(12, 28));
        board.makeMove(new Move(52, 44));
        board.makeMove(new Move(6, 21));
        board.makeMove(new Move(62, 45));
        board.makeMove(new Move(5, 12));
        board.makeMove(new Move(61, 52));
        // castles
        board.makeMove(new Move(4, 6, Move.CASTLE_FLAG));
        Assertions.assertFalse(board.isWhite());
        Assertions.assertFalse(Castling.kingsideAllowed(board.getState().rights, true));
        Assertions.assertFalse(Castling.queensideAllowed(board.getState().rights, true));
        Assertions.assertTrue(Castling.kingsideAllowed(board.getState().rights, false));
        Assertions.assertTrue(Castling.queensideAllowed(board.getState().rights, false));

        board.unmakeMove();
        Assertions.assertTrue(board.isWhite());
        Assertions.assertTrue(Castling.kingsideAllowed(board.getState().rights, true));
        Assertions.assertTrue(Castling.queensideAllowed(board.getState().rights, true));
        Assertions.assertTrue(Castling.kingsideAllowed(board.getState().rights, false));
        Assertions.assertTrue(Castling.queensideAllowed(board.getState().rights, false));

    }

    @Test
    public void testRookCannotJumpToOtherSide() {

        String fen = "r1b1k2r/1p3p2/8/8/1n6/2Q5/4P2p/5KNR w kq - 0 1";
        Board board = FEN.parse(fen).toBoard();
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

        Board board = FEN.parse("rn1qkb1r/ppp2ppp/3p1n2/8/2BPPpb1/5N2/PPP3PP/RNBQK2R w KQkq - 1 6").toBoard();
        long initialZobrist = board.getState().getKey();
        Assertions.assertTrue(board.isWhite());
        board.makeNullMove();
        Assertions.assertFalse(board.isWhite());
        Board board2 = FEN.parse("rn1qkb1r/ppp2ppp/3p1n2/8/2BPPpb1/5N2/PPP3PP/RNBQK2R b KQkq - 1 6").toBoard();
        Assertions.assertEquals(board.getState().getKey(), board2.getState().getKey());
        board.unmakeNullMove();
        Assertions.assertTrue(board.isWhite());
        Assertions.assertEquals(initialZobrist, board.getState().getKey());

    }

    @Test
    public void testUnmakeMoveResetsEnPassantFile() {

        Board board = FEN.parse("r1bqkbnr/ppp1pppp/2n5/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3").toBoard();
        long initialZobrist = board.getState().getKey();
        Assertions.assertEquals(3, board.getState().getEnPassantFile());
        board.makeNullMove();
        Assertions.assertEquals(-1, board.getState().getEnPassantFile());
        board.unmakeNullMove();
        Assertions.assertEquals(3, board.getState().getEnPassantFile());
        Assertions.assertEquals(initialZobrist, board.getState().getKey());

    }

    @Test
    public void testUnmakeMoveResetsFiftyMoveCounter() {

        Board board = FEN.parse("8/4n3/2kn4/8/3B4/5K2/8/8 w - - 4 3").toBoard();
        long initialZobrist = board.getState().getKey();
        Assertions.assertEquals(4, board.getState().getHalfMoveClock());
        board.makeNullMove();
        Assertions.assertEquals(0, board.getState().getHalfMoveClock());
        board.unmakeNullMove();
        Assertions.assertEquals(4, board.getState().getHalfMoveClock());
        Assertions.assertEquals(initialZobrist, board.getState().getKey());

    }

    @Test
    public void testPromotionWithFilters() {

        String fen = "8/k5P1/8/8/8/8/5rr1/7K w - - 0 1";
        Board board = FEN.parse(fen).toBoard();

        MoveGenerator moveGenerator = new MoveGenerator();

        Assertions.assertEquals(0, moveGenerator.generateMoves(board, MoveGenerator.MoveFilter.QUIET).size());
        Assertions.assertEquals(4, moveGenerator.generateMoves(board, MoveGenerator.MoveFilter.NOISY).size());

    }

    @Test
    public void testUnmakeCastling() {

        Board board = FEN.startpos().toBoard();
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

        Board board2 = FEN.parse("2kr1bnr/pppqpppp/2n5/1B1p4/4P1b1/5N2/PPPP1PPP/RNBQR1K1 w - - 8 6").toBoard();
        Assertions.assertEquals(board.getWhitePieces(), board2.getWhitePieces());
        Assertions.assertEquals(board.getBlackPieces(), board2.getBlackPieces());
        Assertions.assertEquals(board.getRooks(), board2.getRooks());

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

        Board board3 = FEN.startpos().toBoard();
        Assertions.assertEquals(board.getWhitePieces(), board3.getWhitePieces());
        Assertions.assertEquals(board.getBlackPieces(), board3.getBlackPieces());
        Assertions.assertEquals(board.getRooks(), board3.getRooks());

    }

    @Test
    public void testUnmakeCastlingChess960() {

        UCI.Options.chess960 = true;

        Board board = Board.from("rnbqk2r/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4");
        board.makeMove(Move.fromUCI("e1h1", Move.CASTLE_FLAG));
        Assertions.assertEquals(Piece.KING, board.pieceAt(Square.fromNotation("g1")));
        Assertions.assertEquals(Piece.ROOK, board.pieceAt(Square.fromNotation("f1")));
        Assertions.assertEquals(Bits.of(Square.fromNotation("g1")), board.getKing(true));
        Assertions.assertTrue(Bits.contains(board.getRooks(true), Square.fromNotation("f1")));

        board.unmakeMove();
        Assertions.assertEquals(Piece.KING, board.pieceAt(Square.fromNotation("e1")));
        Assertions.assertEquals(Piece.ROOK, board.pieceAt(Square.fromNotation("h1")));
        Assertions.assertEquals(Bits.of(Square.fromNotation("e1")), board.getKing(true));
        Assertions.assertTrue(Bits.contains(board.getRooks(true), Square.fromNotation("h1")));

        UCI.Options.chess960 = false;

    }

    @Test
    public void testMakeTooManyMoves() {
        Board board = FEN.startpos().toBoard();
        for (int i = 0; i < 65; i++) {
            board.makeMove(Move.fromUCI("g1f3"));
            board.makeMove(Move.fromUCI("g8f6"));
            board.makeMove(Move.fromUCI("f3g1"));
            board.makeMove(Move.fromUCI("f6g8"));
        }
    }

    private Set<Integer> getPiecePositions(Board board, boolean whiteToMove) {
        Set<Integer> positions = new HashSet<>();
        if (whiteToMove) {
            long whitePieces = board.getWhitePieces();
            while (whitePieces != 0) {
                int position = Bits.next(whitePieces);
                positions.add(position);
                whitePieces = Bits.pop(whitePieces);
            }
        } else {
            long blackPieces = board.getBlackPieces();
            while (blackPieces != 0) {
                int position = Bits.next(blackPieces);
                positions.add(position);
                blackPieces = Bits.pop(blackPieces);
            }
        }
        return positions;
    }


}
