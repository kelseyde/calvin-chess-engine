package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

public class KnightMoveGeneratorTest {

    private final MoveGenerator generator = new MoveGenerator();

    private Board board;

    @BeforeEach
    public void beforeEach() {
        board = TestUtils.emptyBoard();
    }

    @Test
    public void canCaptureOpponentPieces() {

        board.toggleSquare(Piece.KING, true, 0);
        board.toggleSquare(Piece.KING, false, 3);
        board.toggleSquare(Piece.KNIGHT, true, 43);
        board.toggleSquare(Piece.PAWN, false, 26);
        board.toggleSquare(Piece.PAWN, false, 28);
        board.toggleSquare(Piece.PAWN, false, 33);
        board.toggleSquare(Piece.KNIGHT, false, 37);
        board.toggleSquare(Piece.BISHOP, false, 49);
        board.toggleSquare(Piece.ROOK, false, 53);
        board.toggleSquare(Piece.QUEEN, false, 58);
        board.toggleSquare(Piece.QUEEN, false, 60);

        Set<Integer> expectedLegalSquares = Set.of(26, 28, 33, 37, 49, 53, 58, 60);
        Set<Integer> legalSquares = generator.generateMoves(board).stream()
                .filter(m -> m.getStartSquare() == 43)
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expectedLegalSquares, legalSquares);

    }

    @Test
    public void cannotCaptureSameColourPieces() {

        String fen = "2R1R3/1P3Q2/3N4/1B3B2/2P1P3/8/8/K6k w - - 0 1";
        board = FEN.toBoard(fen);

        Set<Integer> legalSquares = generator.generateMoves(board).stream()
                .filter(m -> m.getStartSquare() == 43)
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        Assertions.assertTrue(legalSquares.isEmpty());

    }

    private void assertLegalSquares(int startSquare, Set<Integer> expectedLegalSquares) {
        board.toggleSquare(Piece.KNIGHT, true, startSquare);
        Set<Integer> legalSquares = generator.generateMoves(board).stream()
                .filter(m -> m.getStartSquare() == startSquare)
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expectedLegalSquares, legalSquares);
        board = TestUtils.emptyBoard();
    }

}