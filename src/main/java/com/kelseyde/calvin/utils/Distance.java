package com.kelseyde.calvin.utils;

public class Distance {

    private static final int[][] DISTANCE_TABLE = new int[64][64];

    public static int between(int sq1, int sq2) {
        return DISTANCE_TABLE[Math.min(sq1, sq2)][Math.max(sq1, sq2)];
    }

    static {
        for (int sq1 = 0; sq1 < 64; sq1++) {
            for (int sq2 = 0; sq2 < 64; sq2++) {
                if (DISTANCE_TABLE[sq2] == null) {
                    int file1 = sq1 & 7;
                    int file2 = sq2 & 7;
                    int rank1 = sq1 >> 3;
                    int rank2 = sq2 >> 3;
                    int rankDistance = Math.abs(rank2 - rank1);
                    int fileDistance = Math.abs(file2 - file1);
                    int distance = Math.max(rankDistance, fileDistance);
                    DISTANCE_TABLE[Math.min(sq1, sq2)][Math.max(sq1, sq2)] = distance;
                }
            }
        }
    }


}
