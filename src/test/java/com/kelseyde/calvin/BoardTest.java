package com.kelseyde.calvin;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitBoardUtils;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.move.MoveType;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoardTest {

    @BeforeEach
    public void beforeEach() {
    }

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
        Assertions.assertTrue(board.getCurrentGameState().isKingsideCastlingAllowed(true));
        Assertions.assertTrue(board.getCurrentGameState().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getCurrentGameState().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getCurrentGameState().isQueensideCastlingAllowed(false));

        board.makeMove(TestUtils.getLegalMove(board, "e2", "e3"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e6"));

        Assertions.assertTrue(board.getCurrentGameState().isKingsideCastlingAllowed(true));
        Assertions.assertTrue(board.getCurrentGameState().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getCurrentGameState().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getCurrentGameState().isQueensideCastlingAllowed(false));

        board.makeMove(TestUtils.getLegalMove(board, "e1", "e2"));

        Assertions.assertFalse(board.getCurrentGameState().isKingsideCastlingAllowed(true));
        Assertions.assertFalse(board.getCurrentGameState().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getCurrentGameState().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getCurrentGameState().isQueensideCastlingAllowed(false));

        Assertions.assertTrue(board.getGameStateHistory().peek().isKingsideCastlingAllowed(true));
        Assertions.assertTrue(board.getGameStateHistory().peek().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getGameStateHistory().peek().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getGameStateHistory().peek().isQueensideCastlingAllowed(false));

        board.makeMove(TestUtils.getLegalMove(board, "f7", "f6"));

        Assertions.assertFalse(board.getCurrentGameState().isKingsideCastlingAllowed(true));
        Assertions.assertFalse(board.getCurrentGameState().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getCurrentGameState().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getCurrentGameState().isQueensideCastlingAllowed(false));

        Assertions.assertFalse(board.getGameStateHistory().peek().isKingsideCastlingAllowed(true));
        Assertions.assertFalse(board.getGameStateHistory().peek().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getGameStateHistory().peek().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getGameStateHistory().peek().isQueensideCastlingAllowed(false));

    }

    @Test
    public void testSimpleUnmakeMove() {
        Board board1 = new Board();
        board1.makeMove(Move.builder().startSquare(12).endSquare(28).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board1.makeMove(Move.builder().startSquare(52).endSquare(36).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board1.makeMove(Move.builder().startSquare(11).endSquare(27).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board1.makeMove(Move.builder().startSquare(51).endSquare(35).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board1.makeMove(Move.builder().startSquare(10).endSquare(26).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board1.makeMove(Move.builder().startSquare(50).endSquare(34).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board1.makeMove(Move.builder().startSquare(9).endSquare(25).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board1.makeMove(Move.builder().startSquare(49).endSquare(33).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board1.makeMove(Move.builder().startSquare(8).endSquare(24).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board1.makeMove(Move.builder().startSquare(48).endSquare(32).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
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

        Assertions.assertEquals(board1.getCurrentGameState(), board2.getCurrentGameState());

    }

    @Test
    public void testEnPassantFileIsClearedAfterNextMove() {
        Board board1 = new Board();
        board1.makeMove(Move.builder().startSquare(13).endSquare(21).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board1.makeMove(Move.builder().startSquare(51).endSquare(35).pieceType(PieceType.PAWN).enPassantFile(4).moveType(MoveType.STANDARD).build());

        new MoveGenerator().generateLegalMoves(board1);
        System.out.println("TODO");
    }

    @Test
    public void testGenerateLegalMovesDoesNotCorruptBoard() {

        Board board1 = new Board();
        Board board2 = new Board();

        new MoveGenerator().generateLegalMoves(board1);

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

        Assertions.assertEquals(board1.getCurrentGameState(), board2.getCurrentGameState());

    }

    @Test
    public void testUnmakeMoveRestoresCapturedPieces() {

        Board board = new Board();
        board.makeMove(Move.builder().startSquare(12).endSquare(28).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board.makeMove(Move.builder().startSquare(51).endSquare(35).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board.makeMove(Move.builder().startSquare(28).endSquare(35).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());

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
        board.makeMove(Move.builder().startSquare(11).endSquare(27).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        //e7e5
        board.makeMove(Move.builder().startSquare(52).endSquare(36).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        //d4e5
        board.makeMove(Move.builder().startSquare(27).endSquare(36).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        //d7d5
        board.makeMove(Move.builder().startSquare(51).endSquare(35).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        //e5d6
        board.makeMove(Move.builder().startSquare(36).endSquare(43).pieceType(PieceType.PAWN).moveType(MoveType.EN_PASSANT).build());

        Set<Integer> blackPiecePositions = getPiecePositions(board, false);
        Assertions.assertFalse(blackPiecePositions.contains(35));

        board.unmakeMove();
        blackPiecePositions = getPiecePositions(board, false);
        Assertions.assertTrue(blackPiecePositions.contains(35));

    }

    @Test
    public void testUnmakeMoveRemovesCorrectMoveFromMoveHistory() {

        Board board = new Board();
        board.makeMove(Move.builder().startSquare(12).endSquare(28).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board.makeMove(Move.builder().startSquare(51).endSquare(35).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board.makeMove(Move.builder().startSquare(28).endSquare(35).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());

        List<Move> moveHistory = board.getMoveHistory().stream().toList();
        Assertions.assertEquals(3, moveHistory.size());
        Assertions.assertTrue(Move.builder().startSquare(28).endSquare(35).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build()
                .matches(moveHistory.get(0)));
        Assertions.assertTrue(Move.builder().startSquare(51).endSquare(35).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build()
                .matches(moveHistory.get(1)));
        Assertions.assertTrue(Move.builder().startSquare(12).endSquare(28).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build()
                .matches(moveHistory.get(2)));

        board.unmakeMove();

        moveHistory = board.getMoveHistory().stream().toList();
        Assertions.assertEquals(2, moveHistory.size());
        Assertions.assertTrue(Move.builder().startSquare(51).endSquare(35).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build()
                .matches(moveHistory.get(0)));
        Assertions.assertTrue(Move.builder().startSquare(12).endSquare(28).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build()
                .matches(moveHistory.get(1)));

    }

    @Test
    public void testUnmakeMoveHandlesTurnSwitching() {

        Board board = new Board();
        Assertions.assertTrue(board.isWhiteToMove());

        board.makeMove(Move.builder().startSquare(28).endSquare(35).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        Assertions.assertFalse(board.isWhiteToMove());

        board.makeMove(Move.builder().startSquare(51).endSquare(35).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        Assertions.assertTrue(board.isWhiteToMove());

        board.makeMove(Move.builder().startSquare(28).endSquare(35).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        Assertions.assertFalse(board.isWhiteToMove());

        board.unmakeMove();
        Assertions.assertTrue(board.isWhiteToMove());

    }

    @Test
    public void testUnmakeMoveHandlesCastling() {

        Board board = new Board();
        board.makeMove(Move.builder().startSquare(28).endSquare(35).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board.makeMove(Move.builder().startSquare(52).endSquare(44).pieceType(PieceType.PAWN).moveType(MoveType.STANDARD).build());
        board.makeMove(Move.builder().startSquare(6).endSquare(21).pieceType(PieceType.KNIGHT).moveType(MoveType.STANDARD).build());
        board.makeMove(Move.builder().startSquare(62).endSquare(45).pieceType(PieceType.KNIGHT).moveType(MoveType.STANDARD).build());
        board.makeMove(Move.builder().startSquare(5).endSquare(12).pieceType(PieceType.BISHOP).moveType(MoveType.STANDARD).build());
        board.makeMove(Move.builder().startSquare(61).endSquare(52).pieceType(PieceType.BISHOP).moveType(MoveType.STANDARD).build());
        // castles
        board.makeMove(Move.builder().startSquare(4).endSquare(6).pieceType(PieceType.KING).moveType(MoveType.STANDARD).build());
        Assertions.assertFalse(board.isWhiteToMove());
        Assertions.assertFalse(board.getCurrentGameState().isKingsideCastlingAllowed(true));
        Assertions.assertFalse(board.getCurrentGameState().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getCurrentGameState().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getCurrentGameState().isQueensideCastlingAllowed(false));

        board.unmakeMove();
        Assertions.assertTrue(board.isWhiteToMove());
        Assertions.assertTrue(board.getCurrentGameState().isKingsideCastlingAllowed(true));
        Assertions.assertTrue(board.getCurrentGameState().isQueensideCastlingAllowed(true));
        Assertions.assertTrue(board.getCurrentGameState().isKingsideCastlingAllowed(false));
        Assertions.assertTrue(board.getCurrentGameState().isQueensideCastlingAllowed(false));

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
    public void test() {
        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "d2", "d4"));
        board.makeMove(TestUtils.getLegalMove(board, "e7", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "d4", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "d7", "d5"));
        List<String> moves = new MoveGenerator().generateLegalMoves(board).stream().map(NotationUtils::toNotation).toList();
        Assertions.assertEquals(31, moves.size());
        Assertions.assertTrue(moves.contains("e1d2"));
    }

    private Set<Integer> getPiecePositions(Board board, boolean isWhiteToMove) {
        Set<Integer> positions = new HashSet<>();
        if (isWhiteToMove) {
            long whitePieces = board.getWhitePieces();
            while (whitePieces != 0) {
                int position = BitBoardUtils.scanForward(whitePieces);
                positions.add(position);
                whitePieces = BitBoardUtils.popLSB(whitePieces);
            }
        } else {
            long blackPieces = board.getBlackPieces();
            while (blackPieces != 0) {
                int position = BitBoardUtils.scanForward(blackPieces);
                positions.add(position);
                blackPieces = BitBoardUtils.popLSB(blackPieces);
            }
        }
        return positions;
    }

    private void assertSinglePieceBoard(Board board, int startSquare) {
        board.setPiece(startSquare, PieceType.ROOK, true, true);
        Assertions.assertEquals(Set.of(startSquare), getPiecePositions(board, true));
        Assertions.assertEquals(Set.of(), getPiecePositions(board, false));
        board.unsetPiece(startSquare, true);
    }

}
