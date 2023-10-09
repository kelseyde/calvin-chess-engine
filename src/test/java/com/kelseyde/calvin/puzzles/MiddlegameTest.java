package com.kelseyde.calvin.puzzles;

import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.bot.Bot;
import com.kelseyde.calvin.bot.CalvinBot;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.utils.NotationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collections;

@Disabled
public class MiddlegameTest {

    @Test
    public void testDontSacKnightForCenterPawn() {

        String fen = "r1bqkb1r/1pp1pppp/p1n2n2/8/2BPP3/2N2N2/PP3PPP/R1BQK2R b KQkq - 0 6";
        Bot bot = new CalvinBot();
        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(500);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("f6e4")));

    }

    @Test
    public void testDontSacKnightForCenterPawn2() {

        String fen = "rnbqk2r/ppp2ppp/3b1n2/4p3/3pP3/5N2/PPPPNPPP/1RBQKB1R w Kkq - 4 6";
        Bot bot = new CalvinBot();
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

        Bot bot = new CalvinBot();
        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(500);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("h1g1")));

    }

    @Test
    public void testDontSacYourQueenForPawn() {

        String fen = "rnbq1rk1/ppp2ppp/5n2/4p3/2P5/3P2P1/PQ2PP1P/R1B1KBNR b KQ - 2 9";

        Bot bot = new CalvinBot();
        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(500);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("d8d3")));

    }

    @Test
    public void testAnotherKnightSac() {

        String fen = "r2qkb1r/ppp1pppp/2n2n2/3p4/3PP3/2N2P1P/PPP2P2/R1BQKB1R b KQkq - 0 6";

        Bot bot = new CalvinBot();
        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(500);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("f6e4")));

    }

    @Test
    public void testDontRepeatWhenCompletelyWinning() {

        String fen = "7r/4b1p1/8/3BkP2/4N3/8/PPn2PP1/1R1R2K1 b - - 0 26";
        Bot bot = new CalvinBot();
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

}
