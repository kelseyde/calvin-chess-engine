package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.NotationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PawnMoveGeneratorTest {

    private final MoveGenerator generator = new MoveGenerator();

    private Board board;

    @BeforeEach
    public void beforeEach() {
        board = TestUtils.emptyBoard();
    }

    @Test
    public void testStartingPositions() {

        board = new Board();
        Set<Move> expected = Set.of(
                new Move(8, 16),
                new Move(8, 24),
                new Move(9, 17),
                new Move(9, 25),
                new Move(10, 18),
                new Move(10, 26),
                new Move(11, 19),
                new Move(11, 27),
                new Move(12, 20),
                new Move(12, 28),
                new Move(13, 21),
                new Move(13, 29),
                new Move(14, 22),
                new Move(14, 30),
                new Move(15, 23),
                new Move(15, 31)
        );
        assertMoves(board, expected);

    }

    @Test
    public void testWhitePawnCannotMoveThroughPiece() {

        board = TestUtils.emptyBoard();
        board.toggleSquare(PieceType.KING, true, 0);
        board.toggleSquare(PieceType.KING, false, 63);
        board.toggleSquare(PieceType.ROOK, true, 16);
        assertMovesFromSquare(board, 8, Set.of());
        board.toggleSquare(PieceType.ROOK, true, 16);

        board.toggleSquare(PieceType.PAWN, false, 17);
        assertMovesFromSquare(board, 9, Set.of());
        board.toggleSquare(PieceType.PAWN, true, 17);

        board.toggleSquare(PieceType.KING, true, 18);
        assertMovesFromSquare(board, 10, Set.of());
        board.toggleSquare(PieceType.KING, true, 8);

        board.toggleSquare(PieceType.QUEEN, false, 19);
        assertMovesFromSquare(board, 11, Set.of());
        board.toggleSquare(PieceType.QUEEN, true, 19);

        board.toggleSquare(PieceType.BISHOP, false, 44);
        assertMovesFromSquare(board, 52, Set.of());
        board.toggleSquare(PieceType.BISHOP, true, 44);

        board.toggleSquare(PieceType.PAWN, false, 45);
        assertMovesFromSquare(board, 53, Set.of());
        board.toggleSquare(PieceType.PAWN, true, 45);

        board.toggleSquare(PieceType.KNIGHT, false, 46);
        assertMovesFromSquare(board, 54, Set.of());
        board.toggleSquare(PieceType.KNIGHT, true, 46);

        board.toggleSquare(PieceType.ROOK, false, 47);
        assertMovesFromSquare(board, 55, Set.of());
        board.toggleSquare(PieceType.ROOK, true, 47);

    }


    @Test
    public void testWhitePawnsNotOnStartingSquares() {

        board = TestUtils.emptyBoard();
        board.toggleSquare(PieceType.KING, true, 0);
        board.toggleSquare(PieceType.KING, false, 63);

        board.toggleSquare(PieceType.PAWN, true, 16);
        assertMovesFromSquare(board, 16, Set.of(new Move(16, 24)));
        board.toggleSquare(PieceType.PAWN, true, 16);

        board.toggleSquare(PieceType.PAWN, true, 34);
        assertMovesFromSquare(board, 34, Set.of(new Move(34, 42)));
        board.toggleSquare(PieceType.PAWN, true, 34);

        board.toggleSquare(PieceType.PAWN, true, 45);
        assertMovesFromSquare(board, 45, Set.of(new Move(45, 53)));
        board.toggleSquare(PieceType.PAWN, true, 45);

    }

    @Test
    public void testBlackPawnsNotOnStartingSquares() {

        board = TestUtils.emptyBoard();
        board.setWhiteToMove(false);

        board.toggleSquare(PieceType.KING, true, 0);
        board.toggleSquare(PieceType.KING, false, 63);
        board.toggleSquare(PieceType.PAWN, false, 45);
        assertMovesFromSquare(board, 45, Set.of(new Move(45, 37)));
        board.toggleSquare(PieceType.PAWN, true, 45);

        board.toggleSquare(PieceType.PAWN, false, 35);
        assertMovesFromSquare(board, 35, Set.of(new Move(35, 27)));
        board.toggleSquare(PieceType.PAWN, true, 35);

        board.toggleSquare(PieceType.PAWN, false, 31);
        assertMovesFromSquare(board, 31, Set.of(new Move(31, 23)));
        board.toggleSquare(PieceType.PAWN, true, 31);

    }

    @Test
    public void testWhitePawnCaptures() {

        String fen = "k7/8/8/8/8/p1p5/1P6/K7 w - - 0 1";
        board = FEN.fromFEN(fen);
        assertMovesFromSquare(board, 9,
                Set.of(new Move(9, 16), new Move(9, 17), new Move(9, 18), new Move(9, 25)));

        fen = "k7/8/p7/6p1/7P/8/8/K7 w - - 0 1";
        board = FEN.fromFEN(fen);
        assertMovesFromSquare(board, 31,
                Set.of(new Move(31, 38), new Move(31, 39)));

        fen = "k7/p1p5/1P6/8/8/8/8/K7 w - - 0 1";
        board = FEN.fromFEN(fen);
        assertMovesFromSquare(board, 41,
                Set.of(new Move(41, 48),
                        new Move(41, 49),
                        new Move(41, 50)
                ));

    }

    @Test
    public void testBlackPawnCaptures() {

        String fen = "k7/1p6/P1P5/8/8/8/8/K7 b - - 0 1";
        board = FEN.fromFEN(fen);
        assertMovesFromSquare(board, 49, Set.of(new Move(49, 40), new Move(49, 41), new Move(49, 42), new Move(49, 33)));

        fen = "k7/8/8/p7/1P5P/8/8/K7 b - - 0 1";
        board = FEN.fromFEN(fen);
        // should not capture the wrapped piece
        assertMovesFromSquare(board, 32, Set.of(new Move(32, 24), new Move(32, 25)));

    }

    @Test
    public void testPawnCannotCaptureEmptySpace() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "f2", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "d7", "d5"));
        Set<Move> legalMoves = new HashSet<>(new MoveGenerator().generateMoves(board, false));
        Move emptySpaceCaptureLeft = new Move(21, 28);
        Move emptySpaceCaptureRight = new Move(21, 30);
        Assertions.assertTrue(legalMoves.stream().noneMatch(emptySpaceCaptureLeft::matches));
        Assertions.assertTrue(legalMoves.stream().noneMatch(emptySpaceCaptureRight::matches));

    }

    @Test
    public void testWhiteEnPassant() {

        board = TestUtils.emptyBoard();
        board.toggleSquare(PieceType.KING, true, 0);
        board.toggleSquare(PieceType.KING, false, 63);

        board.toggleSquare(PieceType.PAWN, true, 35);
        board.toggleSquare(PieceType.PAWN, false, 34);
        board.getGameState().setEnPassantFile(BoardUtils.getFile(42));

        List<Move> legalWhiteMoves = generator.generateMoves(board, false).stream()
                .filter(m -> board.pieceAt(m.getStartSquare()) == PieceType.PAWN)
                .toList();

        Move standardMove = new Move(35, 43);
        Move enPassantCapture = new Move(35, 42, Move.EN_PASSANT_FLAG);

        List<Move> expectedLegalMoves = List.of(standardMove, enPassantCapture);

        Assertions.assertTrue(expectedLegalMoves.size() == legalWhiteMoves.size() && expectedLegalMoves.containsAll(legalWhiteMoves));

    }

    @Test
    public void testWhiteEnPassantWithOtherCapture() {

        String fen = "k7/4p3/2q5/3P4/8/8/8/K7 b - - 0 1";
        board = FEN.fromFEN(fen);

        board.makeMove(TestUtils.getLegalMove(board, NotationUtils.fromNotation("e7", "e5")));

        List<Move> legalWhiteMoves = generator.generateMoves(board, false).stream()
                .filter(move -> board.pieceAt(move.getStartSquare()) == PieceType.PAWN)
                .toList();

        Move standardMove = new Move(35, 43);
        Move standardCapture = new Move(35, 42);
        Move enPassantCapture = new Move(35, 44, Move.EN_PASSANT_FLAG);

        List<Move> expectedLegalMoves = List.of(standardMove, standardCapture, enPassantCapture);

        Assertions.assertTrue(expectedLegalMoves.size() == legalWhiteMoves.size() && expectedLegalMoves.containsAll(legalWhiteMoves));

    }

    @Test
    public void testWhiteDoubleEnPassantIsImpossible() {

        board = TestUtils.emptyBoard();
        board.toggleSquare(PieceType.KING, true, 0);
        board.toggleSquare(PieceType.KING, false, 63);

        board.toggleSquare(PieceType.PAWN, true, 35);
        // we need another white piece to spend a move in between black's pawn moves
        board.toggleSquare(PieceType.ROOK, true, 0);
        // two black pawns on starting positions
        board.toggleSquare(PieceType.PAWN, false, 50);
        board.toggleSquare(PieceType.PAWN, false, 52);

        board.setWhiteToMove(false);

        // black first double pawn advance
        board.makeMove(new Move(50, 34, Move.PAWN_DOUBLE_MOVE_FLAG));

        // white wastes a move with rook
        board.makeMove(new Move(0, 8));

        // black second double pawn advance
        board.makeMove(new Move(52, 36, Move.PAWN_DOUBLE_MOVE_FLAG));

        List<Move> legalWhiteMoves = generator.generateMoves(board, false).stream()
                .filter(move -> board.pieceAt(move.getStartSquare()) == PieceType.PAWN)
                .toList();

        Move standardMove = new Move(35, 43);
        Move enPassantCapture = new Move(35, 44, Move.EN_PASSANT_FLAG);

        List<Move> expectedLegalMoves = List.of(standardMove, enPassantCapture);

        Assertions.assertTrue(expectedLegalMoves.size() == legalWhiteMoves.size() && expectedLegalMoves.equals(legalWhiteMoves));

    }

    @Test
    public void testBlackEnPassant() {
        board.toggleSquare(PieceType.KING, true, 0);
        board.toggleSquare(PieceType.KING, false, 63);

        board.toggleSquare(PieceType.PAWN, false, 29);
        board.toggleSquare(PieceType.PAWN, true, 30);
        board.getGameState().setEnPassantFile(BoardUtils.getFile(22));
        board.setWhiteToMove(false);

        List<Move> legalBlackMoves = generator.generateMoves(board, false).stream()
                .filter(move -> board.pieceAt(move.getStartSquare()) == PieceType.PAWN)
                .toList();

        Move standardMove = new Move(29, 21);
        Move enPassantCapture = new Move(29, 22, Move.EN_PASSANT_FLAG);

        List<Move> expectedLegalMoves = List.of(standardMove, enPassantCapture);
        Assertions.assertTrue(expectedLegalMoves.size() == legalBlackMoves.size() && expectedLegalMoves.containsAll(legalBlackMoves));


    }

    @Test
    public void testBlackEnPassantWithOtherCapture() {

        String fen = "k7/8/8/8/5p2/6P1/4P3/K7 w - - 0 1";
        Board board = FEN.fromFEN(fen);
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));

        List<Move> legalBlackMoves = generator.generateMoves(board, false).stream()
                .filter(move -> board.pieceAt(move.getStartSquare()) == PieceType.PAWN)
                .toList();

        Move standardMove = new Move(29, 21);
        Move standardCapture = new Move(29, 22);
        Move enPassantCapture = new Move(29, 20, Move.EN_PASSANT_FLAG);
        List<Move> expectedMoves = List.of(standardMove, standardCapture, enPassantCapture);
        Assertions.assertTrue(expectedMoves.size() == legalBlackMoves.size() && expectedMoves.containsAll(legalBlackMoves));

    }

    @Test
    public void testBlackDoubleEnPassantIsImpossible() {

        board.toggleSquare(PieceType.KING, true, 12);
        board.toggleSquare(PieceType.KING, false, 15);

        board.toggleSquare(PieceType.PAWN, false, 25);
        // we need another black piece to spend a move in between black's pawn moves
        board.toggleSquare(PieceType.ROOK, false, 63);
        // two black pawns on starting positions
        board.toggleSquare(PieceType.PAWN, true, 8);
        board.toggleSquare(PieceType.PAWN, true, 10);
        board.setWhiteToMove(true);

        // white first double pawn advance
        board.makeMove(new Move(10, 26, Move.PAWN_DOUBLE_MOVE_FLAG));

        // black wastes move with rook
        board.makeMove(new Move(63, 62));

        // second double pawn move from white, should make the first en-passant capture impossible
        board.makeMove(new Move(8, 24, Move.PAWN_DOUBLE_MOVE_FLAG));

        List<Move> legalBlackMoves = generator.generateMoves(board, false).stream()
                .filter(move -> board.pieceAt(move.getStartSquare()) == PieceType.PAWN)
                .toList();

        Move standardMove = new Move(25, 17);
        Move enPassantCapture = new Move(25, 16, Move.EN_PASSANT_FLAG);

        List<Move> expectedLegalMoves = List.of(standardMove, enPassantCapture);

        Assertions.assertTrue(expectedLegalMoves.size() == legalBlackMoves.size() && expectedLegalMoves.containsAll(legalBlackMoves));

    }

    @Test
    public void testEnPassantRemovesCapturedPawn() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));
        board.makeMove(TestUtils.getLegalMove(board, "g8", "f6"));
        board.makeMove(TestUtils.getLegalMove(board, "e4", "e5"));
        board.makeMove(TestUtils.getLegalMove(board, "d7", "d5"));
        //en passant
        board.makeMove(TestUtils.getLegalMove(board, "e5", "d6"));

        long d5Bitboard = 1L << 35;
        // Assert d5 is empty
        Assertions.assertEquals(0, (board.getOccupied() & d5Bitboard));

    }

    @Test
    public void testWhiteStandardPromotion() {
        board = TestUtils.emptyBoard();
        board.toggleSquare(PieceType.KING, true, 0);
        board.toggleSquare(PieceType.KING, false, 63);
        board.toggleSquare(PieceType.PAWN, true, 51);
        List<Move> legalMoves = generator.generateMoves(board, false).stream()
                .filter(move -> board.pieceAt(move.getStartSquare()) == PieceType.PAWN)
                .toList();
        List<Move> expectedLegalMoves = List.of(
                new Move(51, 59, Move.PROMOTE_TO_QUEEN_FLAG),
                new Move(51, 59, Move.PROMOTE_TO_ROOK_FLAG),
                new Move(51, 59, Move.PROMOTE_TO_BISHOP_FLAG),
                new Move(51, 59, Move.PROMOTE_TO_KNIGHT_FLAG));
        Assertions.assertTrue(expectedLegalMoves.size() == legalMoves.size() && expectedLegalMoves.containsAll(legalMoves));

    }

    @Test
    public void testBlackStandardPromotion() {
        board.toggleSquare(PieceType.KING, true, 61);
        board.toggleSquare(PieceType.KING, false, 63);
        board.toggleSquare(PieceType.PAWN, false, 8);
        board.setWhiteToMove(false);
        List<Move> legalMoves = generator.generateMoves(board, false).stream()
                .filter(move -> board.pieceAt(move.getStartSquare()) == PieceType.PAWN)
                .toList();
        List<Move> expectedLegalMoves = List.of(
                new Move(8, 0, Move.PROMOTE_TO_QUEEN_FLAG),
                new Move(8, 0, Move.PROMOTE_TO_ROOK_FLAG),
                new Move(8, 0, Move.PROMOTE_TO_BISHOP_FLAG),
                new Move(8, 0, Move.PROMOTE_TO_KNIGHT_FLAG));
        Assertions.assertTrue(expectedLegalMoves.size() == legalMoves.size() && expectedLegalMoves.containsAll(legalMoves));

    }

    @Test
    public void testWhiteCapturePromotion() {
        String fen = "k1q5/3P4/8/8/8/8/8/K7 w - - 0 1";
        board = FEN.fromFEN(fen);
        List<Move> legalMoves = generator.generateMoves(board, false).stream()
                .filter(move -> board.pieceAt(move.getStartSquare()) == PieceType.PAWN)
                .toList();
        List<Move> expectedLegalMoves = List.of(
                new Move(51, 58, Move.PROMOTE_TO_QUEEN_FLAG),
                new Move(51, 58, Move.PROMOTE_TO_ROOK_FLAG),
                new Move(51, 58, Move.PROMOTE_TO_BISHOP_FLAG),
                new Move(51, 58, Move.PROMOTE_TO_KNIGHT_FLAG),
                new Move(51, 59, Move.PROMOTE_TO_QUEEN_FLAG),
                new Move(51, 59, Move.PROMOTE_TO_ROOK_FLAG),
                new Move(51, 59, Move.PROMOTE_TO_BISHOP_FLAG),
                new Move(51, 59, Move.PROMOTE_TO_KNIGHT_FLAG));
        Assertions.assertTrue(expectedLegalMoves.size() == legalMoves.size() && expectedLegalMoves.containsAll(legalMoves));

    }

    @Test
    public void testBlackCapturePromotion() {
        String fen = "k7/8/8/8/8/8/7p/K5B1 b - - 0 1";
        board = FEN.fromFEN(fen);
        List<Move> legalMoves = generator.generateMoves(board, false).stream()
                .filter(move -> board.pieceAt(move.getStartSquare()) == PieceType.PAWN)
                .toList();
        List<Move> expectedLegalMoves = List.of(
                new Move(15, 7, Move.PROMOTE_TO_QUEEN_FLAG),
                new Move(15, 7, Move.PROMOTE_TO_ROOK_FLAG),
                new Move(15, 7, Move.PROMOTE_TO_BISHOP_FLAG),
                new Move(15, 7, Move.PROMOTE_TO_KNIGHT_FLAG),
                new Move(15, 6, Move.PROMOTE_TO_QUEEN_FLAG),
                new Move(15, 6, Move.PROMOTE_TO_ROOK_FLAG),
                new Move(15, 6, Move.PROMOTE_TO_BISHOP_FLAG),
                new Move(15, 6, Move.PROMOTE_TO_KNIGHT_FLAG));
        Assertions.assertTrue(expectedLegalMoves.size() == legalMoves.size() && expectedLegalMoves.containsAll(legalMoves));
    }

    @Test
    public void testPawnCannotWrapCaptureAroundBoard() {

        String fen = "4k3/8/8/8/n7/7P/8/4K3 w - - 0 1";

        Board board = FEN.fromFEN(fen);

        List<Move> legalMoves = generator.generateMoves(board, false).stream()
                .filter(move -> board.pieceAt(move.getStartSquare()) == PieceType.PAWN)
                .toList();
        List<Move> expectedLegalMoves = List.of(new Move(23, 31));
        Assertions.assertEquals(1, legalMoves.size());
        Assertions.assertEquals(expectedLegalMoves, legalMoves);

    }

    private void assertMoves(Board board, Set<Move> expected) {
        List<Move> actual = generator.generateMoves(board, false).stream()
                .filter(move -> board.pieceAt(move.getStartSquare()) == PieceType.PAWN)
                .toList();

        Assertions.assertEquals(
                expected.stream().map(m -> m.getStartSquare() + m.getEndSquare()).collect(Collectors.toSet()),
                actual.stream().map(m -> m.getStartSquare() + m.getEndSquare()).collect(Collectors.toSet()));
    }

    private void assertMovesFromSquare(Board board, int square, Set<Move> expected) {
        List<Move> actual = generator.generateMoves(board, false).stream()
                .filter(move -> move.getStartSquare() == square)
                .toList();
        Assertions.assertEquals(
                expected.stream().map(m -> m.getStartSquare() + m.getEndSquare()).collect(Collectors.toSet()),
                actual.stream().map(m -> m.getStartSquare() + m.getEndSquare()).collect(Collectors.toSet()));
    }

}