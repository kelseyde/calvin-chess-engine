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

public class BishopMoveGeneratorTest {

    private final BishopMoveGenerator generator = new BishopMoveGenerator();

    private final Piece bishop = new Piece(Colour.WHITE, PieceType.BISHOP);

    private Board board;

    @BeforeEach
    public void beforeEach() {
        board = Board.emptyBoard();
    }

    @Test
    public void testEmptyBoard() {

        // bottom corner
        assertLegalSquares(0, Set.of(9, 18, 27, 36, 45, 54, 63));

        // middle of first rank
        assertLegalSquares(3, Set.of(10, 17, 24, 12, 21, 30, 39));

        // bottom corner
        assertLegalSquares(7, Set.of(14, 21, 28, 35, 42, 49, 56));

        // random selection of squares in the middle ranks
        // random sample of squares in the middle ranks
        assertLegalSquares(10, Set.of(1, 19, 28, 37, 46, 55, 3, 17, 24));
        assertLegalSquares(23, Set.of(5, 14, 30, 37, 44, 51, 58));
        assertLegalSquares(25, Set.of(16, 34, 43, 52, 61, 4, 11, 18, 32));
        assertLegalSquares(36, Set.of(0, 9, 18, 27, 45, 54, 63, 15, 22, 29, 43, 50, 57));
        assertLegalSquares(47, Set.of(2, 11, 20, 29, 38, 54, 61));
        assertLegalSquares(48, Set.of(6, 13, 20, 27, 34, 41, 57));

        // top corner
        assertLegalSquares(56, Set.of(7, 14, 21, 28, 35, 42, 49));

        // middle of last rank
        assertLegalSquares(60, Set.of(24, 33, 42, 51, 39, 46, 53));

        // top corner
        assertLegalSquares(63, Set.of(0, 9, 18, 27, 36, 45, 54));
    }

    @Test
    public void capturingOpponentPiecesEndsVector() {

        int startSquare = 28; //e4

        board.setPiece(10, new Piece(Colour.BLACK, PieceType.PAWN));
        board.setPiece(14, new Piece(Colour.BLACK, PieceType.KNIGHT));
        board.setPiece(42, new Piece(Colour.BLACK, PieceType.BISHOP));
        board.setPiece(46, new Piece(Colour.BLACK, PieceType.ROOK));

        assertLegalSquares(startSquare, Set.of(19, 21, 35, 37, 10, 14, 42, 46));

    }

    @Test
    public void reachingSameColourPiecesEndsVector() {

        int startSquare = 28; //e4

        board.setPiece(10, new Piece(Colour.WHITE, PieceType.PAWN));
        board.setPiece(14, new Piece(Colour.WHITE, PieceType.KNIGHT));
        board.setPiece(42, new Piece(Colour.WHITE, PieceType.BISHOP));
        board.setPiece(46, new Piece(Colour.WHITE, PieceType.ROOK));

        assertLegalSquares(startSquare, Set.of(19, 21, 35, 37));

    }

    private void assertLegalSquares(int startSquare, Set<Integer> expectedLegalSquares) {
        board.setPiece(startSquare, bishop);
        Set<Integer> legalSquares = generator.generateLegalMoves(Game.fromPosition(board), startSquare).stream()
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expectedLegalSquares, legalSquares);
        board.clear();
    }

}