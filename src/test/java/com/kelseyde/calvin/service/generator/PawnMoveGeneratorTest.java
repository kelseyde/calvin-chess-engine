package com.kelseyde.calvin.service.generator;

import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.board.Board;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.model.piece.Piece;
import com.kelseyde.calvin.model.piece.PieceType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

public class PawnMoveGeneratorTest {

    private final PawnMoveGenerator generator = new PawnMoveGenerator();

    private final Piece whitePawn = new Piece(Colour.WHITE, PieceType.PAWN);
    private final Piece blackPawn = new Piece(Colour.BLACK, PieceType.PAWN);

    private Board board;

    @BeforeEach
    public void beforeEach() {
        board = Board.empty();
    }

    @Test
    public void testStartingPositions() {

        assertLegalSquares(8, whitePawn, Set.of(16, 24));
        assertLegalSquares(9, whitePawn, Set.of(17, 25));
        assertLegalSquares(10, whitePawn, Set.of(18, 26));
        assertLegalSquares(11, whitePawn, Set.of(19, 27));
        assertLegalSquares(12, whitePawn, Set.of(20, 28));
        assertLegalSquares(13, whitePawn, Set.of(21, 29));
        assertLegalSquares(14, whitePawn, Set.of(22, 30));
        assertLegalSquares(15, whitePawn, Set.of(23, 31));

        assertLegalSquares(48, blackPawn, Set.of(40, 32));
        assertLegalSquares(49, blackPawn, Set.of(41, 33));
        assertLegalSquares(50, blackPawn, Set.of(42, 34));
        assertLegalSquares(51, blackPawn, Set.of(43, 35));
        assertLegalSquares(52, blackPawn, Set.of(44, 36));
        assertLegalSquares(53, blackPawn, Set.of(45, 37));
        assertLegalSquares(54, blackPawn, Set.of(46, 38));
        assertLegalSquares(55, blackPawn, Set.of(47, 39));

    }

    @Test
    public void testPawnCannotMoveThroughPiece() {

        board.setPiece(16, new Piece(Colour.WHITE, PieceType.ROOK));
        assertLegalSquares(8, whitePawn, Set.of());

        board.setPiece(17, new Piece(Colour.BLACK, PieceType.PAWN));
        assertLegalSquares(9, whitePawn, Set.of());

        board.setPiece(18, new Piece(Colour.WHITE, PieceType.KING));
        assertLegalSquares(10, whitePawn, Set.of());

        board.setPiece(19, new Piece(Colour.BLACK, PieceType.QUEEN));
        assertLegalSquares(11, whitePawn, Set.of());

        board.setPiece(44, new Piece(Colour.BLACK, PieceType.BISHOP));
        assertLegalSquares(52, blackPawn, Set.of());

        board.setPiece(45, new Piece(Colour.BLACK, PieceType.PAWN));
        assertLegalSquares(53, blackPawn, Set.of());

        board.setPiece(46, new Piece(Colour.BLACK, PieceType.KNIGHT));
        assertLegalSquares(54, blackPawn, Set.of());

        board.setPiece(47, new Piece(Colour.BLACK, PieceType.ROOK));
        assertLegalSquares(55, blackPawn, Set.of());

    }

    @Test
    public void testWhitePawnsNotOnStartingSquares() {

        assertLegalSquares(16, whitePawn, Set.of(24));
        assertLegalSquares(34, whitePawn, Set.of(42));
        assertLegalSquares(45, whitePawn, Set.of(53));
        assertLegalSquares(55, whitePawn, Set.of(63));

    }

    @Test
    public void testBlackPawnsNotOnStartingSquares() {

        assertLegalSquares(45, blackPawn, Set.of(37));
        assertLegalSquares(35, blackPawn, Set.of(27));
        assertLegalSquares(31, blackPawn, Set.of(23));
        assertLegalSquares(8, blackPawn, Set.of(0));

    }

    @Test
    public void testWhitePawnCaptures() {

        board.setPiece(16, new Piece(Colour.BLACK, PieceType.PAWN));
        board.setPiece(18, new Piece(Colour.BLACK, PieceType.PAWN));
        assertLegalSquares(9, whitePawn, Set.of(16, 17, 18, 25));

        board.setPiece(38, new Piece(Colour.BLACK, PieceType.PAWN));
        // should not capture the wrapped piece
        board.setPiece(40, new Piece(Colour.BLACK, PieceType.PAWN));
        assertLegalSquares(31, whitePawn, Set.of(38, 39));

        board.setPiece(58, new Piece(Colour.BLACK, PieceType.PAWN));
        // should not capture white pieces
        board.setPiece(60, new Piece(Colour.WHITE, PieceType.PAWN));
        assertLegalSquares(51, whitePawn, Set.of(58, 59));

    }

    @Test
    public void testBlackPawnCaptures() {

        board.setPiece(40, new Piece(Colour.WHITE, PieceType.PAWN));
        board.setPiece(42, new Piece(Colour.WHITE, PieceType.PAWN));
        assertLegalSquares(49, blackPawn, Set.of(40, 41, 42, 33));

        board.setPiece(25, new Piece(Colour.WHITE, PieceType.PAWN));
        // should not capture the wrapped piece
        board.setPiece(23, new Piece(Colour.WHITE, PieceType.PAWN));
        assertLegalSquares(32, blackPawn, Set.of(24, 25));

        board.setPiece(3, new Piece(Colour.WHITE, PieceType.PAWN));
        // should not capture black pieces
        board.setPiece(5, new Piece(Colour.BLACK, PieceType.PAWN));
        assertLegalSquares(12, blackPawn, Set.of(3, 4));

    }

    private void assertLegalSquares(int startSquare, Piece pawn, Set<Integer> expectedLegalSquares) {
        board.setPiece(startSquare, pawn);
        Set<Integer> legalSquares = generator.generateLegalMoves(board, startSquare).stream()
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expectedLegalSquares, legalSquares);
        board.clear();
    }



}