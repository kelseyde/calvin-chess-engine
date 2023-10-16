package com.kelseyde.calvin.puzzles;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.bot.Bot;
import com.kelseyde.calvin.bot.CalvinBot;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collections;

@Disabled
public class OpeningTest {

    @Test
    public void openingMove() {

        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Bot bot = new CalvinBot();
        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(3000);
        System.out.println(NotationUtils.toNotation(move));

    }

    @Test
    public void test() {

        String fen = "r2qkbnr/pppnp2p/4b1p1/1B1p1pN1/3P4/2N1P3/PPP2PPP/R1BQK2R b KQkq - 5 6";
        Bot bot = new CalvinBot();
        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(400);
        System.out.println(NotationUtils.toNotation(move));

    }

}
