package com.kelseyde.calvin.evaluation.pawnstructure;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PawnStructureEvaluatorTest {

    private final PawnStructureEvaluator evaluator = new PawnStructureEvaluator();

    @Test
    public void testSinglePassedPawn() {

        // White
        String fen = "4k3/8/8/8/8/8/P7/4K3 w - - 0 1";
        Board board = FEN.fromFEN(fen);
        Assertions.assertEquals(15, score(board));

        fen = "4k3/8/8/8/8/P7/8/4K3 w - - 0 1";
        board = FEN.fromFEN(fen);
        Assertions.assertEquals(15, score(board));

        fen = "4k3/8/8/8/P7/8/8/4K3 w - - 0 1";
        board = FEN.fromFEN(fen);
        Assertions.assertEquals(30, score(board));

        fen = "4k3/8/8/P7/8/8/8/4K3 w - - 0 1";
        board = FEN.fromFEN(fen);
        Assertions.assertEquals(50, score(board));

        fen = "4k3/8/P7/8/8/8/8/4K3 w - - 0 1";
        board = FEN.fromFEN(fen);
        Assertions.assertEquals(80, score(board));

        fen = "4k3/P7/8/8/8/8/8/4K3 w - - 0 1";
        board = FEN.fromFEN(fen);
        Assertions.assertEquals(120, score(board));

        // Black
        fen = "4k3/p7/8/8/8/8/8/4K3 w - - 0 1";
        board = FEN.fromFEN(fen);
        Assertions.assertEquals(-15, score(board));

        fen = "4k3/8/p7/8/8/8/8/4K3 w - - 0 1";
        board = FEN.fromFEN(fen);
        Assertions.assertEquals(-15, score(board));

        fen = "4k3/8/8/p7/8/8/8/4K3 w - - 0 1";
        board = FEN.fromFEN(fen);
        Assertions.assertEquals(-30, score(board));

        fen = "4k3/8/8/8/p7/8/8/4K3 w - - 0 1";
        board = FEN.fromFEN(fen);
        Assertions.assertEquals(-50, score(board));

        fen = "4k3/8/8/8/8/p7/8/4K3 w - - 0 1";
        board = FEN.fromFEN(fen);
        Assertions.assertEquals(-80, score(board));

        fen = "4k3/8/8/8/8/8/p7/4K3 w - - 0 1";
        board = FEN.fromFEN(fen);
        Assertions.assertEquals(-120, score(board));

    }

    @Test
    public void testSingleProtectedPassedPawn() {

        String fen = "4k3/2p5/Pp6/1P6/8/8/8/4K3 w - - 0 1";
        Board board = FEN.fromFEN(fen);
        //80 + (25)
        Assertions.assertEquals(105, score(board));

        fen = "4k3/8/6p1/6Pp/5P2/8/8/4K3 w - - 0 1";
        board = FEN.fromFEN(fen);
        //30 + (25)
        Assertions.assertEquals(-55, score(board));

    }

    @Test
    public void testDoubleProtectedPassedPawn() {

        String fen = "4k3/8/3p3p/4pPp1/4P1P1/7P/8/4K3 w - - 0 1";
        Board board = FEN.fromFEN(fen);
        //50 + (2 * 25)
        Assertions.assertEquals(100, score(board));

        fen = "4k3/8/8/8/1p1p4/1PpP4/P3P3/4K3 w - - 0 1";
        board = FEN.fromFEN(fen);
        //80 + (2 * 25)
        Assertions.assertEquals(-130, score(board));

    }

    @Test
    public void testIsolatedPawnPenalty() {

        String fen = "4k3/4pppp/8/8/8/8/3PPP1P/4K3 w - - 0 1";
        Board board = FEN.fromFEN(fen);
        Assertions.assertEquals(-10, score(board));

        fen = "4k3/2pp2pp/8/8/8/8/2P1PP1P/4K3 w - - 0 1";
        board = FEN.fromFEN(fen);
        Assertions.assertEquals(-25, score(board));

        fen = "4k3/1p1p1p1p/8/8/8/8/1PP2PP1/4K3 w - - 0 1";
        board = FEN.fromFEN(fen);
        Assertions.assertEquals(75, score(board));

    }

    @Test
    public void testDoubledPawnsPenalty() {

        String fen = "4k3/1pppppp1/8/8/8/2P2P2/2PP1PP1/4K3 w - - 0 1";
        Board board = FEN.fromFEN(fen);

        // 2 sets of doubled pawns = -20 * 2
        Assertions.assertEquals(-40, score(board));

    }

    @Test
    public void testDoubledIsolatedPawnsPenalty() {

        String fen = "4k3/pppppppp/8/8/8/P1P1P1P1/P1P1P1P1/4K3 w - - 0 1";

        Board board = FEN.fromFEN(fen);

        Assertions.assertEquals(-170, score(board));

    }

    private int score(Board board) {
        int modifier = board.isWhiteToMove() ? 1 : -1;
        return modifier * (evaluator.evaluate(board, true) - evaluator.evaluate(board, false));
    }

}