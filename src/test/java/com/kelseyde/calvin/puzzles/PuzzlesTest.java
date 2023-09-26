package com.kelseyde.calvin.puzzles;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.search.minimax.MinimaxSearch;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.fen.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class PuzzlesTest {

    private Search search;

    @Test
    public void testKnightMateInOne() {

        String fen = "5rk1/pp2b1pp/8/5p1B/2P1p3/2PnR3/PP1r2PP/R1Q3NK b - - 0 1";
        Board board = FEN.fromFEN(fen);

        search = new MinimaxSearch(board);

        SearchResult result = search.search(3);

        Move bestMove = NotationUtils.fromNotation("d3", "f2");
        Assertions.assertTrue(bestMove.matches(result.move()));

    }

    @Test
    public void testFreeKnight() {

        String fen = "rn3rk1/pp4pp/2pR1p2/4p3/8/2P1BNP1/PPPRbP1P/6K1 b - - 1 1";
        Board board = FEN.fromFEN(fen);

        search = new MinimaxSearch(board);

        SearchResult result = search.search(3);

        Move bestMove = NotationUtils.fromNotation("e2", "f3");
        Assertions.assertTrue(bestMove.matches(result.move()));

    }

    @Test
    public void testBackRankMateInTwo() {

        String fen = "6k1/p1p2ppp/1pP5/3r4/2q2PQP/P5PK/8/R7 w - - 0 2";
        Board board = FEN.fromFEN(fen);

        search = new MinimaxSearch(board);

        SearchResult result = search.search(3);

        Move bestMove = NotationUtils.fromNotation("g4", "c8", PieceType.QUEEN);
        Assertions.assertTrue(bestMove.matches(result.move()));

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("d5", "d8", PieceType.ROOK));

        result = search.search(3);
        bestMove = NotationUtils.fromNotation("c8", "d8", PieceType.QUEEN);
        Assertions.assertTrue(bestMove.matches(result.move()));

    }

    @Test
    public void testSimpleRemoveTheDefender() {

        String fen = "r1b1k2r/1p3ppp/5n2/q2pp3/1P1b4/1QB3P1/4PPBP/RN2K1NR b KQkq - 0 1";
        Board board = FEN.fromFEN(fen);

        search = new MinimaxSearch(board);

        SearchResult result = search.search(3);

        Move bestMove = NotationUtils.fromNotation("d4", "c3", PieceType.BISHOP);
        Assertions.assertTrue(bestMove.matches(result.move()));

        board.makeMove(bestMove);
        board.makeMove(NotationUtils.fromNotation("b1", "c3", PieceType.BISHOP));

        result = search.search(3);
        bestMove = NotationUtils.fromNotation("a5", "a1", PieceType.QUEEN);
        Assertions.assertTrue(bestMove.matches(result.move()));

    }


}
