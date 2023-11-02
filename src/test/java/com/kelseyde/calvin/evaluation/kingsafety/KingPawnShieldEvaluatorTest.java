package com.kelseyde.calvin.evaluation.kingsafety;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.KingSafety;
import com.kelseyde.calvin.evaluation.Material;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KingPawnShieldEvaluatorTest {

    @Test
    public void testSimpleCastledKingFullPawnShield() {

        String fen = "rnbq1rk1/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQ1RK1 w - - 6 5";
        Board board = FEN.fromFEN(fen);

        int eval = KingSafety.score(board, new Material(1, 1, 1, 1, 1), 1.0f, true);

        Assertions.assertEquals(0, eval);

    }

    @Test
    public void testH3Hook() {

        String fen = "rnbq1rk1/pppp1ppp/5n2/2b1p3/2B1P3/5N1P/PPPP1PP1/RNBQ1RK1 b - - 0 5";
        Board board = FEN.fromFEN(fen);

        int eval = KingSafety.score(board, new Material(1, 1, 1, 1, 1), 1.0f, true);

        Assertions.assertEquals(-10, eval);

    }

    @Test
    public void testG4Push() {

        String fen = "rnbq1rk1/pppp1ppp/5n2/2b1p3/2B1P1P1/5N2/PPPP1P1P/RNBQ1RK1 b - g3 0 5";
        Board board = FEN.fromFEN(fen);

        int eval = KingSafety.score(board, new Material(1, 1, 1, 1, 1), 1.0f, true);

        Assertions.assertEquals(-25, eval);

    }

    @Test
    public void testPawnShieldPawnPushedToFifthRank() {

        String fen = "2kr1bnr/pppq1ppp/2n1p3/1P1p1b2/3P1B2/2N5/P1PQPPPP/2KR1BNR b - - 0 7";
        Board board = FEN.fromFEN(fen);

        int eval = KingSafety.score(board, new Material(1, 1, 1, 1, 1), 1.0f, true);

        // pawn shield: 0 + 25 + 50
        Assertions.assertEquals(-75, eval);

    }

    @Test
    public void testQueensideCastleKb1IncreasesKingSafety() {

        String fen = "1k1r1bnr/pppq1ppp/2n1p3/1P1p1b2/3P1B2/2N5/P1PQPPPP/1K1R1BNR b - - 2 8";
        Board board = FEN.fromFEN(fen);

        int eval = KingSafety.score(board, new Material(1, 1, 1, 1, 1), 1.0f, true);

        // pawn shield: 0 + 50
        // pawn storm: 0
        Assertions.assertEquals(-50, eval);

    }

    @Test
    public void testAdjacentSemiOpenFile() {

        String fen = "rnbq1rk1/pp1pbppp/2p2n2/4p3/2B1P3/3P1N2/PPP3PP/RNBQ1RK1 w Qq - 0 1";
        Board board = FEN.fromFEN(fen);

        int eval = KingSafety.score(board, new Material(1, 1, 1, 1, 1), 1.0f, true);

        // adjacent semi-open file: -10
        Assertions.assertEquals(-10, eval);

    }

    @Test
    public void testKingSemiOpenFile() {

        String fen = "rnbq1rk1/pp1pbppp/2p2n2/4p3/2B1P3/3P1N2/PPP2P1P/RNBQ1RK1 w Qq - 0 1";
        Board board = FEN.fromFEN(fen);

        int eval = KingSafety.score(board, new Material(8, 2, 2, 2, 1), 1.0f, true);

        // king semi-open file: -15
        Assertions.assertEquals(-15, eval);

    }

    @Test
    public void testAdjacentOpenFile() {

        String fen = "rnbqkr2/pp1pbp1p/2p2n2/4p3/2B1P3/3P1N2/PPP2P1P/RNBQ1R1K w Qq - 0 1";
        Board board = FEN.fromFEN(fen);

        int eval = KingSafety.score(board, new Material(1, 1, 1, 1, 1), 1.0f, true);

        // adjacent open file: -10 -15
        Assertions.assertEquals(-25, eval);

    }

    @Test
    public void testKingOpenFile() {

        String fen = "rnbqkr2/pp1pbp1p/2p2n2/4p3/2B1P3/3P1N2/PPP2P1P/RNBQ1RK1 w Qq - 0 1";
        Board board = FEN.fromFEN(fen);

        int eval = KingSafety.score(board, new Material(1, 1, 1, 1, 1), 1.0f, true);

        // open king-file: -15-25
        Assertions.assertEquals(-40, eval);

    }

    @Test
    public void testThreeOpenFiles() {
        String fen = "rnbq1r2/ppkpb3/2p2n2/4p3/2B1P3/3P1N2/PPP5/RNBQ1RK1 w Qa - 0 1";
        Board board = FEN.fromFEN(fen);

        int eval = KingSafety.score(board, new Material(1, 1, 1, 1, 1), 1.0f, true);

        // open king-file: -40 - 25 - 25
        Assertions.assertEquals(-90, eval);
    }

    @Test
    public void testEndgameWeight() {

        String fen = "rn1qkbnr/pbp1p3/1p1p4/6pp/8/2P2NP1/PP1PP1BP/RNBQ1RK1 w Qkq - 0 1";
        Board board = FEN.fromFEN(fen);

        float phase = 0.75f;

        int eval = KingSafety.score(board, new Material(1, 1, 1, 1, 1), phase, true);

        // pawn shield: -10
        // open adjacent file: -10 -15
        // total: -35
        // phase: 0.75, 35*0.75=-26.25
        Assertions.assertEquals(-26, eval);

        // remove opponent queen, -26.25 * 0.6 = -15.75
        eval = KingSafety.score(board, new Material(1, 1, 1, 1, 0), phase, true);
        Assertions.assertEquals(-15, eval);

    }

}