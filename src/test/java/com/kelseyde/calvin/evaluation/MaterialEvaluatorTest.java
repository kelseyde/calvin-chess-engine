package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.evaluation.material.MaterialEvaluator;
import com.kelseyde.calvin.utils.NotationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MaterialEvaluatorTest {

    private final MaterialEvaluator evaluator = new MaterialEvaluator();

    @Test
    public void testStartingPosition() {

        Board board = new Board();
        int score = evaluator.evaluate(board, 1);
        Assertions.assertEquals(0, score);

    }

    @Test
    public void testManyBlunderedPieces() {

        Board board = new Board();
        board.makeMove(NotationUtils.fromNotation("e2", "e4", PieceType.PAWN));
        board.makeMove(NotationUtils.fromNotation("e7", "e5", PieceType.PAWN));
        board.makeMove(NotationUtils.fromNotation("g1", "f3", PieceType.KNIGHT));
        board.makeMove(NotationUtils.fromNotation("f8", "a3", PieceType.BISHOP));
        board.makeMove(NotationUtils.fromNotation("b1", "a3", PieceType.KNIGHT));
        board.makeMove(NotationUtils.fromNotation("d8", "g5", PieceType.QUEEN));
        board.makeMove(NotationUtils.fromNotation("f3", "g5", PieceType.KNIGHT));
        board.makeMove(NotationUtils.fromNotation("f7", "f6", PieceType.PAWN));
        board.makeMove(NotationUtils.fromNotation("f1", "a6", PieceType.BISHOP));
        board.makeMove(NotationUtils.fromNotation("b8", "a6", PieceType.KNIGHT));
        board.makeMove(NotationUtils.fromNotation("e1", "f1", PieceType.KING));
        board.makeMove(NotationUtils.fromNotation("f6", "g5", PieceType.PAWN));
        board.makeMove(NotationUtils.fromNotation("d1", "h5", PieceType.QUEEN));
        board.makeMove(NotationUtils.fromNotation("g7", "g6", PieceType.PAWN));
        board.makeMove(NotationUtils.fromNotation("d2", "d3", PieceType.PAWN));
        board.makeMove(NotationUtils.fromNotation("g6", "h5", PieceType.PAWN));

        // white score: (8 * 100) + 650 + 1000 + 10000 = 12450
        // black score: (8 * 100) + 970 + 1000 + 10000 = 12770
        // score = 12450 - 12770 = -320
        int score = evaluator.evaluate(board, 1);
        Assertions.assertEquals(-320, score);

        board.setWhiteToMove(!board.isWhiteToMove());
        Assertions.assertEquals(320, evaluator.evaluate(board, 1));

    }

}