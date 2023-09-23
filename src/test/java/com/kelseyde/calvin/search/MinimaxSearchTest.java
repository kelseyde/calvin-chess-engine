package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.search.engine.MinimaxSearch;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MinimaxSearchTest {

    private final Search search = new MinimaxSearch(TestEvaluators.ALL_EVALUATORS);

    @Test
    public void testFreeQueenWhite() {

        Board board = TestUtils.emptyBoard();
        board.setPiece(0, PieceType.KING, true, true);
        board.setPiece(63, PieceType.KING, false, true);

        board.setPiece(27, PieceType.KNIGHT, true, true);
        board.setPiece(18, PieceType.ROOK, true, true);
        board.setPiece(44, PieceType.QUEEN, false, true);

        SearchResult result = search.search(board, 2);

        Move bestMove = Move.builder().startSquare(27).endSquare(44).build();
        Assertions.assertTrue(bestMove.matches(result.move()));

    }

    @Test
    public void testFreeQueenBlack() {

        Board board = TestUtils.emptyBoard();
        board.setPiece(0, PieceType.KING, false, true);
        board.setPiece(63, PieceType.KING, true, true);

        board.setPiece(27, PieceType.KNIGHT, false, true);
        board.setPiece(18, PieceType.ROOK, false, true);
        board.setPiece(44, PieceType.QUEEN, true, true);
        board.setWhiteToMove(false);

        SearchResult result = search.search(board, 2);

        Move bestMove = Move.builder().startSquare(27).endSquare(44).build();
        Assertions.assertTrue(bestMove.matches(result.move()));

    }

    @Test
    public void testFreeQueenScandinavian() {

        Board board = new Board();
        board.makeMove(NotationUtils.fromNotation("e2", "e4", PieceType.PAWN));
        board.makeMove(NotationUtils.fromNotation("d7", "d5", PieceType.PAWN));
        board.makeMove(NotationUtils.fromNotation("e4", "d5", PieceType.PAWN));
        board.makeMove(NotationUtils.fromNotation("d8", "d5", PieceType.QUEEN));
        board.makeMove(NotationUtils.fromNotation("b1", "c3", PieceType.KNIGHT));
        board.makeMove(NotationUtils.fromNotation("g8", "f6", PieceType.KNIGHT));

        SearchResult result = search.search(board, 3);

        Move bestMove = NotationUtils.fromNotation("c3", "d5");
        Assertions.assertTrue(bestMove.matches(result.move()));

    }

    @Test
    public void testFreeQueenScandinavianReverse() {

        Board board = new Board();
        board.makeMove(NotationUtils.fromNotation("a2", "a3", PieceType.PAWN));
        board.makeMove(NotationUtils.fromNotation("e7", "e5", PieceType.PAWN));
        board.makeMove(NotationUtils.fromNotation("d2", "d4", PieceType.PAWN));
        board.makeMove(NotationUtils.fromNotation("e5", "d4", PieceType.PAWN));
        board.makeMove(NotationUtils.fromNotation("d1", "d4", PieceType.QUEEN));
        board.makeMove(NotationUtils.fromNotation("b8", "c6", PieceType.KNIGHT));
        board.makeMove(NotationUtils.fromNotation("g1", "f3", PieceType.KNIGHT));

        SearchResult result = search.search(board, 3);

        Move bestMove = NotationUtils.fromNotation("c6", "d4");
        Assertions.assertTrue(bestMove.matches(result.move()));

    }

    @Test
    public void testFreeBishopBlack() {
        Board board = new Board();
        board.makeMove(NotationUtils.fromNotation("e2", "e4", PieceType.PAWN));
        board.makeMove(NotationUtils.fromNotation("b8", "c6", PieceType.PAWN));
        board.makeMove(NotationUtils.fromNotation("f1", "a6", PieceType.PAWN));
        SearchResult result = search.search(board, 3);

        Move bestMove = NotationUtils.fromNotation("b7", "a6");
        Assertions.assertTrue(bestMove.matches(result.move()));
    }

    @Disabled
    @Test
    public void testSimpleQueenCheckmate() {

        Board board = TestUtils.emptyBoard();
        board.setPiece(48, PieceType.KING, false, true);
        board.setPiece(42, PieceType.KING, true, true);
        board.setPiece(1, PieceType.QUEEN, true, true);

        SearchResult result = search.search(board, 4);
        System.out.printf("Selected move %s, eval %s", NotationUtils.toNotation(result.move()), result.eval());

        Move checkmate = Move.builder().startSquare(1).endSquare(49).pieceType(PieceType.QUEEN).build();
        Assertions.assertTrue(checkmate.matches(result.move()));

    }

    @Disabled
    @Test
    public void testSimpleQueenCheckmateBlack() {

        Board board = TestUtils.emptyBoard();
        board.setPiece(48, PieceType.KING, true, true);
        board.setPiece(42, PieceType.KING, false, true);
        board.setPiece(1, PieceType.QUEEN, false, true);
        board.setWhiteToMove(false);

        SearchResult result = search.search(board, 4);

        Move checkmate = Move.builder().startSquare(1).endSquare(49).pieceType(PieceType.QUEEN).build();
        Assertions.assertTrue(checkmate.matches(result.move()));

    }

}
