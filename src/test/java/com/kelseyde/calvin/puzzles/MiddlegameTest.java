package com.kelseyde.calvin.puzzles;

import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.bot.Bot;
import com.kelseyde.calvin.bot.CalvinBot;
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
        Move move = bot.think(1000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("f6e4")));

    }

    @Test
    public void testDontSacKnightForCenterPawn2() {

        String fen = "rnbqk2r/ppp2ppp/3b1n2/4p3/3pP3/5N2/PPPPNPPP/1RBQKB1R w Kkq - 4 6";
        Bot bot = new CalvinBot();
        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(1000);
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
        int thinkTime = bot.chooseThinkTime(165769, 154150, 2000, 2000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("h1g1")));

    }

    @Test
    public void testDontSacYourQueenForPawn() {

        String fen = "rnbq1rk1/ppp2ppp/5n2/4p3/2P5/3P2P1/PQ2PP1P/R1B1KBNR b KQ - 2 9";

        Bot bot = new CalvinBot();
        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(165320, 149989, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("d8d3")));

    }

}
