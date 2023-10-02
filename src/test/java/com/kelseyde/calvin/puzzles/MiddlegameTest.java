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

}
