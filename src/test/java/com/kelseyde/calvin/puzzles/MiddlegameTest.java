package com.kelseyde.calvin.puzzles;

import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.bot.Bot;
import com.kelseyde.calvin.bot.CalvinBot;
import com.kelseyde.calvin.utils.NotationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class MiddlegameTest {

    @Test
    public void testDontSacKnightForCenterPawn() throws InterruptedException {

        String fen = "r1bqkb1r/1pp1pppp/p1n2n2/8/2BPP3/2N2N2/PP3PPP/R1BQK2R b KQkq - 0 6";
        Bot bot = new CalvinBot();
        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(3000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertFalse(move.matches(NotationUtils.fromCombinedNotation("f6e4")));

    }

}
