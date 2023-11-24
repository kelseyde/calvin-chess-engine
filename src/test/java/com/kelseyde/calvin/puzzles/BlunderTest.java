package com.kelseyde.calvin.puzzles;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.bot.CalvinBot;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.ParallelSearcher;
import com.kelseyde.calvin.utils.notation.NotationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Not;

import java.util.Collections;
import java.util.List;

/**
 * These are all positions from Calvin's games on Lichess, where he commits a horrific blunder.
 */
@Disabled
public class BlunderTest {

    private final CalvinBot bot = new CalvinBot(new ParallelSearcher(new Board(), 3));

    @Test
    public void testDontSacKnightForCenterPawn() {

        String fen = "r1bqkb1r/1pp1pppp/p1n2n2/8/2BPP3/2N2N2/PP3PPP/R1BQK2R b KQkq - 0 6";
        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(500);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("f6e4")));

    }

    @Test
    public void testDontSacKnightForCenterPawn2() {

        String fen = "rnbqk2r/ppp2ppp/3b1n2/4p3/3pP3/5N2/PPPPNPPP/1RBQKB1R w Kkq - 4 6";
        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(500);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("f3d4")));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("e2d4")));

    }

    @Test
    public void testDontMoveRookBeforeCastling() {

        String fen = "r1b1kbnr/ppp2ppp/2n1p3/3q4/3P4/5N2/PPP2PPP/RNBQKB1R w KQkq - 0 5";

        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(500);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("h1g1")));

    }

    @Test
    public void testDontSacYourQueenForPawn() {

        String fen = "rnbq1rk1/ppp2ppp/5n2/4p3/2P5/3P2P1/PQ2PP1P/R1B1KBNR b KQ - 2 9";

        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(500);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("d8d3")));

    }

    @Test
    public void testAnotherKnightSac() {

        String fen = "r2qkb1r/ppp1pppp/2n2n2/3p4/3PP3/2N2P1P/PPP2P2/R1BQKB1R b KQkq - 0 6";

        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(500);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("f6e4")));

    }

    @Test
    public void testDontRepeatWhenCompletelyWinning() {

        String fen = "7r/4b1p1/8/3BkP2/4N3/8/PPn2PP1/1R1R2K1 b - - 0 26";
        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        bot.applyMove(NotationUtils.fromNotation("h8", "b8"));
        bot.applyMove(NotationUtils.fromNotation("e4", "c3"));
        bot.applyMove(NotationUtils.fromNotation("e7", "c5"));
        bot.applyMove(NotationUtils.fromNotation("c3", "e4"));
        bot.applyMove(NotationUtils.fromNotation("c5", "e7"));
        bot.applyMove(NotationUtils.fromNotation("e4", "c3"));
        bot.applyMove(NotationUtils.fromNotation("e7", "c5"));

        int thinkTimeMs = bot.chooseThinkTime(121959, 139090, 2000, 2000);
        Move move = bot.think(thinkTimeMs);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("c3e4")));
        System.out.println(new ResultCalculator().calculateResult(bot.getBoard()));

        bot.applyMove(NotationUtils.fromNotation("c3", "e4"));
        bot.applyMove(NotationUtils.fromNotation("c5", "e7"));
        System.out.println(new ResultCalculator().calculateResult(bot.getBoard()));

    }

    @Test
    public void testDontPushPawnShield() {

        String fen = "r1bq2k1/ppp1nppp/5b2/3pN3/3P1B2/2PB4/P1P2PPP/1R2Q1K1 b - - 2 14";
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(500);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("g7g5")));
    }

    @Test
    public void testDontTrapOwnQueen() {

        String fen = "r1b2rk1/ppp2ppp/2n1pn2/q5N1/2PP4/P2B1N2/1P1Q1PPP/R3K2R b KQ - 0 11";
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(1000);
        System.out.println(NotationUtils.toNotation(move));

        List<Move> goodMoves = List.of(
                NotationUtils.fromCombinedNotation("a5d2"),
                NotationUtils.fromCombinedNotation("h7h6"),
                NotationUtils.fromCombinedNotation("a5a6"),
                NotationUtils.fromCombinedNotation("a5b6"),
                NotationUtils.fromCombinedNotation("a7a6"));

        Assertions.assertTrue(goodMoves.stream().anyMatch(move::equals));

    }

    @Test
    public void testDontSacQueenForKnight() {

        String fen = "rnb1kb1r/ppN1pppp/5n2/8/P1qppB2/2P5/4NPPP/R2QK2R b KQkq - 1 11";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(52710, 47259, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("c4c7")));

    }

    @Test
    public void testIgnoreQueenKnightMatingAttack() {

        String fen = "r4rk1/pp2ppb1/3p1npp/4n3/4P3/1BN1BP1q/PPP2P1P/R2Q1R1K w - - 5 15";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(52710, 47259, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertTrue(move.matches(NotationUtils.fromCombinedNotation("f1g1")));

    }

    @Test
    public void testDontBlunderRook() {

        String fen = "r7/p5kp/1p3np1/2n1B3/2P1B3/5P1P/Pr3P2/3R2K1 b - - 0 31";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(82260, 67509, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertTrue(
                move.matches(NotationUtils.fromCombinedNotation("c5e4"))
            ||  move.matches(NotationUtils.fromCombinedNotation("e2e4"))
        );

    }

    @Test
    public void testDontBlunderRook2() {

        String fen = "1rb3k1/p1q3pp/4pr2/5p2/2pP4/1PQ3P1/4PPBP/2R1K2R b K - 0 21";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(82260, 67509, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("b8b3"))
        );

    }

    @Test
    public void testDontBlunderQueen() {

        String fen = "2b3k1/p5pp/4pr2/q4p2/3P4/2Q3P1/4PPBP/2R1K2R b K - 2 25";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(82260, 67509, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("a5c7"))
        );

    }

    @Test
    public void testDontBlunderQueenDiscoveredCheck() {

        String fen = "3r1rk1/1p4pp/1p6/1P2p1p1/P4nP1/2BnK1RP/2BQ1P1q/5R2 w - - 0 37";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(82260, 67509, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertTrue(
                move.matches(NotationUtils.fromCombinedNotation("c2d3"))
        );

    }

    @Test
    public void testDontBlunderQueen2() {

        String fen = "r3kbnr/pp1qpp1p/2np2p1/8/3pP3/2N2N2/PPP2PPP/R1BQ1RK1 w kq - 0 8";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(83500, 95900, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("d1d4")));

    }

    @Test
    public void testDontWalkIntoPin() {

        String fen = "r1b1kb1r/pppp1ppp/2n5/2q1P1B1/2Bp2n1/5N2/PPPNQPPP/R3K2R b KQkq - 6 8";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(57000, 60100, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("g4e5")));

    }

    @Test
    public void testDontBlunderBishop() {

        String fen = "r3kbnr/pp3ppp/2p5/8/4P1b1/5P2/PP2BP1P/RNBK3R b kq - 0 10";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(23400, 23100, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("g4f3")));

    }

    @Test
    public void testDontSacBishopForTwoPawns() {

        String fen = "r2q1rk1/1pp2ppp/p1b5/3P4/n1P5/4QN2/P2N1PPP/1R3RK1 b - - 0 19";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(15800, 15800, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("c6d5")));

    }

    @Test
    public void testDontBlunderRook3() {

        String fen = "3r4/4kp2/q1p2p1b/p1p1pP2/2P3Qp/1R2N2P/PP1r1PPK/1R6 w - - 4 41";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(61400, 61400, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("b3b6"))
        );

    }

    @Test
    public void testDontMoveKingBeforeCastling() {

        String fen = "r1bqk2r/ppp2ppp/2n1pb2/8/3P4/2PB1N2/PP3PPP/R1BQ1RK1 b kq - 3 9";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(22300, 22300, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("e8f8"))
        );

    }

    @Test
    public void testDontMoveKingBeforeCastling2() {

        String fen = "rn2kb1r/pp4pp/2p1p3/3n4/1qNP1p2/2N5/PPP1Q1PP/R1B2RK1 b kq - 5 14";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(43290, 41568, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("e8d8"))
        );

    }

    @Test
    public void testDontSacExchangeForSpeculativeCompensation() {

        String fen = "4r1k1/5pp1/7p/p3Bq2/Prb2P2/R1Q5/1P2p1PP/4R2K b - - 1 33";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(22300, 22300, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("e8e5"))
        );

    }

    @Test
    public void testCastleOutOfDanger() {

        String fen = "r3k2r/pppqppb1/4b2p/4P2Q/3pP3/2N4P/PPP1BP1P/2KR3R b kq - 2 14";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(22300, 22300, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("a8d8"))
        );

    }

    @Test
    public void testDontSacExchangeForSpeculativeCompensation2() {

        String fen = "5r2/kb4p1/p1p1p1qp/3pP3/3Pr3/1Q3NPP/PPR3K1/5R2 b - - 6 29";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(95300, 122599, 2000, 2000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("f8f3"))
        );

    }

    @Test
    public void testDontLetOpponentBuildBigCenter() {

        String fen = "4kb1r/3r1ppp/1q2p3/p2p1b2/2PP4/1P2P3/1B1NQPPP/R4RK1 b k - 0 17";
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(95300, 122599, 2000, 2000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("d5c4"))
        );

    }

    @Test
    public void testDontLosePawnInDrawnEndgame() {

        String fen = "8/8/4k3/6p1/1R2Bb1p/5P1P/1p1r1P2/4K3 b - - 9 57";
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(1000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("g5g4"))
        );

    }

    @Test
    public void testOneMoveToPreventMateThreat() {

        String fen = "4Q3/5p1k/5Pp1/5bP1/p7/3p1Nb1/q2B4/6K1 b - - 5 65";
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(200);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertTrue(
                move.matches(NotationUtils.fromCombinedNotation("g3d6"))
        );

    }

    @Test
    public void testDontSacExchangeAgain() {

        String fen = "5r2/kb4p1/p1p1p1qp/3pP3/3Pr3/1Q3NPP/PPR3K1/5R2 b - - 6 29";
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(1000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("f8f3"))
        );

    }

    @Test
    public void testAvoidForcedDraw() {

        String fen = "R7/2p1k2p/1r1pBpqb/3P4/1P2bP2/2P3P1/1B5K/4Q3 w - - 5 51";
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(1000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("h2g1"))
        );

    }

    @Test
    public void testDontSacBishopOnF3() {

        String fen = "7k/1b2Bp1p/5p2/p3pq2/1pP5/1r3P2/1P3QPP/4R1K1 b - - 1 33";
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(1000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("b7f3"))
        );

    }

    @Test
    public void testDontMoveBishopWhereItCanBeKicked() {

        String fen = "r1b1kb1r/ppp2ppp/2n2n2/q3p3/4P3/P1NP1N2/1P3PPP/R1BQKB1R b KQkq - 2 7";
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(1000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("f8c5"))
        );

    }

    @Test
    public void testDontOpenHFileToKing() {

        String fen = "4rrk1/p4pp1/1p5p/5PP1/2P5/P1PQpq2/4R1R1/2K5 b - - 0 35";
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(1000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertTrue(
                move.matches(NotationUtils.fromCombinedNotation("h6h5"))
        );

    }

    @Test
    public void testDefendAgainstHeavyPieceMatingAttack() {

        String fen = "4r1k1/p4pp1/1p6/5P2/2P1rq2/P1PQp3/4R3/1K5R b - - 6 39";
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(1000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertTrue(
                move.matches(NotationUtils.fromCombinedNotation("f4f5"))
        );

    }

    @Test
    public void testDontSacBishopTacticWhenKingIsInDanger() {

        String fen = "3r2k1/5rp1/p1R3N1/1p2b2P/6Q1/P2q4/5PP1/5RK1 b - - 4 34";
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(1000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("e5h2"))
        );

    }

    @Test
    public void testDontBlunderRepetitionMissingMateInThree() {

        String fen = "8/7k/5Q2/1P6/8/6P1/4KP1P/6R1 b - - 8 54";
        bot.setPosition(fen, Collections.emptyList());
        bot.applyMove(NotationUtils.fromNotation("h7", "g8"));
        bot.applyMove(NotationUtils.fromNotation("e2", "d3"));
        bot.applyMove(NotationUtils.fromNotation("g8", "h7"));
        bot.applyMove(NotationUtils.fromNotation("d3", "e2"));
        bot.applyMove(NotationUtils.fromNotation("h7", "g8"));
        bot.applyMove(NotationUtils.fromNotation("g1", "g2"));
        bot.applyMove(NotationUtils.fromNotation("g8", "h7"));
        Move move = bot.think(1000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("g2g1"))
        );

    }

    @Test
    public void testDontLetOpponentBuildQueenBishopBattery() {

        String fen = "5rk1/2q2pp1/b4b2/rpp2Q2/2P3P1/1B2PN1P/P5K1/3R1R2 b - - 2 25";
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(1000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertTrue(
                move.matches(NotationUtils.fromCombinedNotation("c7c8")) ||
                        move.matches(NotationUtils.fromCombinedNotation("a6a8"))
        );

    }

    @Test
    public void testDontBlockBishopAttackOnRookWithFreePawn() {

        String fen = "r1bq1rk1/2pn1pp1/p3pb1p/8/Pp1PB3/2N1PN2/1P3PPP/R2QK2R b KQ - 1 13";
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(1000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(
                move.matches(NotationUtils.fromCombinedNotation("c7c6"))
        );
        Assertions.assertTrue(
                move.matches(NotationUtils.fromCombinedNotation("a8b8")) ||
                        move.matches(NotationUtils.fromCombinedNotation("a8a7"))
        );

    }

    @Test
    public void testDontHangKnightForTwoPawns() {

        String fen = "3rrqk1/2pp1pp1/p6p/4P2Q/Bp2nP2/2P5/PP2R1PP/3R2K1 b - - 0 22";
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(1000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertTrue(
                move.matches(NotationUtils.fromCombinedNotation("e4c5"))
        );

    }

    @Test
    public void testDontThrowDrawnOppositeBishopsEndgame() {

        String fen = "8/8/1kp2P2/1p3K2/p1B5/P1P5/1b6/8 w - - 0 53";
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(1000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertTrue(
                move.matches(NotationUtils.fromCombinedNotation("f6f7"))
        );

    }

}
