package com.kelseyde.calvin.puzzles;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.bot.Bot;
import com.kelseyde.calvin.bot.CalvinBot;
import com.kelseyde.calvin.utils.NotationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collections;

@Disabled
public class OpeningTest {

    @Test
    public void doesNotMoveTheKingBeforeCastling() {

        String fen = "rnbqk2r/ppp2ppp/3bpn2/1B1p4/3P1B2/2P1PN2/PP3PPP/RN1QK2R b KQkq - 2 6";
        Bot bot = new CalvinBot();
        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(3000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertNotEquals(NotationUtils.fromNotation("e8", "f8"), move);

    }

    @Test
    public void doesNotMoveTheKingBeforeCastling2() {

        String fen = "r3kb1r/p1ppqppp/b1p5/3nP3/2P5/8/PP2QPPP/RNB1KB1R w KQkq - 1 9";
        Bot bot = new CalvinBot();
        bot.newGame();
        bot.setPosition(fen, Collections.emptyList());
        Move move = bot.think(3000);
        System.out.println(NotationUtils.toNotation(move));
        Assertions.assertNotEquals(NotationUtils.fromNotation("e1", "d1"), move);

    }

}
