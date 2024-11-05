package com.kelseyde.calvin.movegen;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Chess960Test {

    private static final List<String> FENS = List.of(
//            "nrkqrbbn/pppppppp/8/8/8/8/PPPPPPPP/NRKQRBBN w EBeb - 0 1"
            "rkrnqnbb/pppppppp/8/8/8/8/PPPPPPPP/RKRNQNBB w CAca - 0 1"
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

        String fen = "rb2r1k1/pp3q1n/2p1b1p1/P2p3p/3BPp1P/2P2P2/1P3NPQ/RB3K1R w HA - 2 20";
        Board board = Board.from(fen);
        List<Move> moves = MOVEGEN.generateMoves(board);
        System.out.println(moves.stream().map(Move::toUCI).toList());
        Assertions.assertFalse(moves.stream().anyMatch(m -> Move.toUCI(m).equals("f1a1")));

        UCI.Options.chess960 = false;

    }

}
