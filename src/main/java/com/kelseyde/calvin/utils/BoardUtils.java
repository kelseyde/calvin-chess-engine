package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.board.bitboard.BitBoardConstants;

public class BoardUtils {

    public static int getFile(int square) {
        long squareMask = 1L << square;
        if ((squareMask & BitBoardConstants.FILE_A) != 0) {
            return 0;
        }
        if ((squareMask & BitBoardConstants.FILE_B) != 0) {
            return 1;
        }
        if ((squareMask & BitBoardConstants.FILE_C) != 0) {
            return 2;
        }
        if ((squareMask & BitBoardConstants.FILE_D) != 0) {
            return 3;
        }
        if ((squareMask & BitBoardConstants.FILE_E) != 0) {
            return 4;
        }
        if ((squareMask & BitBoardConstants.FILE_F) != 0) {
            return 5;
        }
        if ((squareMask & BitBoardConstants.FILE_G) != 0) {
            return 6;
        }
        if ((squareMask & BitBoardConstants.FILE_H) != 0) {
            return 7;
        }
        throw new IllegalArgumentException("Illegal square coordinate " + square);
    }

}
