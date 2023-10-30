package com.kelseyde.calvin.puzzles;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.bot.Bot;
import com.kelseyde.calvin.bot.CalvinBot;
import com.kelseyde.calvin.utils.notation.NotationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collections;

@Disabled
public class SacrificeTest {

    @Test
    public void testBishopH6HugeAttack() {

        String fen = "r2q1rk1/5pp1/2bbp2p/3n4/2BP3R/p1N4Q/1PPB1PPP/5RK1 w - - 0 20";
        Bot bot = new CalvinBot();
        bot.setPosition(fen, Collections.emptyList());
        int thinkTime = bot.chooseThinkTime(61400, 61400, 1000, 1000);
        Move move = bot.think(thinkTime);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertTrue(
                move.matches(NotationUtils.fromCombinedNotation("d2h6"))
        );

    }

}
