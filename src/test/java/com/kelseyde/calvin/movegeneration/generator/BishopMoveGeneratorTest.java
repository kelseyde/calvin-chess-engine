package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

public class BishopMoveGeneratorTest {

    private final BishopMoveGenerator generator = new BishopMoveGenerator();

    private Board board;

    @BeforeEach
    public void beforeEach() {
        board = TestUtils.emptyBoard();
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
    public void doesNotGenerateOpponentBishopMoves() {

        Board board = FEN.fromFEN("K7/1B6/8/8/8/8/6b1/7k w - - 0 1");

        Set<Move> moves = generator.generatePseudoLegalMoves(board);

        Assertions.assertEquals(7, moves.size());

    }

    @Test
    public void capturingOpponentPiecesEndsVector() {

        int startSquare = 28; //e4

        board.setPiece(10, PieceType.PAWN, false, true);
        board.setPiece(14, PieceType.KNIGHT, false, true);
        board.setPiece(42, PieceType.BISHOP, false, true);
        board.setPiece(46, PieceType.ROOK, false, true);

        assertLegalSquares(startSquare, Set.of(19, 21, 35, 37, 10, 14, 42, 46));

    }

    @Test
    public void reachingSameColourPiecesEndsVector() {

        int startSquare = 28; //e4

        board.setPiece(10, PieceType.PAWN, true, true);
        board.setPiece(14, PieceType.KNIGHT, true, true);
        board.setPiece(42, PieceType.BISHOP, true, true);
        board.setPiece(46, PieceType.ROOK, true, true);

        assertLegalSquares(startSquare, Set.of(19, 21, 35, 37));

    }

    private void assertLegalSquares(int startSquare, Set<Integer> expectedLegalSquares) {
        board.setPiece(startSquare, PieceType.BISHOP, true, true);
        Set<Integer> legalSquares = generator.generatePseudoLegalMoves(board).stream()
                .filter(move -> move.getStartSquare() == startSquare)
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expectedLegalSquares, legalSquares);
        board.unsetPiece(startSquare);
    }

}