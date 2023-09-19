package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Game;
import com.kelseyde.calvin.board.piece.Piece;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.move.MoveType;
import com.kelseyde.calvin.board.move.MoveUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

public class PawnMoveGeneratorTest {

    private final PawnMoveGenerator generator = new PawnMoveGenerator();

    private final String whitePawn = Piece.getPieceCode(true, PieceType.PAWN);
    private final String blackPawn = Piece.getPieceCode(false, PieceType.PAWN);

    private Board board;

    @BeforeEach
    public void beforeEach() {
        board = Board.emptyBoard();
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

        board = Board.emptyBoard();
        board.setPiece(16, Piece.getPieceCode(true, PieceType.ROOK));
        assertMovesFromSquare(board, 8, Set.of());
        board.unsetPiece(16);

        board.setPiece(17, Piece.getPieceCode(false, PieceType.PAWN));
        assertMovesFromSquare(board, 9, Set.of());
        board.unsetPiece(17);

        board.setPiece(18, Piece.getPieceCode(true, PieceType.KING));
        assertMovesFromSquare(board, 10, Set.of());
        board.unsetPiece(18);

        board.setPiece(19, Piece.getPieceCode(false, PieceType.QUEEN));
        assertMovesFromSquare(board, 11, Set.of());
        board.unsetPiece(19);

        board.setPiece(44, Piece.getPieceCode(false, PieceType.BISHOP));
        assertMovesFromSquare(board, 52, Set.of());
        board.unsetPiece(44);

        board.setPiece(45, Piece.getPieceCode(false, PieceType.PAWN));
        assertMovesFromSquare(board, 53, Set.of());
        board.unsetPiece(45);

        board.setPiece(46, Piece.getPieceCode(false, PieceType.KNIGHT));
        assertMovesFromSquare(board, 54, Set.of());
        board.unsetPiece(46);

        board.setPiece(47, Piece.getPieceCode(false, PieceType.ROOK));
        assertMovesFromSquare(board, 55, Set.of());
        board.unsetPiece(47);

    }


    @Test
    public void testWhitePawnsNotOnStartingSquares() {

        board = Board.emptyBoard();
        board.setPiece(16, whitePawn);
        assertMovesFromSquare(board, 16, Set.of(moveBuilder(16, 24).build()));
        board.unsetPiece(16);

        board.setPiece(34, whitePawn);
        assertMovesFromSquare(board, 34, Set.of(moveBuilder(34, 42).build()));
        board.unsetPiece(34);

        board.setPiece(45, whitePawn);
        assertMovesFromSquare(board, 45, Set.of(moveBuilder(45, 53).build()));
        board.unsetPiece(45);

    }

    @Test
    public void testBlackPawnsNotOnStartingSquares() {

        board = Board.emptyBoard();
        board.setWhiteToMove(false);

        board.setPiece(45, blackPawn);
        assertMovesFromSquare(board, 45, Set.of(moveBuilder(45, 37).build()));
        board.unsetPiece(45);

        board.setPiece(35, blackPawn);
        assertMovesFromSquare(board, 35, Set.of(moveBuilder(35, 27).build()));
        board.unsetPiece(35);

        board.setPiece(31, blackPawn);
        assertMovesFromSquare(board, 31, Set.of(moveBuilder(31, 23).build()));
        board.unsetPiece(31);

    }

    @Test
    public void testWhitePawnCaptures() {

        board = Board.emptyBoard();
        board.setPiece(9, Piece.getPieceCode(true, PieceType.PAWN));
        board.setPiece(16, Piece.getPieceCode(false, PieceType.PAWN));
        board.setPiece(18, Piece.getPieceCode(false, PieceType.PAWN));
        assertMovesFromSquare(board, 9,
                Set.of(moveBuilder(9, 16).build(), moveBuilder(9, 17).build(), moveBuilder(9, 18).build(), moveBuilder(9, 25).build()));

        board.setPiece(31, Piece.getPieceCode(true, PieceType.PAWN));
        board.setPiece(38, Piece.getPieceCode(false, PieceType.PAWN));
        // should not capture the wrapped piece
        board.setPiece(40, Piece.getPieceCode(false, PieceType.PAWN));
        assertMovesFromSquare(board, 31,
                Set.of(moveBuilder(31, 38).build(), moveBuilder(31, 39).build()));

        board.setPiece(41, Piece.getPieceCode(true, PieceType.PAWN));
        board.setPiece(48, Piece.getPieceCode(false, PieceType.PAWN));
        // should not capture white pieces
        board.setPiece(50, Piece.getPieceCode(true, PieceType.ROOK));
        assertMovesFromSquare(board, 41,
                Set.of(moveBuilder(41, 48).build(),
                        moveBuilder(41, 49).build()));

    }

