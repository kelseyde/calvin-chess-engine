package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.board.bitboard.Bits;

import java.util.Set;

public class BoardUtils {

    // All the possible move 'vectors' for a sliding piece, i.e., the offsets for the directions in which a sliding
    // piece is permitted to move. Bishops will use only the diagonal vectors, rooks only the orthogonal vectors, while
    // queens will use both.
    public static final Set<Integer> DIAGONAL_MOVE_VECTORS = Set.of(-9, -7, 7, 9);
    public static final Set<Integer> ORTHOGONAL_MOVE_VECTORS = Set.of(-8, -1, 1, 8);

    // The following sets are exceptions to the initial rule, in scenarios where the sliding piece is placed on the a or h-files.
    // These exceptions prevent the piece from 'wrapping' around to the other side of the board.
    public static final Set<Integer> A_FILE_OFFSET_EXCEPTIONS = Set.of(-9, -1, 7);
    public static final Set<Integer> H_FILE_OFFSET_EXCEPTIONS = Set.of(-7, 1, 9);

    public static int getFile(int square) {
        long bb = 1L << square;
        if ((bb & Bits.FILE_A) != 0) return 0;
        if ((bb & Bits.FILE_B) != 0) return 1;
        if ((bb & Bits.FILE_C) != 0) return 2;
        if ((bb & Bits.FILE_D) != 0) return 3;
        if ((bb & Bits.FILE_E) != 0) return 4;
        if ((bb & Bits.FILE_F) != 0) return 5;
        if ((bb & Bits.FILE_G) != 0) return 6;
        if ((bb & Bits.FILE_H) != 0) return 7;
        throw new IllegalArgumentException("Illegal square coordinate " + square);
    }

    public static int getRank(int square) {
        long bb = 1L << square;
        if ((bb & Bits.RANK_1) != 0) return 0;
        if ((bb & Bits.RANK_2) != 0) return 1;
        if ((bb & Bits.RANK_3) != 0) return 2;
        if ((bb & Bits.RANK_4) != 0) return 3;
        if ((bb & Bits.RANK_5) != 0) return 4;
        if ((bb & Bits.RANK_6) != 0) return 5;
        if ((bb & Bits.RANK_7) != 0) return 6;
        if ((bb & Bits.RANK_8) != 0) return 7;
        throw new IllegalArgumentException("Illegal square coordinate " + square);
    }

    public static int squareIndex(int rank, int file) {
        return 8 * rank + file;
    }

    public static int distance(int sq1, int sq2) {
        int file1 = sq1 & 7;
        int file2 = sq2 & 7;
        int rank1 = sq1 >> 3;
        int rank2 = sq2 >> 3;
        int rankDistance = Math.abs(rank2 - rank1);
        int fileDistance = Math.abs(file2 - file1);
        return Math.max(rankDistance, fileDistance);
    }

    public static boolean isValidIndex(int squareIndex) {
        return squareIndex >= 0 && squareIndex < 64;
    }

}
