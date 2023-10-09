package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.move.MoveType;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
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
        board.toggleSquare(PieceType.PAWN, true, 16);
        assertMovesFromSquare(board, 16, Set.of(moveBuilder(16, 24).build()));
        board.toggleSquare(PieceType.PAWN, true, 16);

        board.toggleSquare(PieceType.PAWN, true, 34);
        assertMovesFromSquare(board, 34, Set.of(moveBuilder(34, 42).build()));
        board.toggleSquare(PieceType.PAWN, true, 34);

        board.toggleSquare(PieceType.PAWN, true, 45);
        assertMovesFromSquare(board, 45, Set.of(moveBuilder(45, 53).build()));
        board.toggleSquare(PieceType.PAWN, true, 45);

    }

    @Test
    public void testBlackPawnsNotOnStartingSquares() {

        board = TestUtils.emptyBoard();
        board.setWhiteToMove(false);

        board.toggleSquare(PieceType.PAWN, false, 45);
        assertMovesFromSquare(board, 45, Set.of(moveBuilder(45, 37).build()));
        board.toggleSquare(PieceType.PAWN, true, 45);

        board.toggleSquare(PieceType.PAWN, false, 35);
        assertMovesFromSquare(board, 35, Set.of(moveBuilder(35, 27).build()));
        board.toggleSquare(PieceType.PAWN, true, 35);

        board.toggleSquare(PieceType.PAWN, false, 31);
        assertMovesFromSquare(board, 31, Set.of(moveBuilder(31, 23).build()));
        board.toggleSquare(PieceType.PAWN, true, 31);

    }

    @Test
    public void testWhitePawnCaptures() {

        String fen = "k7/8/8/8/8/p1p5/1P6/K7 w - - 0 1";
        board = FEN.fromFEN(fen);
        assertMovesFromSquare(board, 9,
                Set.of(moveBuilder(9, 16).build(), moveBuilder(9, 17).build(), moveBuilder(9, 18).build(), moveBuilder(9, 25).build()));

        fen = "k7/8/p7/6p1/7P/8/8/K7 w - - 0 1";
        board = FEN.fromFEN(fen);
        assertMovesFromSquare(board, 31,
                Set.of(moveBuilder(31, 38).build(), moveBuilder(31, 39).build()));

        fen = "k7/p1p5/1P6/8/8/8/8/K7 w - - 0 1";
        board = FEN.fromFEN(fen);
        assertMovesFromSquare(board, 41,
                Set.of(moveBuilder(41, 48).build(),
                        moveBuilder(41, 49).build(),
                        moveBuilder(41, 50).build()
                ));

    }

    @Test
    public void testBlackPawnCaptures() {

        String fen = "k7/1p6/P1P5/8/8/8/8/K7 b - - 0 1";
        board = FEN.fromFEN(fen);
        assertMovesFromSquare(board, 49,
                Set.of(moveBuilder(49, 40).build(), moveBuilder(49, 41).build(), moveBuilder(49, 42).build(), moveBuilder(49, 33).build()));

        fen = "k7/8/8/p7/1P5P/8/8/K7 b - - 0 1";
        board = FEN.fromFEN(fen);
        // should not capture the wrapped piece
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
        board.toggleSquare(PieceType.PAWN, true, 35);
        board.toggleSquare(PieceType.PAWN, false, 34);
        board.getGameState().setEnPassantFile(BoardUtils.getFile(42));

        List<Move> legalWhiteMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(35, 43).build();
        Move enPassantCapture = moveBuilder(35, 42).moveType(MoveType.EN_PASSANT).build();

        Assertions.assertEquals(Set.of(standardMove, enPassantCapture), legalWhiteMoves);

    }

    @Test
    public void testWhiteEnPassantWithOtherCapture() {

        String fen = "k7/4p3/2q5/3P4/8/8/8/K7 b - - 0 1";
        board = FEN.fromFEN(fen);

        board.makeMove(TestUtils.getLegalMove(board, NotationUtils.fromNotation("e7", "e5", PieceType.PAWN)));

        List<Move> legalWhiteMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(35, 43).build();
        Move standardCapture = moveBuilder(35, 42).build();
        Move enPassantCapture = moveBuilder(35, 44)
                .moveType(MoveType.EN_PASSANT).build();

        Assertions.assertEquals(Set.of(standardMove, standardCapture, enPassantCapture), legalWhiteMoves);

    }

    @Test
    public void testWhiteDoubleEnPassantIsImpossible() {

        board = TestUtils.emptyBoard();

        board.toggleSquare(PieceType.PAWN, true, 35);
        // we need another white piece to spend a move in between black's pawn moves
        board.toggleSquare(PieceType.ROOK, true, 0);
        // two black pawns on starting positions
        board.toggleSquare(PieceType.PAWN, false, 50);
        board.toggleSquare(PieceType.PAWN, false, 52);

        board.setWhiteToMove(false);

        // black first double pawn advance
        board.makeMove(moveBuilder(50, 34).enPassantFile(2).build());

        // white wastes a move with rook
        board.makeMove(moveBuilder(0, 8).pieceType(PieceType.ROOK).build());

        // black second double pawn advance
        board.makeMove(moveBuilder(52, 36).enPassantFile(4).build());

        List<Move> legalWhiteMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(35, 43)
                .moveType(MoveType.STANDARD)
                .build();
        Move enPassantCapture = moveBuilder(35, 44)
                .moveType(MoveType.EN_PASSANT).build();

        Assertions.assertEquals(Set.of(standardMove, enPassantCapture), legalWhiteMoves);

    }

    @Test
    public void testBlackEnPassant() {

        board.toggleSquare(PieceType.PAWN, false, 29);
        board.toggleSquare(PieceType.PAWN, true, 30);
        board.getGameState().setEnPassantFile(BoardUtils.getFile(22));
        board.setWhiteToMove(false);

        List<Move> legalBlackMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(29, 21).build();
        Move enPassantCapture = moveBuilder(29, 22).moveType(MoveType.EN_PASSANT).build();

        Assertions.assertEquals(Set.of(standardMove, enPassantCapture), legalBlackMoves);

    }

    @Test
    public void testBlackEnPassantWithOtherCapture() {

        String fen = "k7/8/8/8/5p2/6P1/4P3/K7 w - - 0 1";
        Board board = FEN.fromFEN(fen);
        board.makeMove(TestUtils.getLegalMove(board, "e2", "e4"));

        List<Move> legalBlackMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(29, 21).build();
        Move standardCapture = moveBuilder(29, 22).build();
        Move enPassantCapture = moveBuilder(29, 20).moveType(MoveType.EN_PASSANT).startSquare(29).endSquare(20).build();
        Assertions.assertEquals(Set.of(standardMove, standardCapture, enPassantCapture), legalBlackMoves);

    }

    @Test
    public void testBlackDoubleEnPassantIsImpossible() {

        board.toggleSquare(PieceType.PAWN, false, 25);
        // we need another black piece to spend a move in between black's pawn moves
        board.toggleSquare(PieceType.ROOK, false, 63);
        // two black pawns on starting positions
        board.toggleSquare(PieceType.PAWN, true, 8);
        board.toggleSquare(PieceType.PAWN, true, 10);
        board.setWhiteToMove(true);

        // white first double pawn advance
        board.makeMove(moveBuilder(10, 26).enPassantFile(2).build());

        // black wastes move with rook
        board.makeMove(moveBuilder(63, 62).pieceType(PieceType.ROOK).build());

        // second double pawn move from white, should make the first en-passant capture impossible
        board.makeMove(moveBuilder(8, 24).enPassantFile(0).build());

        List<Move> legalBlackMoves = generator.generatePseudoLegalMoves(board);

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
        board.toggleSquare(PieceType.PAWN, true, 51);
        List<Move> legalMoves = generator.generatePseudoLegalMoves(board);
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
        board.toggleSquare(PieceType.PAWN, false, 8);
        board.setWhiteToMove(false);
        List<Move> legalMoves = generator.generatePseudoLegalMoves(board);
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
        String fen = "k1q5/3P4/8/8/8/8/8/K7 w - - 0 1";
        board = FEN.fromFEN(fen);
        List<Move> legalMoves = generator.generatePseudoLegalMoves(board);
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
        String fen = "k7/8/8/8/8/8/7p/K5B1 b - - 0 1";
        board = FEN.fromFEN(fen);
        List<Move> legalMoves = generator.generatePseudoLegalMoves(board);
        List<Move> expectedLegalMoves = List.of(
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

        List<Move> legalMoves = generator.generatePseudoLegalMoves(board);
        List<Move> expectedLegalMoves = List.of(moveBuilder(23, 31).build());
        Assertions.assertEquals(1, legalMoves.size());
        Assertions.assertEquals(expectedLegalMoves, legalMoves);

    }

    private void assertMoves(Board board, Set<Move> expected) {
        List<Move> actual = generator.generatePseudoLegalMoves(board);

        Assertions.assertEquals(
                expected.stream().map(m -> m.getPieceType().toString() + m.getStartSquare() + m.getEndSquare()).collect(Collectors.toSet()),
                actual.stream().map(m -> m.getPieceType().toString() + m.getStartSquare() + m.getEndSquare()).collect(Collectors.toSet()));
    }

    private void assertMovesFromSquare(Board board, int square, Set<Move> expected) {
        List<Move> actual = generator.generatePseudoLegalMoves(board).stream()
                .filter(move -> move.getStartSquare() == square)
                .toList();
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