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

public class KingMoveGeneratorTest {

    private final KingMoveGenerator generator = new KingMoveGenerator();

    private final Piece king = new Piece(Colour.WHITE, PieceType.KING);

    private Board board;

    @BeforeEach
    public void beforeEach() {
        board = Board.emptyBoard();
    }

    @Test
    public void testEmptyBoard() {

        // bottom corner
        assertLegalSquares(0, Set.of(1, 9, 8));

        // middle of first rank
        assertLegalSquares(3, Set.of(2, 10, 11, 12, 4));

        // bottom corner
        assertLegalSquares(7, Set.of(6, 14, 15));

        // middle of board
        assertLegalSquares(18, Set.of(9, 10, 11, 17, 19, 25, 26, 27));
        assertLegalSquares(29, Set.of(20, 21, 22, 28, 30, 36, 37, 38));
        assertLegalSquares(42, Set.of(33, 34, 35, 41, 43, 49, 50, 51));

        // top corner
        assertLegalSquares(56, Set.of(48, 49, 57));

        // middle of last rank
        assertLegalSquares(60, Set.of(51, 52, 53, 59, 61));

        // top corner
        assertLegalSquares(63, Set.of(54, 55, 62));

    }

    private void assertLegalSquares(int startSquare, Set<Integer> expectedLegalSquares) {
        board.setPiece(startSquare, king);
        Set<Integer> legalSquares = generator.generatePseudoLegalMoves(Game.fromPosition(board), startSquare).stream()
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expectedLegalSquares, legalSquares);
        board.clear();
    }

}