    @Test
    public void testBlackPawnCaptures() {

        board = Board.emptyBoard();
        board.setWhiteToMove(false);

        board.setPiece(49, Piece.getPieceCode(false, PieceType.PAWN));
        board.setPiece(40, Piece.getPieceCode(true, PieceType.PAWN));
        board.setPiece(42, Piece.getPieceCode(true, PieceType.PAWN));
        assertMovesFromSquare(board, 49,
                Set.of(moveBuilder(49, 40).build(), moveBuilder(49, 41).build(), moveBuilder(49, 42).build(), moveBuilder(49, 33).build()));

        board.setPiece(32, Piece.getPieceCode(false, PieceType.PAWN));
        board.setPiece(25, Piece.getPieceCode(true, PieceType.PAWN));
        // should not capture the wrapped piece
        board.setPiece(23, Piece.getPieceCode(true, PieceType.PAWN));
        assertMovesFromSquare(board, 32,
                Set.of(moveBuilder(32, 24).build(), moveBuilder(32, 25).build()));

    }

    @Test
    public void testWhiteEnPassant() {

        board = Board.emptyBoard();
        board.setPiece(35, whitePawn);
        board.setPiece(34, blackPawn);
        board.setEnPassantTarget(1L << 42);

        Set<Move> legalWhiteMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(35, 43).build();
        Move enPassantCapture = moveBuilder(35, 42).moveType(MoveType.EN_PASSANT)
                .enPassantCapture(1L << 34)
                .build();

        Assertions.assertEquals(Set.of(standardMove, enPassantCapture), legalWhiteMoves);

    }

    @Test
    public void testWhiteEnPassantWithOtherCapture() {

        board = Board.emptyBoard();
        board.setPiece(35, whitePawn);
        board.setPiece(52, blackPawn);
        board.setPiece(42, Piece.getPieceCode(false, PieceType.QUEEN));
        board.setEnPassantTarget(1L << 44);

        Set<Move> legalWhiteMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(35, 43).build();
        Move standardCapture = moveBuilder(35, 42).build();
        Move enPassantCapture = moveBuilder(35, 44)
                .moveType(MoveType.EN_PASSANT)
                .enPassantCapture(1L << 36)
                .build();

        Assertions.assertEquals(Set.of(standardMove, standardCapture, enPassantCapture), legalWhiteMoves);

    }

    @Test
    public void testWhiteDoubleEnPassantIsImpossible() {

        board = Board.emptyBoard();

        board.setPiece(35, whitePawn);
        // we need another white piece to spend a move in between black's pawn moves
        board.setPiece(0, Piece.getPieceCode(true, PieceType.ROOK));
        // two black pawns on starting positions
        board.setPiece(50, Piece.getPieceCode(false, PieceType.PAWN));
        board.setPiece(52, Piece.getPieceCode(false, PieceType.PAWN));

        board.setWhiteToMove(false);

        // black first double pawn advance
        board.applyMove(moveBuilder(50, 34).enPassantTarget(1L << 42).build());

        // white wastes a move with rook
        board.applyMove(moveBuilder(0, 8).pieceType(PieceType.ROOK).build());

        // black second double pawn advance
        board.applyMove(moveBuilder(52, 36).enPassantTarget(1L << 44).build());

        Set<Move> legalWhiteMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(35, 43)
                .moveType(MoveType.STANDARD)
                .build();
        Move enPassantCapture = moveBuilder(35, 44)
                .moveType(MoveType.EN_PASSANT)
                .enPassantCapture(1L << 36)
                .build();

        Assertions.assertEquals(Set.of(standardMove, enPassantCapture), legalWhiteMoves);

    }

    @Test
    public void testBlackEnPassant() {

        board.setPiece(29, blackPawn);
        board.setPiece(30, whitePawn);
        board.setEnPassantTarget(1L << 22);
        board.setWhiteToMove(false);

        Set<Move> legalBlackMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(29, 21).build();
        Move enPassantCapture = moveBuilder(29, 22).moveType(MoveType.EN_PASSANT)
                .enPassantCapture(1L << 30)
                .build();

        Assertions.assertEquals(Set.of(standardMove, enPassantCapture), legalBlackMoves);

    }

