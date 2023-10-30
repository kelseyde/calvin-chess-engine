package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.tuning.SearchResult;
import com.kelseyde.calvin.utils.notation.NotationUtils;
import com.kelseyde.calvin.utils.notation.FEN;
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
        Searcher search = new Searcher(board);

        SearchResult result = search.search(THINK_TIME);

        System.out.printf("Eval %s, Move %s", result.eval(), NotationUtils.toNotation(result.move()));

    }

    @Test
    public void testStartingPosition() {

        Board board = new Board();
        Searcher search = new Searcher(board);

        SearchResult result = search.search(THINK_TIME);

        System.out.printf("Eval %s, Move %s", result.eval(), NotationUtils.toNotation(result.move()));

    }

    @Test
    public void testKiwiPete() {

        Board board = FEN.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
        Searcher search = new Searcher(board);

        SearchResult result = search.search(THINK_TIME);

        System.out.printf("Eval %s, Move %s", result.eval(), NotationUtils.toNotation(result.move()));

    }

}
