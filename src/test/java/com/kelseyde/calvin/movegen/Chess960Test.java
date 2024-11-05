package com.kelseyde.calvin.movegen;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.search.SEE;
import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.search.TimeControl;
import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Chess960Test {

    private static final List<String> FENS = List.of(
            "nrkqrbbn/pppppppp/8/8/8/8/PPPPPPPP/NRKQRBBN w EBeb - 0 1"
//            "nrbnkbqr/pppppppp/8/8/8/8/PPPPPPPP/NRBNKBQR w KQkq - 0 1"
    );

    private static final Searcher SEARCHER = TestUtils.SEARCHER;
    private static final MoveGenerator MOVEGEN = new MoveGenerator();

    @Test
    public void testFens() {

        UCI.Options.chess960 = true;

        for (String fen : FENS) {

            SEARCHER.clearHistory();
            SEARCHER.setPosition(Board.from(fen));
            SEARCHER.search(20, 0, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1);

        }

        UCI.Options.chess960 = false;


    }

    @Test
    public void badCastling() {

        UCI.Options.chess960 = true;

        String fen = "nrkqrbbn/pppppppp/8/8/8/8/PPPPPPPP/NRKQRBBN w EBeb - 0 1";
        Board board = Board.from(fen);
        List<Move> moves = MOVEGEN.generateMoves(board);
        Assertions.assertFalse(moves.stream().anyMatch(m -> Move.toUCI(m).equals("c1a1")));

        UCI.Options.chess960 = false;

    }

}
