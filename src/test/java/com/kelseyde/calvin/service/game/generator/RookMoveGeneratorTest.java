package com.kelseyde.calvin.service.game.generator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.Game;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.utils.BoardUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

public class RookMoveGeneratorTest {

    private final RookMoveGenerator generator = new RookMoveGenerator();

    private final Piece rook = new Piece(Colour.WHITE, PieceType.ROOK);

    private Game game;

    @BeforeEach
    public void beforeEach() {
        Board board = BoardUtils.emptyBoard();
        game = new Game(board);
    }

    @Test
    public void testEmptyBoard() {

        // bottom corner
        assertLegalSquares(0, Set.of(1, 2, 3, 4, 5, 6, 7, 8, 16, 24, 32, 40, 48, 56));

        // middle first rank
        assertLegalSquares(4, Set.of(0, 1, 2, 3, 5, 6, 7, 12, 20, 28, 36, 44, 52, 60));

        // bottom corner
        assertLegalSquares(7, Set.of(0, 1, 2, 3, 4, 5, 6, 15, 23, 31, 39, 47, 55, 63));

        // random sample of squares in the middle ranks
        assertLegalSquares(10, Set.of(8, 9, 11, 12, 13, 14, 15, 2, 18, 26, 34, 42, 50, 58));
        assertLegalSquares(23, Set.of(16, 17, 18, 19, 20, 21, 22, 7, 15, 31, 39, 47, 55, 63));
        assertLegalSquares(25, Set.of(24, 26, 27, 28, 29, 30, 31, 1, 9, 17, 33, 41, 49, 57));
        assertLegalSquares(36, Set.of(32, 33, 34, 35, 37, 38, 39, 4, 12, 20, 28, 44, 52, 60));
        assertLegalSquares(47, Set.of(40, 41, 42, 43, 44, 45, 46, 7, 15, 23, 31, 39, 55, 63));
        assertLegalSquares(48, Set.of(49, 50, 51, 52, 53, 54, 55, 0, 8, 16, 24, 32, 40, 56));

        // top corner
        assertLegalSquares(56, Set.of(57, 58, 59, 60, 61, 62, 63, 0, 8, 16, 24, 32, 40, 48));

        // middle last rank
        assertLegalSquares(60, Set.of(56, 57, 58, 59, 61, 62, 63, 4, 12, 20, 28, 36, 44, 52));

    }

    @Test
    public void testCapturingOpponentPiecesEndsVector() {

        int startSquare = 28; //e4

        game.getBoard().setPiece(12, new Piece(Colour.BLACK, PieceType.PAWN));
        game.getBoard().setPiece(26, new Piece(Colour.BLACK, PieceType.KNIGHT));
        game.getBoard().setPiece(30, new Piece(Colour.BLACK, PieceType.BISHOP));
        game.getBoard().setPiece(44, new Piece(Colour.BLACK, PieceType.ROOK));

        assertLegalSquares(startSquare, Set.of(12, 20, 26, 27, 29, 30, 36, 44));

    }

    @Test
    public void testReachingSameColourPiecesEndsVector() {

        int startSquare = 28; //e4

        game.getBoard().setPiece(12, new Piece(Colour.WHITE, PieceType.PAWN));
        game.getBoard().setPiece(26, new Piece(Colour.WHITE, PieceType.KNIGHT));
        game.getBoard().setPiece(30, new Piece(Colour.WHITE, PieceType.BISHOP));
        game.getBoard().setPiece(44, new Piece(Colour.WHITE, PieceType.ROOK));

        assertLegalSquares(startSquare, Set.of(20, 27, 29, 36));

    }

    private void assertLegalSquares(int startSquare, Set<Integer> expectedLegalSquares) {
        game.getBoard().setPiece(startSquare, rook);
        Set<Integer> legalSquares = generator.generatePseudoLegalMoves(game, startSquare).stream()
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expectedLegalSquares, legalSquares);
        game.getBoard().unsetPiece(startSquare);
    }

}