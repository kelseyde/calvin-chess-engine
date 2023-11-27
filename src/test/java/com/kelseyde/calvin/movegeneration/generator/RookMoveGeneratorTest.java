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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RookMoveGeneratorTest {

    private final MoveGenerator generator = new MoveGenerator();

    private Board board;
    
    @BeforeEach
    public void beforeEach() {
        board = TestUtils.emptyBoard();
    }

    @Test
    public void testCapturingOpponentPiecesEndsVector() {

        String fen = "k7/8/4p3/8/2n1R1b1/8/4q3/K7 w - - 0 1";
        board = FEN.toBoard(fen);

        Set<Integer> legalSquares = generator.generateMoves(board).stream()
                .filter(move -> move.getStartSquare() == 28)
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        Assertions.assertEquals(Set.of(12, 20, 26, 27, 29, 30, 36, 44), legalSquares);

    }

    @Test
    public void doesNotGenerateOpponentRookMoves() {

        Board board = FEN.toBoard("K7/1R6/8/8/8/8/6r1/7k w - - 0 1");

        List<Move> moves = generator.generateMoves(board).stream()
                .filter(move -> board.pieceAt(move.getStartSquare()) == Piece.ROOK)
                .toList();

        Assertions.assertEquals(14, moves.size());

    }

    @Test
    public void testReachingSameColourPiecesEndsVector() {
        board.toggleSquare(Piece.KING, true, 0);
        board.toggleSquare(Piece.KING, false, 63);

        int startSquare = 28; //e4

        board.toggleSquare(Piece.PAWN, true, 12);
        board.toggleSquare(Piece.KNIGHT, true, 26);
        board.toggleSquare(Piece.BISHOP, true, 30);
        board.toggleSquare(Piece.ROOK, true, 44);
        board.recalculatePieces();

        assertLegalSquares(startSquare, Set.of(20, 27, 29, 36));

    }

    private void assertLegalSquares(int startSquare, Set<Integer> expectedLegalSquares) {
        board.toggleSquare(Piece.ROOK, true, startSquare);
        Set<Integer> legalSquares = generator.generateMoves(board).stream()
                .filter(move -> move.getStartSquare() == startSquare)
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expectedLegalSquares, legalSquares);
        board.toggleSquare(Piece.ROOK, true, startSquare);
    }

}