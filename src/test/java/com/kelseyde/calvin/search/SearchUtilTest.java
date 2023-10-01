package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Disabled
public class SearchUtilTest {

    private static final String FEN_STRING = "r1bqkb1r/ppp1pppp/2n5/3pP3/3Pn3/3B1N2/PPP2PPP/RNBQK2R b KQkq - 1 5";
    private static final Duration THINK_TIME = Duration.ofMillis(5000);

    @Test
    public void testBestMove() {

        Board board = FEN.fromFEN(FEN_STRING);
        IterativeDeepeningSearch search = new IterativeDeepeningSearch(board);

        SearchResult result = search.search(THINK_TIME);

        System.out.printf("Eval %s, Move %s", result.eval(), NotationUtils.toNotation(result.move()));

    }

}
