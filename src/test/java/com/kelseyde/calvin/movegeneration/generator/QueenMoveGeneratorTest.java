package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

public class QueenMoveGeneratorTest {

    private final MoveGenerator generator = new MoveGenerator();

    private Board board;

    @BeforeEach
    public void beforeEach() {
        board = TestUtils.emptyBoard();
    }

    @Test
    public void capturingOpponentPiecesEndsVector() {

        int startSquare = 28; //e4

        board.toggleSquare(PieceType.KING, false, 0);
        board.toggleSquare(PieceType.KING, true, 64);
        board.toggleSquare(PieceType.PAWN, false, 10);
        board.toggleSquare(PieceType.KNIGHT, false, 14);
        board.toggleSquare(PieceType.BISHOP, false, 42);
        board.toggleSquare(PieceType.ROOK, false, 46);
        board.toggleSquare(PieceType.PAWN, false, 12);
        board.toggleSquare(PieceType.KNIGHT, false, 26);
        board.toggleSquare(PieceType.BISHOP, false, 30);
        board.toggleSquare(PieceType.ROOK, false, 44);
        board.recalculatePieces();

        assertLegalSquares(startSquare, Set.of(19, 21, 35, 37, 10, 14, 42, 46, 12, 20, 26, 27, 29, 30, 36, 44));

    }

    @Test
    public void reachingSameColourPiecesEndsVector() {

        board.toggleSquare(PieceType.KING, true, 0);
        board.toggleSquare(PieceType.KING, false, 63);

        int startSquare = 28; //e4

        board.toggleSquare(PieceType.PAWN, true, 10);
        board.toggleSquare(PieceType.KNIGHT, true, 14);
        board.toggleSquare(PieceType.BISHOP, true, 42);
        board.toggleSquare(PieceType.ROOK, true, 46);
        board.toggleSquare(PieceType.PAWN, true, 12);
        board.toggleSquare(PieceType.KNIGHT, true, 26);
        board.toggleSquare(PieceType.BISHOP, true, 30);
        board.toggleSquare(PieceType.ROOK, true, 44);
        board.recalculatePieces();

        assertLegalSquares(startSquare, Set.of(19, 21, 35, 37, 27, 29, 20, 36));

    }

    private void assertLegalSquares(int startSquare, Set<Integer> expectedLegalSquares) {
        board.toggleSquare(PieceType.QUEEN, true, startSquare);
        Set<Integer> legalSquares = generator.generateMoves(board, false).stream()
                .filter(move -> move.getStartSquare() == startSquare)
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expectedLegalSquares, legalSquares);
        board = TestUtils.emptyBoard();
    }

}