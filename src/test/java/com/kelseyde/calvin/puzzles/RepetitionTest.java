package com.kelseyde.calvin.puzzles;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Disabled
public class RepetitionTest {

    private Search search;

    @Test
    public void testCorrectlyIdentifyRepetition() {

        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Board board = FEN.fromFEN(fen);
        board.makeMove(NotationUtils.fromNotation("g1", "f3"));
        board.makeMove(NotationUtils.fromNotation("g8", "f6"));
        board.makeMove(NotationUtils.fromNotation("f3", "g1"));
        board.makeMove(NotationUtils.fromNotation("f6", "g8"));
        search = new Searcher(board);
        Move move = search.search(Duration.ofSeconds(1)).move();
        Assertions.assertNotNull(move);
        System.out.println(NotationUtils.toNotation(move));
        board.makeMove(NotationUtils.fromNotation("g1", "f3"));
        board.makeMove(NotationUtils.fromNotation("g8", "f6"));
        board.makeMove(NotationUtils.fromNotation("f3", "g1"));
        search = new Searcher(board);
        move = search.search(Duration.ofSeconds(1)).move();
        Assertions.assertNotNull(move);
        System.out.println(NotationUtils.toNotation(move));
        board.makeMove(NotationUtils.fromNotation("f6", "g8"));
        search = new Searcher(board);
        SearchResult result = search.search(Duration.ofSeconds(1));
        Assertions.assertNull(result.move());
        Assertions.assertEquals(0, result.eval());


    }

}
