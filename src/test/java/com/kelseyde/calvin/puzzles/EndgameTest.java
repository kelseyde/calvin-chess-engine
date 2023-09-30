package com.kelseyde.calvin.puzzles;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.search.iterative.IterativeDeepeningSearch;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class EndgameTest {

    @Test
    public void testQueenMateInOneBetterThanMateInTwo() {

        String fen = "k7/8/2K5/8/8/8/1Q6/8 w - - 1 1";
        Board board = FEN.fromFEN(fen);

        IterativeDeepeningSearch search = new IterativeDeepeningSearch(board);

        SearchResult result = search.search(Duration.ofMillis(300));

        Move bestMove = NotationUtils.fromNotation("b2", "b7", PieceType.QUEEN);
        assertMove(bestMove, result.move());

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
