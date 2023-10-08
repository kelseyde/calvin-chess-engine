package com.kelseyde.calvin.puzzles;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.bot.Bot;
import com.kelseyde.calvin.bot.CalvinBot;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.IterativeDeepeningSearch;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;

public class EndgameTest {

    private final Bot bot = new CalvinBot();

    private final ResultCalculator resultCalculator = new ResultCalculator();

    @Test
    public void testQueenMateInOneBetterThanMateInTwo() {

        String fen = "k7/8/2K5/8/8/8/1Q6/8 w - - 1 1";
        Board board = FEN.fromFEN(fen);

        IterativeDeepeningSearch search = new IterativeDeepeningSearch(board);

        SearchResult result = search.search(Duration.ofMillis(300));

        Move bestMove = NotationUtils.fromNotation("b2", "b7", PieceType.QUEEN);
        assertMove(bestMove, result.move());

    }

    @Test
    @Disabled
    public void testQueenCheckmate() {

        String fen = "8/8/8/3k4/8/8/1Q6/4K3 w - - 0 1";
        assertCheckmate(fen, 50);

    }

    @Disabled
    @Test
    public void testRookCheckmate() {

        String fen = "8/8/8/3k4/8/8/R7/4K3 w - - 0 1";
        assertCheckmate(fen, 50);

    }

    private void assertCheckmate(String fen, int moveLimit) {

        bot.setPosition(fen, Collections.emptyList());
        Bot opponentBot = new CalvinBot();
        opponentBot.setPosition(fen, Collections.emptyList());

        boolean checkmate = false;

        while (moveLimit > 0) {
            Move move = bot.think(200);
            bot.applyMove(move);
            opponentBot.applyMove(move);
            if (resultCalculator.calculateResult(bot.getBoard()).isCheckmate()) {
                checkmate = true;
                break;
            }
            Move opponentMove = opponentBot.think(200);
            bot.applyMove(opponentMove);
            opponentBot.applyMove(opponentMove);
            moveLimit--;
        }
        if (!checkmate) {
            Assertions.fail(String.format("Could not checkmate in %s moves", moveLimit));
        } else {
            System.out.println(50 - moveLimit);
        }

    }

    private void assertMove(Move expected, Move actual) {
        boolean matches = expected.matches(actual);
        if (!matches) {
            System.out.printf("Expected move %s, Actual move %s%n",
                    NotationUtils.toNotation(expected), NotationUtils.toNotation(actual));
        }
        Assertions.assertTrue(matches);
    }

}
