package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.board.bitboard.BitBoardConstants;

public class BoardUtils {

    public static int getFile(int square) {
        long bb = 1L << square;
        if ((bb & BitBoardConstants.FILE_A) != 0) return 0;
        if ((bb & BitBoardConstants.FILE_B) != 0) return 1;
        if ((bb & BitBoardConstants.FILE_C) != 0) return 2;
        if ((bb & BitBoardConstants.FILE_D) != 0) return 3;
        if ((bb & BitBoardConstants.FILE_E) != 0) return 4;
        if ((bb & BitBoardConstants.FILE_F) != 0) return 5;
        if ((bb & BitBoardConstants.FILE_G) != 0) return 6;
        if ((bb & BitBoardConstants.FILE_H) != 0) return 7;
        throw new IllegalArgumentException("Illegal square coordinate " + square);
    }

    public static int getRank(int square) {
        long bb = 1L << square;
        if ((bb & BitBoardConstants.RANK_1) != 0) return 0;
        if ((bb & BitBoardConstants.RANK_2) != 0) return 1;
        if ((bb & BitBoardConstants.RANK_3) != 0) return 2;
        if ((bb & BitBoardConstants.RANK_4) != 0) return 3;
        if ((bb & BitBoardConstants.RANK_5) != 0) return 4;
        if ((bb & BitBoardConstants.RANK_6) != 0) return 5;
        if ((bb & BitBoardConstants.RANK_7) != 0) return 6;
        if ((bb & BitBoardConstants.RANK_8) != 0) return 7;
        throw new IllegalArgumentException("Illegal square coordinate " + square);
    }

    public static int squareIndex(int rank, int file) {
        return 8 * rank + file;
    }

}
