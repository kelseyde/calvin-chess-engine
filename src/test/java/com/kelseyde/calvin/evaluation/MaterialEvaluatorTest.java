package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.Notation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class MaterialEvaluatorTest {

    private final EngineConfig config = TestUtils.TST_CONFIG;

    public static final int[] SIMPLE_PIECE_VALUES = Arrays.stream(Piece.values()).mapToInt(Piece::getValue).toArray();


    @Test
    public void testStartingPosition() {

        Board board = new Board();
        int whiteScore = Material.fromBoard(board, true).sum(SIMPLE_PIECE_VALUES, 50);
        int blackScore = Material.fromBoard(board, false).sum(SIMPLE_PIECE_VALUES, 50);
        Assertions.assertEquals(0, whiteScore - blackScore);
        // 900 (queen) + 1000 (rooks) + 660 (bishops) + 640 (knights) + 800 (pawns) + 50 (bishop pair) = 4050
        Assertions.assertEquals(4050, whiteScore);
        Assertions.assertEquals(4050, blackScore);

    }

    @Test
    public void testManyBlunderedPieces() {

        Board board = new Board();
        board.makeMove(Notation.fromNotation("e2", "e4"));
        board.makeMove(Notation.fromNotation("e7", "e5"));
        board.makeMove(Notation.fromNotation("g1", "f3"));
        board.makeMove(Notation.fromNotation("f8", "a3"));
        board.makeMove(Notation.fromNotation("b1", "a3"));
        board.makeMove(Notation.fromNotation("d8", "g5"));
        board.makeMove(Notation.fromNotation("f3", "g5"));
        board.makeMove(Notation.fromNotation("f7", "f6"));
        board.makeMove(Notation.fromNotation("f1", "a6"));
        board.makeMove(Notation.fromNotation("b8", "a6"));
        board.makeMove(Notation.fromNotation("e1", "f1"));
        board.makeMove(Notation.fromNotation("f6", "g5"));
        board.makeMove(Notation.fromNotation("d1", "h5"));
        board.makeMove(Notation.fromNotation("g7", "g6"));
        board.makeMove(Notation.fromNotation("d2", "d3"));
        board.makeMove(Notation.fromNotation("g6", "h5"));

        // white score: (8 * 100) + 650 + 1000 + 10000 = 12450
        // black score: (8 * 100) + 970 + 1000 + 10000 = 12770
        // score = 12450 - 12770 = -320
        int whiteScore = Material.fromBoard(board, true).sum(SIMPLE_PIECE_VALUES, config.getBishopPairBonus());
        int blackScore = Material.fromBoard(board, false).sum(SIMPLE_PIECE_VALUES, config.getBishopPairBonus());
        Assertions.assertEquals(-320, whiteScore - blackScore);

    }

}