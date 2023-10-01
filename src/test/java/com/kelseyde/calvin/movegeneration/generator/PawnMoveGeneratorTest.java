package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.move.MoveType;
import com.kelseyde.calvin.board.piece.Piece;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class PawnMoveGeneratorTest {

    private final PawnMoveGenerator generator = new PawnMoveGenerator();

    private Board board;

    @BeforeEach
    public void beforeEach() {
        board = TestUtils.emptyBoard();
    }

    @Test
    public void testStartingPositions() {

        board = new Board();
        Set<Move> expected = Set.of(
                moveBuilder(8, 16).build(), moveBuilder(8, 24).build(),
                moveBuilder(9, 17).build(), moveBuilder(9, 25).build(),
                moveBuilder(10, 18).build(), moveBuilder(10, 26).build(),
                moveBuilder(11, 19).build(), moveBuilder(11, 27).build(),
                moveBuilder(12, 20).build(), moveBuilder(12, 28).build(),
                moveBuilder(13, 21).build(), moveBuilder(13, 29).build(),
                moveBuilder(14, 22).build(), moveBuilder(14, 30).build(),
                moveBuilder(15, 23).build(), moveBuilder(15, 31).build()
        );
        assertMoves(board, expected);

    }

    @Test
    public void testWhitePawnCannotMoveThroughPiece() {

        board = TestUtils.emptyBoard();
        board.setPiece(16, PieceType.ROOK, true, true);
        assertMovesFromSquare(board, 8, Set.of());
        board.unsetPiece(16);

        board.setPiece(17, PieceType.PAWN, false, true);
        assertMovesFromSquare(board, 9, Set.of());
        board.unsetPiece(17);

        board.setPiece(18, PieceType.KING, true, true);
        assertMovesFromSquare(board, 10, Set.of());
        board.unsetPiece(18);

        board.setPiece(19, PieceType.QUEEN, false, true);
        assertMovesFromSquare(board, 11, Set.of());
        board.unsetPiece(19);

        board.setPiece(44, PieceType.BISHOP, false, true);
        assertMovesFromSquare(board, 52, Set.of());
        board.unsetPiece(44);

        board.setPiece(45, PieceType.PAWN, false, true);
        assertMovesFromSquare(board, 53, Set.of());
        board.unsetPiece(45);

        board.setPiece(46, PieceType.KNIGHT, false, true);
        assertMovesFromSquare(board, 54, Set.of());
        board.unsetPiece(46);

        board.setPiece(47, PieceType.ROOK, false, true);
        assertMovesFromSquare(board, 55, Set.of());
        board.unsetPiece(47);

    }


    @Test
    public void testWhitePawnsNotOnStartingSquares() {

        board = TestUtils.emptyBoard();
        board.setPiece(16, PieceType.PAWN, true, true);
        assertMovesFromSquare(board, 16, Set.of(moveBuilder(16, 24).build()));
        board.unsetPiece(16);

        board.setPiece(34, PieceType.PAWN, true, true);
        assertMovesFromSquare(board, 34, Set.of(moveBuilder(34, 42).build()));
        board.unsetPiece(34);

        board.setPiece(45, PieceType.PAWN, true, true);
        assertMovesFromSquare(board, 45, Set.of(moveBuilder(45, 53).build()));
        board.unsetPiece(45);

    }

    @Test
    public void testBlackPawnsNotOnStartingSquares() {

        board = TestUtils.emptyBoard();
        board.setWhiteToMove(false);

        board.setPiece(45, PieceType.PAWN, false, true);
        assertMovesFromSquare(board, 45, Set.of(moveBuilder(45, 37).build()));
        board.unsetPiece(45);

        board.setPiece(35, PieceType.PAWN, false, true);
        assertMovesFromSquare(board, 35, Set.of(moveBuilder(35, 27).build()));
        board.unsetPiece(35);

        board.setPiece(31, PieceType.PAWN, false, true);
        assertMovesFromSquare(board, 31, Set.of(moveBuilder(31, 23).build()));
        board.unsetPiece(31);

    }

    @Test
    public void testWhitePawnCaptures() {

        board = TestUtils.emptyBoard();
        board.setPiece(9, PieceType.PAWN, true, true);
        board.setPiece(16, PieceType.PAWN, false, true);
        board.setPiece(18, PieceType.PAWN, false, true);
        assertMovesFromSquare(board, 9,
                Set.of(moveBuilder(9, 16).build(), moveBuilder(9, 17).build(), moveBuilder(9, 18).build(), moveBuilder(9, 25).build()));

        board.setPiece(31, PieceType.PAWN, true, true);
        board.setPiece(38, PieceType.PAWN, false, true);
        // should not capture the wrapped piece
        board.setPiece(40, PieceType.PAWN, false, true);
        assertMovesFromSquare(board, 31,
                Set.of(moveBuilder(31, 38).build(), moveBuilder(31, 39).build()));

        board.setPiece(41, PieceType.PAWN, true, true);
        board.setPiece(48, PieceType.PAWN, false, true);
        // should not capture white pieces
        board.setPiece(50, PieceType.ROOK, true, true);
        assertMovesFromSquare(board, 41,
                Set.of(moveBuilder(41, 48).build(),
                        moveBuilder(41, 49).build()));

    }

    @Test
    public void testBlackPawnCaptures() {

        board = TestUtils.emptyBoard();
        board.setWhiteToMove(false);

        board.setPiece(49, PieceType.PAWN, false, true);
        board.setPiece(40, PieceType.PAWN, true, true);
        board.setPiece(42, PieceType.PAWN, true, true);
        assertMovesFromSquare(board, 49,
                Set.of(moveBuilder(49, 40).build(), moveBuilder(49, 41).build(), moveBuilder(49, 42).build(), moveBuilder(49, 33).build()));

        board.setPiece(32, PieceType.PAWN, false, true);
        board.setPiece(25, PieceType.PAWN, true, true);
        // should not capture the wrapped piece
        board.setPiece(23, PieceType.PAWN, true, true);
        assertMovesFromSquare(board, 32,
                Set.of(moveBuilder(32, 24).build(), moveBuilder(32, 25).build()));

    }

    @Test
    public void testPawnCannotCaptureEmptySpace() {

        Board board = new Board();
        board.makeMove(TestUtils.getLegalMove(board, "f2", "f3"));
        board.makeMove(TestUtils.getLegalMove(board, "d7", "d5"));
        Set<Move> legalMoves = Arrays.stream(new MoveGenerator().generateLegalMoves(board, false)).collect(Collectors.toSet());
        Move emptySpaceCaptureLeft = moveBuilder(21, 28).build();
        Move emptySpaceCaptureRight = moveBuilder(21, 30).build();
        Assertions.assertTrue(legalMoves.stream().noneMatch(emptySpaceCaptureLeft::matches));
        Assertions.assertTrue(legalMoves.stream().noneMatch(emptySpaceCaptureRight::matches));

    }

    @Test
    public void testWhiteEnPassant() {

        board = TestUtils.emptyBoard();
        board.setPiece(35, PieceType.PAWN, true, true);
        board.setPiece(34, PieceType.PAWN, false, true);
        board.getGameState().setEnPassantFile(BoardUtils.getFile(42));

        Set<Move> legalWhiteMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(35, 43).build();
        Move enPassantCapture = moveBuilder(35, 42).moveType(MoveType.EN_PASSANT).build();

        Assertions.assertEquals(Set.of(standardMove, enPassantCapture), legalWhiteMoves);

    }

    @Test
    public void testWhiteEnPassantWithOtherCapture() {

        board = TestUtils.emptyBoard();
        board.setPiece(35, PieceType.PAWN, true, true);
        board.setPiece(52, PieceType.PAWN, false, true);
        board.setPiece(42, PieceType.QUEEN, false, true);
        board.getGameState().setEnPassantFile(BoardUtils.getFile(44));

        Set<Move> legalWhiteMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(35, 43).build();
        Move standardCapture = moveBuilder(35, 42).build();
        Move enPassantCapture = moveBuilder(35, 44)
                .moveType(MoveType.EN_PASSANT).build();

        Assertions.assertEquals(Set.of(standardMove, standardCapture, enPassantCapture), legalWhiteMoves);

    }

    @Test
    public void testWhiteDoubleEnPassantIsImpossible() {

        board = TestUtils.emptyBoard();

        board.setPiece(35, PieceType.PAWN, true, true);
        // we need another white piece to spend a move in between black's pawn moves
        board.setPiece(0, PieceType.ROOK, true, true);
        // two black pawns on starting positions
        board.setPiece(50, PieceType.PAWN, false, true);
        board.setPiece(52, PieceType.PAWN, false, true);

        board.setWhiteToMove(false);

        // black first double pawn advance
        board.makeMove(moveBuilder(50, 34).enPassantFile(2).build());

        // white wastes a move with rook
        board.makeMove(moveBuilder(0, 8).pieceType(PieceType.ROOK).build());

        // black second double pawn advance
        board.makeMove(moveBuilder(52, 36).enPassantFile(4).build());

        Set<Move> legalWhiteMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(35, 43)
                .moveType(MoveType.STANDARD)
                .build();
        Move enPassantCapture = moveBuilder(35, 44)
                .moveType(MoveType.EN_PASSANT).build();

        Assertions.assertEquals(Set.of(standardMove, enPassantCapture), legalWhiteMoves);

    }

    @Test
    public void testBlackEnPassant() {

        board.setPiece(29, PieceType.PAWN, false, true);
        board.setPiece(30, PieceType.PAWN, true, true);
        board.getGameState().setEnPassantFile(BoardUtils.getFile(22));
        board.setWhiteToMove(false);

        Set<Move> legalBlackMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(29, 21).build();
        Move enPassantCapture = moveBuilder(29, 22).moveType(MoveType.EN_PASSANT).build();

        Assertions.assertEquals(Set.of(standardMove, enPassantCapture), legalBlackMoves);

    }

    @Test
    public void testBlackEnPassantWithOtherCapture() {

        board.setPiece(29, PieceType.PAWN, false, true);
        board.setPiece(28, PieceType.PAWN, true, true);
        board.setPiece(22, PieceType.ROOK, true, true);
        board.getGameState().setEnPassantFile(BoardUtils.getFile(20));
        board.setWhiteToMove(false);

        Set<Move> legalBlackMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(29, 21).build();
        Move standardCapture = moveBuilder(29, 22).build();
        Move enPassantCapture = moveBuilder(29, 20).moveType(MoveType.EN_PASSANT).startSquare(29).endSquare(20).build();
        Assertions.assertEquals(Set.of(standardMove, standardCapture, enPassantCapture), legalBlackMoves);

    }

    @Test
    public void testBlackDoubleEnPassantIsImpossible() {

        board.setPiece(25, PieceType.PAWN, false, true);
        // we need another black piece to spend a move in between black's pawn moves
        board.setPiece(63, PieceType.ROOK, false, true);
        // two black pawns on starting positions
        board.setPiece(8, PieceType.PAWN, true, true);
        board.setPiece(10, PieceType.PAWN, true, true);
        board.setWhiteToMove(true);

        // white first double pawn advance
        board.makeMove(moveBuilder(10, 26).enPassantFile(2).build());

        // black wastes move with rook
        board.makeMove(moveBuilder(63, 62).pieceType(PieceType.ROOK).build());

        // second double pawn move from white, should make the first en-passant capture impossible
        board.makeMove(moveBuilder(8, 24).enPassantFile(0).build());

        Set<Move> legalBlackMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(25, 17).build();
        Move enPassantCapture = moveBuilder(25, 16).moveType(MoveType.EN_PASSANT).build();

        Assertions.assertEquals(Set.of(standardMove, enPassantCapture), legalBlackMoves);

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
        board.setPiece(51, PieceType.PAWN, true, true);
        Set<Move> legalMoves = generator.generatePseudoLegalMoves(board);
        Assertions.assertEquals(
                Set.of(moveBuilder(51, 59).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.QUEEN).build(),
                        moveBuilder(51, 59).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.ROOK).build(),
                        moveBuilder(51, 59).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.BISHOP).build(),
                        moveBuilder(51, 59).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.KNIGHT).build()),
                legalMoves
        );
    }

    @Test
    public void testBlackStandardPromotion() {
        board.setPiece(8, PieceType.PAWN, false, true);
        board.setWhiteToMove(false);
        Set<Move> legalMoves = generator.generatePseudoLegalMoves(board);
        Assertions.assertEquals(
                Set.of(moveBuilder(8, 0).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.QUEEN).build(),
                        moveBuilder(8, 0).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.ROOK).build(),
                        moveBuilder(8, 0).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.BISHOP).build(),
                        moveBuilder(8, 0).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.KNIGHT).build()),
                legalMoves
        );
    }

    @Test
    public void testWhiteCapturePromotion() {
        board.setPiece(51, PieceType.PAWN, true, true);
        board.setPiece(58, PieceType.QUEEN, false, true);
        Set<Move> legalMoves = generator.generatePseudoLegalMoves(board);
        Assertions.assertEquals(Set.of(
                moveBuilder(51, 59).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.QUEEN).build(),
                moveBuilder(51, 59).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.ROOK).build(),
                moveBuilder(51, 59).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.BISHOP).build(),
                moveBuilder(51, 59).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.KNIGHT).build(),
                moveBuilder(51, 58).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.QUEEN).build(),
                moveBuilder(51, 58).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.ROOK).build(),
                moveBuilder(51, 58).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.BISHOP).build(),
                moveBuilder(51, 58).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.KNIGHT).build()),
                legalMoves
        );
    }

    @Test
    public void testBlackCapturePromotion() {
        board.setPiece(15, PieceType.PAWN, false, true);
        board.setPiece(6, PieceType.BISHOP, true, true);
        board.setWhiteToMove(false);
        Set<Move> legalMoves = generator.generatePseudoLegalMoves(board);
        Set<Move> expectedLegalMoves = Set.of(
                moveBuilder(15, 7).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.QUEEN).build(),
                moveBuilder(15, 7).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.ROOK).build(),
                moveBuilder(15, 7).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.BISHOP).build(),
                moveBuilder(15, 7).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.KNIGHT).build(),
                moveBuilder(15, 6).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.QUEEN).build(),
                moveBuilder(15, 6).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.ROOK).build(),
                moveBuilder(15, 6).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.BISHOP).build(),
                moveBuilder(15, 6).moveType(MoveType.PROMOTION).promotionPieceType(PieceType.KNIGHT).build());
        Assertions.assertEquals(expectedLegalMoves, legalMoves);
    }

    @Test
    public void testPawnCannotWrapCaptureAroundBoard() {

        String fen = "4k3/8/8/8/n7/7P/8/4K3 w - - 0 1";

        Board board = FEN.fromFEN(fen);

        Set<Move> legalMoves = generator.generatePseudoLegalMoves(board);
        Set<Move> expectedLegalMoves = Set.of(moveBuilder(23, 31).build());
        Assertions.assertEquals(1, legalMoves.size());
        Assertions.assertEquals(expectedLegalMoves, legalMoves);

    }

    private void assertMoves(Board board, Set<Move> expected) {
        Set<Move> actual = generator.generatePseudoLegalMoves(board);

        Assertions.assertEquals(
                expected.stream().map(m -> m.getPieceType().toString() + m.getStartSquare() + m.getEndSquare()).collect(Collectors.toSet()),
                actual.stream().map(m -> m.getPieceType().toString() + m.getStartSquare() + m.getEndSquare()).collect(Collectors.toSet()));
    }

    private void assertMovesFromSquare(Board board, int square, Set<Move> expected) {
        Set<Move> actual = generator.generatePseudoLegalMoves(board).stream()
                .filter(move -> move.getStartSquare() == square)
                .collect(Collectors.toSet());
        Assertions.assertEquals(
                expected.stream().map(m -> m.getPieceType().toString() + m.getStartSquare() + m.getEndSquare()).collect(Collectors.toSet()),
                actual.stream().map(m -> m.getPieceType().toString() + m.getStartSquare() + m.getEndSquare()).collect(Collectors.toSet()));
    }
    
    private Move.MoveBuilder moveBuilder(int startSquare, int endSquare) {
        return Move.builder()
                .startSquare(startSquare)
                .endSquare(endSquare)
                .pieceType(PieceType.PAWN);
    }

}