package com.kelseyde.calvin.board;

import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.IllegalMoveException;
import com.kelseyde.calvin.utils.notation.NotationUtils;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoardTest {

    @Test
    public void testFromPositionDoesNotCorruptBoard() {

        Board board = TestUtils.emptyBoard();
        assertSinglePieceBoard(board, 0);
        assertSinglePieceBoard(board, 7);
        assertSinglePieceBoard(board, 12);
        assertSinglePieceBoard(board, 18);
        assertSinglePieceBoard(board, 25);
        assertSinglePieceBoard(board, 31);
        assertSinglePieceBoard(board, 38);
        assertSinglePieceBoard(board, 36);
        assertSinglePieceBoard(board, 43);
        assertSinglePieceBoard(board, 54);
        assertSinglePieceBoard(board, 59);
        assertSinglePieceBoard(board, 60);
        assertSinglePieceBoard(board, 63);

    }

    @Test
    public void testBoardHistoryPreservedMultipleMoves() {
        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "c6"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "b5"));
        board.makeMove(TestUtils.getLegalMove(board, "a7", "a6"));
        board.makeMove(TestUtils.getLegalMove(board, "b5", "a4"));

        Assertions.assertEquals(7, board.getMoveHistory().size());

    }

    @Test
    public void testBoardHistoryPreservesCastlingRights() {

        Board board = new Board();
        Assertions.assertTrue(board.getGameState().isKingsideCastlingAllowed(true));
        Assertions.assertTrue(board.getGameState().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getGameState().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getGameState().isQueensideCastlingAllowed(false));

        board.makeMove(TestUtils.getLegalMove(board, "e2", "e3"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e6"));

        Assertions.assertTrue(board.getGameState().isKingsideCastlingAllowed(true));
        Assertions.assertTrue(board.getGameState().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getGameState().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getGameState().isQueensideCastlingAllowed(false));

        board.makeMove(TestUtils.getLegalMove(board, "e1", "e2"));

        Assertions.assertFalse(board.getGameState().isKingsideCastlingAllowed(true));
        Assertions.assertFalse(board.getGameState().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getGameState().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getGameState().isQueensideCastlingAllowed(false));

        Assertions.assertTrue(board.getGameStateHistory().peek().isKingsideCastlingAllowed(true));
        Assertions.assertTrue(board.getGameStateHistory().peek().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getGameStateHistory().peek().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getGameStateHistory().peek().isQueensideCastlingAllowed(false));

        board.makeMove(TestUtils.getLegalMove(board, "f7", "f6"));

        Assertions.assertFalse(board.getGameState().isKingsideCastlingAllowed(true));
        Assertions.assertFalse(board.getGameState().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getGameState().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getGameState().isQueensideCastlingAllowed(false));

        Assertions.assertFalse(board.getGameStateHistory().peek().isKingsideCastlingAllowed(true));
        Assertions.assertFalse(board.getGameStateHistory().peek().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getGameStateHistory().peek().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getGameStateHistory().peek().isQueensideCastlingAllowed(false));

    }

    @Test
    public void testSimpleUnmakeMove() {
        Board board1 = new Board();
        board1.makeMove(new Move(12, 28));
        board1.makeMove(new Move(52, 36));
        board1.makeMove(new Move(11, 27));
        board1.makeMove(new Move(51, 35));
        board1.makeMove(new Move(10, 26));
        board1.makeMove(new Move(50, 34));
        board1.makeMove(new Move(9, 25));
        board1.makeMove(new Move(49, 33));
        board1.makeMove(new Move(8, 24));
        board1.makeMove(new Move(48, 32));
        board1.unmakeMove();
        board1.unmakeMove();
        board1.unmakeMove();
        board1.unmakeMove();
        board1.unmakeMove();
        board1.unmakeMove();
        board1.unmakeMove();
        board1.unmakeMove();
        board1.unmakeMove();
        board1.unmakeMove();

        Board board2 = new Board();

        Assertions.assertEquals(board1.getWhitePawns(), board2.getWhitePawns());
        Assertions.assertEquals(board1.getWhiteKnights(), board2.getWhiteKnights());
        Assertions.assertEquals(board1.getWhiteBishops(), board2.getWhiteBishops());
        Assertions.assertEquals(board1.getWhiteRooks(), board2.getWhiteRooks());
        Assertions.assertEquals(board1.getWhiteQueens(), board2.getWhiteQueens());
        Assertions.assertEquals(board1.getWhiteKing(), board2.getWhiteKing());
        Assertions.assertEquals(board1.getBlackPawns(), board2.getBlackPawns());
        Assertions.assertEquals(board1.getBlackKnights(), board2.getBlackKnights());
        Assertions.assertEquals(board1.getBlackBishops(), board2.getBlackBishops());
        Assertions.assertEquals(board1.getBlackRooks(), board2.getBlackRooks());
        Assertions.assertEquals(board1.getBlackQueens(), board2.getBlackQueens());
        Assertions.assertEquals(board1.getBlackKing(), board2.getBlackKing());
        Assertions.assertEquals(board1.getWhitePieces(), board2.getWhitePieces());
        Assertions.assertEquals(board1.getBlackPieces(), board2.getBlackPieces());
        Assertions.assertEquals(board1.getOccupied(), board2.getOccupied());
        Assertions.assertEquals(board1.isWhiteToMove(), board2.isWhiteToMove());

        Assertions.assertEquals(board1.getGameState(), board2.getGameState());

    }

    @Test
    public void testEnPassantFileIsClearedAfterNextMove() {
        // TODO
        Board board1 = new Board();
        board1.makeMove(new Move(13, 21));
        board1.makeMove(new Move(51, 35, Move.PAWN_DOUBLE_MOVE_FLAG));

        new MoveGenerator().generateMoves(board1, false);
    }

    @Test
    public void testGenerateLegalMovesDoesNotCorruptBoard() {

        Board board1 = new Board();
        Board board2 = new Board();

        new MoveGenerator().generateMoves(board1, false);

        Assertions.assertEquals(board1.getWhitePawns(), board2.getWhitePawns());
        Assertions.assertEquals(board1.getWhiteKnights(), board2.getWhiteKnights());
        Assertions.assertEquals(board1.getWhiteBishops(), board2.getWhiteBishops());
        Assertions.assertEquals(board1.getWhiteRooks(), board2.getWhiteRooks());
        Assertions.assertEquals(board1.getWhiteQueens(), board2.getWhiteQueens());
        Assertions.assertEquals(board1.getWhiteKing(), board2.getWhiteKing());
        Assertions.assertEquals(board1.getBlackPawns(), board2.getBlackPawns());
        Assertions.assertEquals(board1.getBlackKnights(), board2.getBlackKnights());
        Assertions.assertEquals(board1.getBlackBishops(), board2.getBlackBishops());
        Assertions.assertEquals(board1.getBlackRooks(), board2.getBlackRooks());
        Assertions.assertEquals(board1.getBlackQueens(), board2.getBlackQueens());
        Assertions.assertEquals(board1.getBlackKing(), board2.getBlackKing());
        Assertions.assertEquals(board1.getWhitePieces(), board2.getWhitePieces());
        Assertions.assertEquals(board1.getBlackPieces(), board2.getBlackPieces());
        Assertions.assertEquals(board1.getOccupied(), board2.getOccupied());
        Assertions.assertEquals(board1.isWhiteToMove(), board2.isWhiteToMove());

        Assertions.assertEquals(board1.getGameState(), board2.getGameState());

    }

    @Test
    public void testUnmakeMoveRestoresCapturedPieces() {

        Board board = new Board();
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
    public void testUnmakeEnPassantRestoresCapturedPawn() {

        Board board = new Board();
        //d4d5
        board.makeMove(new Move(11, 27));
        //e7e5
        board.makeMove(new Move(52, 36));
        //d4e5
        board.makeMove(new Move(27, 36));
        //d7d5
        board.makeMove(new Move(51, 35, Move.PAWN_DOUBLE_MOVE_FLAG));
        //e5d6
        board.makeMove(new Move(36, 43, Move.EN_PASSANT_FLAG));

        Set<Integer> blackPiecePositions = getPiecePositions(board, false);
        Assertions.assertFalse(blackPiecePositions.contains(35));

        board.unmakeMove();
        blackPiecePositions = getPiecePositions(board, false);
        Assertions.assertTrue(blackPiecePositions.contains(35));

    }

    @Test
    public void testUnmakeMoveRemovesCorrectMoveFromMoveHistory() {

        Board board = new Board();
        board.makeMove(new Move(12, 28));
        board.makeMove(new Move(51, 35));
        board.makeMove(new Move(28, 35));

        List<Move> moveHistory = board.getMoveHistory().stream().toList();
        Assertions.assertEquals(3, moveHistory.size());
        Assertions.assertTrue(new Move(28, 35).matches(moveHistory.get(0)));
        Assertions.assertTrue(new Move(51, 35).matches(moveHistory.get(1)));
        Assertions.assertTrue(new Move(12, 28).matches(moveHistory.get(2)));

        board.unmakeMove();

        moveHistory = board.getMoveHistory().stream().toList();
        Assertions.assertEquals(2, moveHistory.size());
        Assertions.assertTrue(new Move(51, 35).matches(moveHistory.get(0)));
        Assertions.assertTrue(new Move(12, 28).matches(moveHistory.get(1)));

    }

    @Test
    public void testUnmakeMoveHandlesTurnSwitching() {

        Board board = new Board();
        Assertions.assertTrue(board.isWhiteToMove());

        board.makeMove(new Move(12, 28));
        Assertions.assertFalse(board.isWhiteToMove());

        board.makeMove(new Move(51, 35));
        Assertions.assertTrue(board.isWhiteToMove());

        board.makeMove(new Move(28, 35));
        Assertions.assertFalse(board.isWhiteToMove());

        board.unmakeMove();
        Assertions.assertTrue(board.isWhiteToMove());

    }

    @Test
    public void testUnmakeMoveHandlesCastling() {

        Board board = new Board();
        board.makeMove(new Move(12, 28));
        board.makeMove(new Move(52, 44));
        board.makeMove(new Move(6, 21));
        board.makeMove(new Move(62, 45));
        board.makeMove(new Move(5, 12));
        board.makeMove(new Move(61, 52));
        // castles
        board.makeMove(new Move(4, 6, Move.CASTLE_FLAG));
        Assertions.assertFalse(board.isWhiteToMove());
        Assertions.assertFalse(board.getGameState().isKingsideCastlingAllowed(true));
        Assertions.assertFalse(board.getGameState().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getGameState().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getGameState().isQueensideCastlingAllowed(false));

        board.unmakeMove();
        Assertions.assertTrue(board.isWhiteToMove());
        Assertions.assertTrue(board.getGameState().isKingsideCastlingAllowed(true));
        Assertions.assertTrue(board.getGameState().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getGameState().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getGameState().isQueensideCastlingAllowed(false));

    }

    @Test
    public void testUnmakeCheckmate() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "d1", "h5"));
        board.makeMove(TestUtils.getLegalMove(board, "b8", "c6"));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "c4"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));
        // scholar's mate
        board.makeMove(TestUtils.getLegalMove(board, "h5", "f7"));
        Assertions.assertFalse(board.isWhiteToMove());
        // todo

    }

    @Test
    public void testRookCannotJumpToOtherSide() {

        String fen = "r1b1k2r/1p3p2/8/8/1n6/2Q5/4P2p/5KNR w kq - 0 1";
        Board board = FEN.fromFEN(fen);
        board.makeMove(TestUtils.getLegalMove(board, "c3", "b4"));
        Move queenPromotion = NotationUtils.fromNotation("h2", "g1", Move.PROMOTE_TO_QUEEN_FLAG);
        board.makeMove(TestUtils.getLegalMove(board, queenPromotion));
        board.makeMove(TestUtils.getLegalMove(board, "f1", "g1"));
        board.makeMove(TestUtils.getLegalMove(board, "h8", "h1"));
        board.makeMove(TestUtils.getLegalMove(board, "g1", "h1"));
        Assertions.assertThrows(IllegalMoveException.class, () ->
                board.makeMove(TestUtils.getLegalMove(board, "h8", "h1")));
    }

    @Test
    public void testKnightMoveGenerationBug() {
        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "b1", "a3"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        List<String> moves = new MoveGenerator().generateMoves(board, false).stream().map(NotationUtils::toNotation).toList();
        System.out.println(moves.size());
        System.out.println(moves);
        Assertions.assertEquals(20, moves.size());
    }

    private Set<Integer> getPiecePositions(Board board, boolean isWhiteToMove) {
        Set<Integer> positions = new HashSet<>();
        if (isWhiteToMove) {
            long whitePieces = board.getWhitePieces();
            while (whitePieces != 0) {
                int position = BitboardUtils.getLSB(whitePieces);
                positions.add(position);
                whitePieces = BitboardUtils.popLSB(whitePieces);
            }
        } else {
            long blackPieces = board.getBlackPieces();
            while (blackPieces != 0) {
                int position = BitboardUtils.getLSB(blackPieces);
                positions.add(position);
                blackPieces = BitboardUtils.popLSB(blackPieces);
            }
        }
        return positions;
    }

    private void assertSinglePieceBoard(Board board, int startSquare) {
        board.toggleSquare(PieceType.ROOK, true, startSquare);
        board.recalculatePieces();
        Assertions.assertEquals(Set.of(startSquare), getPiecePositions(board, true));
        Assertions.assertEquals(Set.of(), getPiecePositions(board, false));
        board.toggleSquare(PieceType.ROOK, true, startSquare);
        board.recalculatePieces();
    }

}
