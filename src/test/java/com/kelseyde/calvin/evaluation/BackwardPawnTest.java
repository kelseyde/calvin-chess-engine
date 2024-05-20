package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.notation.Notation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BackwardPawnTest {

    @Test
    public void test() {

        String fen = "1k6/2p4p/3p2p1/5p2/P6P/3P2P1/2P5/5K2 w - - 0 1";
        Board board = FEN.toBoard(fen);

        for (int square = 0; square < 64; square++) {
            int rank = BoardUtils.getRank(square);
            int file = BoardUtils.getFile(square);
            if (square == 22 || square == 10) {
                Assertions.assertTrue(Bitwise.isBackwardPawn(square, rank, file, board.getWhitePawns(), true));
            }
            else {
                System.out.println("pawn " + Notation.toNotation(square));
                Assertions.assertFalse(Bitwise.isBackwardPawn(square, rank, file, board.getWhitePawns(), true));
            }
        }

        for (int square = 0; square < 64; square++) {
            int rank = BoardUtils.getRank(square);
            int file = BoardUtils.getFile(square);
            if (square == 50 || square == 55) {
                Assertions.assertTrue(Bitwise.isBackwardPawn(square, rank, file, board.getBlackPawns(), false));
            }
            else {
                Assertions.assertFalse(Bitwise.isBackwardPawn(square, rank, file, board.getBlackPawns(), false));
            }
        }

    }

}
