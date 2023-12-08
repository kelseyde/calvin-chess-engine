package com.kelseyde.calvin.evaluation.kingsafety;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.evaluation.Evaluator;
import com.kelseyde.calvin.evaluation.score.Score;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KingSafetyTest {

    private final EngineConfig config = TestUtils.TST_CONFIG;
    private final Evaluator evaluator = new Evaluator(config);

    @Test
    public void testSimpleCastledKingFullPawnShield() {

        String fen = "rnbq1rk1/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQ1RK1 w - - 6 5";
        Board board = FEN.toBoard(fen);
        evaluator.evaluate(board);
        Score score = evaluator.getScore();
        Assertions.assertEquals(0, score.getWhiteKingSafetyScore());

    }

    @Test
    public void testH3Hook() {

        String fen = "rnbq1rk1/pppp1ppp/5n2/2b1p3/2B1P3/5N1P/PPPP1PP1/RNBQ1RK1 b - - 0 5";
        Board board = FEN.toBoard(fen);
        evaluator.evaluate(board);
        Score score = evaluator.getScore();
        Assertions.assertEquals(-10, score.getWhiteKingSafetyScore());

    }

    @Test
    public void testG4Push() {

        String fen = "rnbq1rk1/pppp1ppp/5n2/2b1p3/2B1P1P1/5N2/PPPP1P1P/RNBQ1RK1 b - g3 0 5";
        Board board = FEN.toBoard(fen);
        evaluator.evaluate(board);
        Score score = evaluator.getScore();
        Assertions.assertEquals(-25, score.getWhiteKingSafetyScore());

    }

    @Test
    public void testPawnShieldPawnPushedToFifthRank() {

        String fen = "2kr1bnr/pppq1ppp/2n1p3/1P1p1b2/3P1B2/2N5/P1PQPPPP/2KR1BNR b - - 0 7";
        Board board = FEN.toBoard(fen);
        evaluator.evaluate(board);
        Score score = evaluator.getScore();
        Assertions.assertEquals(-75, score.getWhiteKingSafetyScore());

    }

    @Test
    public void testQueensideCastleKb1IncreasesKingSafety() {

        String fen = "1k1r1bnr/pppq1ppp/2n1p3/1P1p1b2/3P1B2/2N5/P1PQPPPP/1K1R1BNR b - - 2 8";
        Board board = FEN.toBoard(fen);
        evaluator.evaluate(board);
        Score score = evaluator.getScore();
        Assertions.assertEquals(-50, score.getWhiteKingSafetyScore());

    }

    @Test
    public void testAdjacentSemiOpenFile() {

        String fen = "rnbq1rk1/pp1pbppp/2p2n2/4p3/2B1P3/3P1N2/PPP3PP/RNBQ1RK1 w Qq - 0 1";
        Board board = FEN.toBoard(fen);
        evaluator.evaluate(board);
        Score score = evaluator.getScore();
        Assertions.assertEquals(-10, score.getWhiteKingSafetyScore());

    }

    @Test
    public void testKingSemiOpenFile() {

        String fen = "rnbq1rk1/pp1pbppp/2p2n2/4p3/2B1P3/3P1N2/PPP2P1P/RNBQ1RK1 w Qq - 0 1";
        Board board = FEN.toBoard(fen);
        evaluator.evaluate(board);
        Score score = evaluator.getScore();
        Assertions.assertEquals(-15, score.getWhiteKingSafetyScore());

    }

    @Test
    public void testAdjacentOpenFile() {

        String fen = "rnbqkr2/pp1pbp1p/2p2n2/4p3/2B1P3/3P1N2/PPP2P1P/RNBQ1R1K w Qq - 0 1";
        Board board = FEN.toBoard(fen);
        evaluator.evaluate(board);
        Score score = evaluator.getScore();
        Assertions.assertEquals(-25, score.getWhiteKingSafetyScore());

    }

    @Test
    public void testKingOpenFile() {

        String fen = "rnbqkr2/pp1pbp1p/2p2n2/4p3/2B1P3/3P1N2/PPP2P1P/RNBQ1RK1 w Qq - 0 1";
        Board board = FEN.toBoard(fen);
        evaluator.evaluate(board);
        Score score = evaluator.getScore();
        Assertions.assertEquals(-40, score.getWhiteKingSafetyScore());

    }

    @Test
    public void testThreeOpenFiles() {
        String fen = "rnbq1r2/ppkpb3/2p2n2/4p3/2B1P3/3P1N2/PPP5/RNBQ1RK1 w Qa - 0 1";
        Board board = FEN.toBoard(fen);
        evaluator.evaluate(board);
        Score score = evaluator.getScore();
        Assertions.assertEquals(-90, score.getWhiteKingSafetyScore());
    }

    @Test
    public void testEndgameWeight() {

        String fen = "rn1qkbnr/pbp1p3/1p1p4/6pp/8/2P2NP1/PP1PP1BP/RNBQ1RK1 w Qkq - 0 1";
        Board board = FEN.toBoard(fen);
        evaluator.evaluate(board);
        Score score = evaluator.getScore();
        Assertions.assertEquals(-35, score.getWhiteKingSafetyScore());

        fen = "rn2kbnr/pbp1p3/1p1p4/6pp/8/2P2NP1/PP1PP1BP/RNBQ1RK1 w Qkq - 0 1";
        board = FEN.toBoard(fen);
        evaluator.evaluate(board);
        score = evaluator.getScore();
        Assertions.assertEquals(-17, score.getWhiteKingSafetyScore());

    }

}