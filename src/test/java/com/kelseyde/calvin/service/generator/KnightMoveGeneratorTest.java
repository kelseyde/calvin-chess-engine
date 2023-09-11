package com.kelseyde.calvin.service.generator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.game.Game;
import com.kelseyde.calvin.model.move.Move;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

public class KnightMoveGeneratorTest {

    private final KnightMoveGenerator generator = new KnightMoveGenerator();

    private final Piece knight = new Piece(Colour.WHITE, PieceType.KNIGHT);

    private Board board;

    @BeforeEach
    public void beforeEach() {
        board = Board.emptyBoard();
    }

    @Test
    public void testEmptyBoard() {

        assertLegalSquares(0, Set.of(10, 17));
        assertLegalSquares(1, Set.of(11, 16, 18));
        assertLegalSquares(2, Set.of(8, 12, 17, 19));
        assertLegalSquares(3, Set.of(9, 13, 18, 20));
        assertLegalSquares(4, Set.of(10, 14, 19, 21));
        assertLegalSquares(5, Set.of(11, 15, 20, 22));
        assertLegalSquares(6, Set.of(12, 21, 23));
        assertLegalSquares(7, Set.of(13, 22));
        assertLegalSquares(8, Set.of(2, 18, 25));
        assertLegalSquares(9, Set.of(3, 19, 24, 26));
        assertLegalSquares(10, Set.of(0, 4, 16, 20, 25, 27));
        assertLegalSquares(11, Set.of(1, 5, 17, 21, 26, 28));
        assertLegalSquares(12, Set.of(2, 6, 18, 22, 27, 29));
        assertLegalSquares(13, Set.of(3, 7, 19, 23, 28, 30));
        assertLegalSquares(14, Set.of(4, 20, 29, 31));
        assertLegalSquares(15, Set.of(5, 21, 30));
        assertLegalSquares(16, Set.of(1, 10, 26, 33));
        assertLegalSquares(17, Set.of(0, 2, 11, 27, 32, 34));
        assertLegalSquares(18, Set.of(1, 3, 8, 12, 24, 28, 33, 35));
        assertLegalSquares(19, Set.of(2, 4, 9, 13, 25, 29, 34, 36));
        assertLegalSquares(20, Set.of(3, 5, 10, 14, 26, 30, 35, 37));
        assertLegalSquares(21, Set.of(4, 6, 11, 15, 27, 31, 36, 38));
        assertLegalSquares(22, Set.of(5, 7, 12, 28, 37, 39));
        assertLegalSquares(23, Set.of(6, 13, 29, 38));
        assertLegalSquares(24, Set.of(9, 18, 34, 41));
        assertLegalSquares(25, Set.of(8, 10, 19, 35, 40, 42));
        assertLegalSquares(26, Set.of(9, 11, 16, 20, 32, 36, 41, 43));
        assertLegalSquares(27, Set.of(10, 12, 17, 21, 33, 37, 42, 44));
        assertLegalSquares(28, Set.of(11, 13, 18, 22, 34, 38, 43, 45));
        assertLegalSquares(29, Set.of(12, 14, 19, 23, 35, 39, 44, 46));
        assertLegalSquares(30, Set.of(13, 15, 20, 36, 45, 47));
        assertLegalSquares(31, Set.of(14, 21, 37, 46));
        assertLegalSquares(32, Set.of(17, 26, 42, 49));
        assertLegalSquares(33, Set.of(16, 18, 27, 43, 48, 50));
        assertLegalSquares(34, Set.of(17, 19, 24, 28, 40, 44, 49, 51));
        assertLegalSquares(35, Set.of(18, 20, 25, 29, 41, 45, 50, 52));
        assertLegalSquares(36, Set.of(19, 21, 26, 30, 42, 46, 51, 53));
        assertLegalSquares(37, Set.of(20, 22, 27, 31, 43, 47, 52, 54));
        assertLegalSquares(38, Set.of(21, 23, 28, 44, 53, 55));
        assertLegalSquares(39, Set.of(22, 29, 45, 54));
        assertLegalSquares(40, Set.of(25, 34, 50, 57));
        assertLegalSquares(41, Set.of(24, 26, 35, 51, 56, 58));
        assertLegalSquares(42, Set.of(25, 27, 32, 36, 48, 52, 57, 59));
        assertLegalSquares(43, Set.of(26, 28, 33, 37, 49, 53, 58, 60));
        assertLegalSquares(44, Set.of(27, 29, 34, 38, 50, 54, 59, 61));
        assertLegalSquares(45, Set.of(28, 30, 35, 39, 51, 55, 60, 62));
        assertLegalSquares(46, Set.of(29, 31, 36, 52, 61, 63));
        assertLegalSquares(47, Set.of(30, 37, 53, 62));
        assertLegalSquares(48, Set.of(33, 42, 58));
        assertLegalSquares(49, Set.of(32, 34, 43, 59));
        assertLegalSquares(50, Set.of(33, 35, 40, 44, 56, 60));
        assertLegalSquares(51, Set.of(34, 36, 41, 45, 57, 61));
        assertLegalSquares(52, Set.of(35, 37, 42, 46, 58, 62));
        assertLegalSquares(53, Set.of(36, 38, 43, 47, 59, 63));
        assertLegalSquares(54, Set.of(37, 39, 44, 60));
        assertLegalSquares(55, Set.of(38, 45, 61));
        assertLegalSquares(56, Set.of(41, 50));
        assertLegalSquares(57, Set.of(40, 42, 51));
        assertLegalSquares(58, Set.of(41, 43, 48, 52));
        assertLegalSquares(59, Set.of(42, 44, 49, 53));
        assertLegalSquares(60, Set.of(43, 45, 50, 54));
        assertLegalSquares(61, Set.of(44, 46, 51, 55));
        assertLegalSquares(62, Set.of(45, 47, 52));
        assertLegalSquares(63, Set.of(46, 53));

    }

