package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.NotationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EvaluatorTest {

    private Evaluator evaluator;

    @Test
    public void testSimpleUndoMove() {

        Board board = new Board();
        evaluator = new Evaluator(board);
        int score = evaluator.get();
        Move move = TestUtils.getLegalMove(board, "d2", "d4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertNotEquals(score, evaluator.get());
        board.unmakeMove();
        evaluator.unmakeMove();
        Assertions.assertEquals(score, evaluator.get());

    }

    @Test
    public void testPiecePlacementUpdate() {

        String fen = "8/8/3k4/3p4/8/3P1K2/8/8 w - - 0 1";
        Board board = FEN.fromFEN(fen);
        evaluator = new Evaluator(board);

        int overallScore = evaluator.get();

        Move move = TestUtils.getLegalMove(board, "d3", "d4");
        board.makeMove(move);
        evaluator.makeMove(move);
        board.unmakeMove();
        evaluator.unmakeMove();

        Assertions.assertEquals(overallScore, evaluator.get());

    }

    @Test
    public void testCapture() {

        String fen = "r7/8/3k4/2n5/8/3B4/8/6K1 b - - 0 1";
        Board board = FEN.fromFEN(fen);
        evaluator = new Evaluator(board);

        int overallScore = evaluator.get();
        System.out.println(overallScore);

        Move move = TestUtils.getLegalMove(board, "c5", "d3");
        board.makeMove(move);
        evaluator.makeMove(move);

        int scoreAfterCapture = evaluator.get();
        System.out.println(scoreAfterCapture);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());


        board.unmakeMove();
        evaluator.unmakeMove();

        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

    }

    @Test
    public void testPromotion() {

        String fen = "8/5KP1/8/8/8/8/5k2/8 w - - 0 1";
        Board board = FEN.fromFEN(fen);
        evaluator = new Evaluator(board);

        int score = evaluator.get();
        System.out.println(score);

        Move move = new Move(NotationUtils.fromNotation("g7"), NotationUtils.fromNotation("g8"), Move.PROMOTE_TO_QUEEN_FLAG);
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        int score2 = evaluator.get();
        System.out.println(score2);

        board.unmakeMove();
        evaluator.unmakeMove();
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

    }

    @Test
    public void testMultipleMakeMoves() {

        // Lasker vs Thomas, 1912
        Board board = new Board();
        evaluator = new Evaluator(board);

        Move move = TestUtils.getLegalMove(board, "d2", "d4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e7", "e6");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "g1", "f3");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "f7", "f5");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "b1", "c3");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "g8", "f6");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "c1", "g5");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "f8", "e7");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "g5", "f6");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e7", "f6");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e2", "e4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "f5", "e4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "c3", "e4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "b7", "b6");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "f3", "e5");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e8", "g8");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "f1", "d3");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "c8", "b7");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "d1", "h5");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "d8", "e7");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        // now the fun begins
        move = TestUtils.getLegalMove(board, "h5", "h7");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "g8", "h7");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e4", "f6");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "h7", "h6");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e5", "g4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "h6", "g5");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "h2", "h4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "g5", "f4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "g2", "g3");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "f4", "f3");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "d3", "e2");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "f3", "g2");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "h1", "h2");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "g2", "g1");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

    }

    @Test
    public void testMultipleUnmakeMove() {

        Board board = FEN.fromFEN("r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4");
        evaluator = new Evaluator(board);
        int startEval = evaluator.get();

        Move move = TestUtils.getLegalMove(board, "d2", "d4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e5", "d4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        board.unmakeMove();
        evaluator.unmakeMove();
        board.unmakeMove();
        evaluator.unmakeMove();

        System.out.println(evaluator.get());
        Assertions.assertEquals(evaluator.get(), new Evaluator(board).get());

    }

    @Test
    public void testCapturingWithPawnCreatesPasser() {

        Board board = new Board();
        evaluator = new Evaluator(board);

        Move move = TestUtils.getLegalMove(board, "e2", "e4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e7", "e5");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "d2", "d4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "f8", "b4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "c2", "c3");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e5", "d4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "c3", "b4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

    }

    @Test
    public void testCapturingEnemyPawnCreatesPasser() {

        Board board = new Board();
        evaluator = new Evaluator(board);

        Move move = TestUtils.getLegalMove(board, "c2", "c4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "d7", "d5");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "d1", "a4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "c8", "d7");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "a4", "a7");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "d5", "c4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "a7", "b7");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

    }

    @Test
    public void testPromotionChangesEndgameWeightScore() {

        Board board = new Board();
        evaluator = new Evaluator(board);

        Move move = TestUtils.getLegalMove(board, "e2", "e4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "g8", "f6");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "b1", "c3");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "d7", "d5");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e4", "e5");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "d5", "d4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e5", "f6");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "d4", "c3");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "f6", "e7");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "c3", "b2");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = new Move(NotationUtils.fromNotation("e7"), NotationUtils.fromNotation("d8"), Move.PROMOTE_TO_ROOK_FLAG);
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

    }

    @Test
    public void testEnPassant() {

        Board board = new Board();
        evaluator = new Evaluator(board);

        Move move = TestUtils.getLegalMove(board, "e2", "e4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "g8", "f6");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e4", "e5");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "d7", "d5");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e5", "d6");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

    }

    @Test
    public void testMopUpEvalUpdatedCorrectly() {

        String fen = "7r/4b1p1/8/3BkP2/4N3/8/PPn2PP1/1R1R2K1 b - - 0 26";

        Board board = FEN.fromFEN(fen);
        evaluator = new Evaluator(board);

        Move move = TestUtils.getLegalMove(board, "h8", "b8");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e4", "c3");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e7", "c5");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "c3", "e4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "c5", "e7");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e4", "c3");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e7", "c5");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "c3", "a4");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "c2", "a3");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "b2", "a3");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "b8", "b1");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "d1", "b1");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "e5", "d5");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());

        move = TestUtils.getLegalMove(board, "a4", "c5");
        board.makeMove(move);
        evaluator.makeMove(move);
        Assertions.assertEquals(new Evaluator(board).get(), evaluator.get());


    }

}