package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NNUETest {

    @Test
    @Disabled
    public void testBenchmark() {

        String startpos = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        benchmark("startpos", startpos);

        String lostPos = "rnbqkbnr/pppppppp/8/8/8/8/8/3QK3 w kq - 0 1";
        benchmark("lostpos", lostPos);

        String wonPos = "rn2k1nr/ppp2ppp/8/4P3/2P3b1/8/PP1B1KPP/RN1q1BR1 b kq - 1 10";
        benchmark("wonpos", wonPos);

    }

    private void benchmark(String name, String fen) {
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);
        System.out.printf("%s %s nnue %s%n", name, fen, nnue.evaluate());
    }

    @Test
    public void testSimpleMakeMove() {

         Board board = Board.from(FEN.STARTPOS);
         NNUE nnue = new NNUE(board);
         Move move = Move.fromUCI("e2e4");
         nnue.makeMove(board, move);
         board.makeMove(move);
         Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());

    }

    @Test
    public void testWhiteKingsideCastling() {

        String fen = "r1bqk1nr/ppppbppp/2n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4";
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        Move move = Move.fromUCI("e1g1", Move.CASTLE_FLAG);
        nnue.makeMove(board, move);
        board.makeMove(move);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        board.unmakeMove();
        nnue.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());

    }

    @Test
    public void testWhiteQueensideCastling() {

        String fen = "rnbq1rk1/pp3pbp/2pp1np1/3Pp3/4P3/2N1BP2/PPPQ2PP/R3KBNR w KQ - 0 8";
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        Move move = Move.fromUCI("e1c1", Move.CASTLE_FLAG);
        nnue.makeMove(board, move);
        board.makeMove(move);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        board.unmakeMove();
        nnue.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());

    }

    @Test
    public void testWhiteQueensideCastlingChess960() {

        UCI.Options.chess960 = true;
        String fen = "rnbq1rk1/pp3pbp/2pp1np1/3Pp3/4P3/2N1BP2/PPPQ2PP/R3KBNR w KQ - 0 8";
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        Move move = Move.fromUCI("e1a1", Move.CASTLE_FLAG);
        nnue.makeMove(board, move);
        board.makeMove(move);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        board.unmakeMove();
        nnue.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        UCI.Options.chess960 = false;

    }

    @Test
    public void testBlackKingsideCastling() {

        String fen = "rnbqk2r/pppp1ppp/5n2/4p3/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 0 4";
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        Move move = Move.fromUCI("e8g8", Move.CASTLE_FLAG);
        nnue.makeMove(board, move);
        board.makeMove(move);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        board.unmakeMove();
        nnue.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());

    }

    @Test
    public void testBlackQueensideCastling() {

        String fen = "r3kbnr/pppq1ppp/2np4/4p3/4P3/2N1BN2/PPPQ1PPP/R3KB1R b KQkq - 0 8";
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        Move move = Move.fromUCI("e8c8", Move.CASTLE_FLAG);
        nnue.makeMove(board, move);
        board.makeMove(move);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        board.unmakeMove();
        nnue.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());

    }

    @Test
    public void testKingCrossesAxisHorizontalFullRefresh() {

        Board board = Board.from("rnbqkbnr/ppp1pppp/8/3p4/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 2");
        NNUE nnue = new NNUE(board);
        Move move = Move.fromUCI("e1d2");
        nnue.makeMove(board, move);
        board.makeMove(move);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());

    }

    @Test
    public void testKingCrossedBucketFullRefresh() {

        Board board = Board.from("rnbq1bnr/ppppkppp/8/4p3/4P3/8/PPPPKPPP/RNBQ1BNR w - - 2 3");
        NNUE nnue = new NNUE(board);
        Move move = Move.fromUCI("e2e3");
        nnue.makeMove(board, move);
        board.makeMove(move);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());

    }

    @Test
    public void testKingCrossedBucketAndMirrorFullRefresh() {

        Board board = Board.from("rnbq1bnr/ppppkppp/8/4p3/4P3/8/PPPPKPPP/RNBQ1BNR w - - 2 3");
        NNUE nnue = new NNUE(board);
        Move move = Move.fromUCI("e2d3");
        nnue.makeMove(board, move);
        board.makeMove(move);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());

    }

    @Test
    public void testKingCrossedBucketAndMirrorFullRefreshBlackPerspective() {

        Board board = Board.from("rn1q1bnr/ppp2ppp/8/3p4/4P3/PNK2kpb/1PPP3P/RNB4R b - - 0 12");
        NNUE nnue = new NNUE(board);
        Move move = Move.fromUCI("f3e2");
        nnue.makeMove(board, move);
        board.makeMove(move);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());

    }

    @Test
    public void testCapture() {

        String fen = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2";
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        Move move = Move.fromUCI("e4d5");
        nnue.makeMove(board, move);
        board.makeMove(move);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        board.unmakeMove();
        nnue.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());

    }

    @Test
    public void testEnPassant() {

        String fen = "rnbqkbnr/ppp2ppp/4p3/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3";
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        Move move = Move.fromUCI("e5d6", Move.EN_PASSANT_FLAG);
        nnue.makeMove(board, move);
        board.makeMove(move);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        board.unmakeMove();
        nnue.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());

    }

    @Test
    public void testPromotion() {

        String fen = "rnbqkb1r/pP3ppp/4pn2/8/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 5";
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        Move move = Move.fromUCI("b7a8", Move.PROMOTE_TO_QUEEN_FLAG);
        nnue.makeMove(board, move);
        board.makeMove(move);
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
        board.unmakeMove();
        nnue.unmakeMove();
        Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());

    }

    @Test
    public void testSymmetry() {

        String fen1 = "r1bqkb1r/1ppp1ppp/p1n2n2/4p3/B3P3/5N2/PPPP1PPP/RNBQ1RK1 b kq - 3 5";
        String fen2 = "rnbq1rk1/pppp1ppp/5n2/b3p3/4P3/P1N2N2/1PPP1PPP/R1BQKB1R w KQ - 3 5";

        Board board1 = FEN.toBoard(fen1);
        Board board2 = FEN.toBoard(fen2);
        NNUE nnue1 = new NNUE(board1);
        NNUE nnue2 = new NNUE(board2);
        Assertions.assertEquals(nnue1.evaluate(), nnue2.evaluate());

    }

    @Test
    public void testMakeUnmakeNullMove() {

        String fen = "r2q1rk1/pp3pp1/2pp1n1p/2bNp2b/2BnP2B/2PP1N1P/PP3PP1/R2Q1RK1 w - - 0 12";
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);
        int eval1 = nnue.evaluate();

        board.makeNullMove();
        nnue = new NNUE(board);
        int eval2 = nnue.evaluate();

        Assertions.assertEquals(eval1, eval2);

        NNUE nnue2 = new NNUE(board);
        Assertions.assertEquals(eval1, nnue2.evaluate());

        board.unmakeNullMove();
        nnue = new NNUE(board);
        int eval3 = nnue.evaluate();
        Assertions.assertEquals(eval1, eval3);
        Assertions.assertEquals(eval2, eval3);

    }

    @Test
    public void testIncrementalEvaluationConsistency() {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Board board = FEN.toBoard(fen);
        NNUE nnue = new NNUE(board);

        for (int i = 0; i < 10; i++) {
            Move move = new MoveGenerator().generateMoves(board).get(0);
            nnue.makeMove(board, move);
            board.makeMove(move);
            Assertions.assertEquals(nnue.evaluate(), new NNUE(board).evaluate());
            board.unmakeMove();
            nnue.unmakeMove();
        }
    }

    @Test
    public void testLazyUpdatesAreNotReapplied() {

        Board board = Board.from(FEN.STARTPOS);
        NNUE nnue = new NNUE(board);

        Move m1 = Move.fromUCI("e2e4", Move.PAWN_DOUBLE_MOVE_FLAG);
        nnue.makeMove(board, m1);
        board.makeMove(m1);

        Move m2 = Move.fromUCI("e7e5", Move.PAWN_DOUBLE_MOVE_FLAG);
        nnue.makeMove(board, m2);
        board.makeMove(m2);

        Move m3 = Move.fromUCI("g1f3");
        nnue.makeMove(board, m3);
        board.makeMove(m3);

        Move m4 = Move.fromUCI("b8c6");
        nnue.makeMove(board, m4);
        board.makeMove(m4);

        Move m5 = Move.fromUCI("f1c4");
        nnue.makeMove(board, m5);
        board.makeMove(m5);

        Move m6 = Move.fromUCI("g8f6");
        nnue.makeMove(board, m6);
        board.makeMove(m6);

        NNUE newNnue = new NNUE(board);
        Assertions.assertEquals(nnue.evaluate(), newNnue.evaluate());
        // apply the lazy updates again
        Assertions.assertEquals(nnue.evaluate(), newNnue.evaluate());

    }

    @Test
    public void testDebugLazyUpdates() {

        Board board = Board.from(FEN.STARTPOS);
        NNUE nnue = new NNUE(board);

        // moves: [g2g4, g8f6, e2e3, a7a6, e1e2, f6g8]
        List<Move> moves = List.of(
                Move.fromUCI("g2g4"),
                Move.fromUCI("g8f6"),
                Move.fromUCI("e2e3"),
                Move.fromUCI("a7a6"),
                Move.fromUCI("e1e2"),
                Move.fromUCI("f6g8")
        );

        for (Move move : moves) {
            nnue.makeMove(board, move);
            board.makeMove(move);
        }

        NNUE newNnue = new NNUE(board);
        Assertions.assertEquals(nnue.evaluate(), newNnue.evaluate());

    }

    @Test
    public void testFindBugs() {

        Board board = Board.from(FEN.STARTPOS);
        NNUE nnue = new NNUE(board);

        int tries = 0;
        int ply = 0;

        List<Move> moveHistory = new ArrayList<>();

//        while (tries < 1000000) {
        while (ply < 100) {

            List<Move> moves = new MoveGenerator().generateMoves(board);
            if (moves.isEmpty()) {
                System.out.println("No moves available");
                break;
            }

            Move move = moves.get(new Random().nextInt(moves.size()));
            System.out.println("Move: " + Move.toUCI(move) + " " + ply);
            nnue.makeMove(board, move);
            board.makeMove(move);
            moveHistory.add(move);

            int evaluation = nnue.evaluate();
            int newEvaluation = new NNUE(board).evaluate();
            if (evaluation != newEvaluation) {
                System.out.println(moveHistory.stream().map(Move::toUCI).toList());
                System.out.println("Evaluation: " + evaluation);
                System.out.println("New Evaluation: " + newEvaluation);
                Assertions.fail("NNUE evaluation mismatch");
            }
            ply++;

        }
            tries++;
//        }

    }


    @AfterAll
    public static void tearDown() {
        UCI.Options.chess960 = false;
    }

}