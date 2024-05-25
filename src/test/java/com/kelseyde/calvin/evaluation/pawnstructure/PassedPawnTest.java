package com.kelseyde.calvin.evaluation.pawnstructure;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PassedPawnTest {

    @Test
    public void testFreePawn() {

        String fen = "2k5/n4pn1/8/8/8/5P2/P1P4P/5K2 w - - 0 1";
        Board board = FEN.toBoard(fen);

        long occupied = board.getOccupied();
        long whitePawns = board.getWhitePawns();
        long blackPawns = board.getBlackPawns();

        Assertions.assertTrue(Bitwise.isPassedPawn(8, blackPawns, true));
        Assertions.assertFalse(Bitwise.isFreePawn(8, occupied, true));

        Assertions.assertTrue(Bitwise.isPassedPawn(10, blackPawns, true));
        Assertions.assertFalse(Bitwise.isFreePawn(10, occupied, true));

        Assertions.assertFalse(Bitwise.isPassedPawn(21, blackPawns, true));
        Assertions.assertFalse(Bitwise.isFreePawn(21, occupied, true));

        Assertions.assertTrue(Bitwise.isPassedPawn(15, blackPawns, true));
        Assertions.assertTrue(Bitwise.isFreePawn(15, occupied, true));


    }

}