    @Test
    public void canCaptureOpponentPieces() {

        board.setPiece(43, knight);
        board.setPiece(26, new Piece(Colour.BLACK, PieceType.PAWN));
        board.setPiece(28, new Piece(Colour.BLACK, PieceType.PAWN));
        board.setPiece(33, new Piece(Colour.BLACK, PieceType.PAWN));
        board.setPiece(37, new Piece(Colour.BLACK, PieceType.KNIGHT));
        board.setPiece(49, new Piece(Colour.BLACK, PieceType.BISHOP));
        board.setPiece(53, new Piece(Colour.BLACK, PieceType.ROOK));
        board.setPiece(58, new Piece(Colour.BLACK, PieceType.QUEEN));
        board.setPiece(60, new Piece(Colour.BLACK, PieceType.QUEEN));

        Set<Integer> expectedLegalSquares = Set.of(26, 28, 33, 37, 49, 53, 58, 60);
        Set<Integer> legalSquares = generator.generatePseudoLegalMoves(Game.fromPosition(board), 43).stream()
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expectedLegalSquares, legalSquares);

    }

    @Test
    public void cannotCaptureSameColourPieces() {

        board.setPiece(43, knight);
        board.setPiece(26, new Piece(Colour.WHITE, PieceType.PAWN));
        board.setPiece(28, new Piece(Colour.WHITE, PieceType.PAWN));
        board.setPiece(33, new Piece(Colour.WHITE, PieceType.PAWN));
        board.setPiece(37, new Piece(Colour.WHITE, PieceType.KNIGHT));
        board.setPiece(49, new Piece(Colour.WHITE, PieceType.BISHOP));
        board.setPiece(53, new Piece(Colour.WHITE, PieceType.ROOK));
        board.setPiece(58, new Piece(Colour.WHITE, PieceType.QUEEN));
        board.setPiece(60, new Piece(Colour.WHITE, PieceType.QUEEN));

        Set<Integer> legalSquares = generator.generatePseudoLegalMoves(Game.fromPosition(board), 43).stream()
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        Assertions.assertTrue(legalSquares.isEmpty());

    }

    private void assertLegalSquares(int startSquare, Set<Integer> expectedLegalSquares) {
        board.setPiece(startSquare, knight);
        Set<Integer> legalSquares = generator.generatePseudoLegalMoves(Game.fromPosition(board), startSquare).stream()
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expectedLegalSquares, legalSquares);
        board.clear();
    }

}