    @Test
    public void testBlackEnPassantWithOtherCapture() {

        board.setPiece(29, blackPawn);
        board.setPiece(28, whitePawn);
        board.setPiece(22, Piece.getPieceCode(true, PieceType.ROOK));
        board.setEnPassantTarget(1L << 20);
        board.setWhiteToMove(false);

        Set<Move> legalBlackMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(29, 21).build();
        Move standardCapture = moveBuilder(29, 22).build();
        Move enPassantCapture = moveBuilder(29, 20).startSquare(29).endSquare(20)
                .enPassantCapture(1L << 28)
                .moveType(MoveType.EN_PASSANT)
                .build();

        Assertions.assertEquals(Set.of(standardMove, standardCapture, enPassantCapture), legalBlackMoves);

    }

    @Test
    public void testBlackDoubleEnPassantIsImpossible() {

        board.setPiece(25, blackPawn);
        // we need another black piece to spend a move in between black's pawn moves
        board.setPiece(63, Piece.getPieceCode(false, PieceType.ROOK));
        // two black pawns on starting positions
        board.setPiece(8, Piece.getPieceCode(true, PieceType.PAWN));
        board.setPiece(10, Piece.getPieceCode(true, PieceType.PAWN));
        board.setWhiteToMove(true);

        // white first double pawn advance
        board.applyMove(moveBuilder(10, 26).enPassantTarget(1L << 18).build());

        // black wastes move with rook
        board.applyMove(moveBuilder(63, 62).pieceType(PieceType.ROOK).build());

        // second double pawn move from white, should make the first en-passant capture impossible
        board.applyMove(moveBuilder(8, 24).enPassantTarget(1L << 16).build());

        Set<Move> legalBlackMoves = generator.generatePseudoLegalMoves(board);

        Move standardMove = moveBuilder(25, 17).build();
        Move enPassantCapture = moveBuilder(25, 16)
                .enPassantCapture(1L << 24)
                .moveType(MoveType.EN_PASSANT)
                .build();

        Assertions.assertEquals(Set.of(standardMove, enPassantCapture), legalBlackMoves);

    }

    @Test
    public void testEnPassantRemovesCapturedPawn() {

        Game game = new Game();
        game.makeMove(MoveUtils.fromNotation("e2", "e4"));
        game.makeMove(MoveUtils.fromNotation("g8", "f6"));
        game.makeMove(MoveUtils.fromNotation("e4", "e5"));
        game.makeMove(MoveUtils.fromNotation("d7", "d5"));
        //en passant
        game.makeMove(MoveUtils.fromNotation("e5", "d6"));

        long d5Bitboard = 1L << 35;
        // Assert d5 is empty
        Assertions.assertEquals(0, (board.getOccupied() & d5Bitboard));

    }

    @Test
    public void testWhiteStandardPromotion() {
        board = Board.emptyBoard();
        board.setPiece(51, whitePawn);
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
        board.setPiece(8, blackPawn);
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
        board.setPiece(51, whitePawn);
        board.setPiece(58, Piece.getPieceCode(false, PieceType.QUEEN));
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
        board.setPiece(15, blackPawn);
        board.setPiece(6, Piece.getPieceCode(true, PieceType.BISHOP));
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

    private void assertMoves(Board board, Set<Move> expected) {
        Set<Move> actual = generator.generatePseudoLegalMoves(board);

        Assertions.assertEquals(
                expected.stream().map(Move::getKey).collect(Collectors.toSet()),
                actual.stream().map(Move::getKey).collect(Collectors.toSet()));
    }

    private void assertMovesFromSquare(Board board, int square, Set<Move> expected) {
        Set<Move> actual = generator.generatePseudoLegalMoves(board).stream()
                .filter(move -> move.getStartSquare() == square)
                .collect(Collectors.toSet());
        Assertions.assertEquals(
                expected.stream().map(Move::getKey).collect(Collectors.toSet()),
                actual.stream().map(Move::getKey).collect(Collectors.toSet()));
    }
    
    private Move.MoveBuilder moveBuilder(int startSquare, int endSquare) {
        return Move.builder()
                .startSquare(startSquare)
                .endSquare(endSquare)
                .pieceType(PieceType.PAWN);
    }

}