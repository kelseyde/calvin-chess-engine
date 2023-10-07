package com.kelseyde.calvin.utils;

import java.util.Arrays;

/**
 * Precalculated lookup tables for distances between squares on the board.
 *
 * @see <a href="https://www.chessprogramming.org/Distance">Chess Programming Wiki</a>
 */
public class Distance {

    /**
     * The 'Chebyshev' distance is the number of king moves between two squares.
     */
    private static final int[][] CHEBYSHEV_DISTANCE = new int[64][64];

    /**
     * The 'Manhattan' distance is the number of orthogonal king moves between two squares.
     */
    private static final int[][] MANHATTAN_DISTANCE = new int[64][64];

    /**
     * The 'Center Manhattan' distance is the Manhattan distance between a square and the central four squares.
     */
    private static final int[] CENTER_MANHATTAN_DISTANCE = new int[64];

    public static int chebyshev(int sq1, int sq2) {
        return CHEBYSHEV_DISTANCE[sq1][sq2];
    }

    public static int manhattan(int sq1, int sq2) {
        return MANHATTAN_DISTANCE[sq1][sq2];
    }

    public static int centerManhattan(int sq) {
        return CENTER_MANHATTAN_DISTANCE[sq];
    }

    static {
        for (int sq1 = 0; sq1 < 64; sq1++) {
            for (int sq2 = 0; sq2 < 64; sq2++) {
                int file1 = sq1 & 7;
                int file2 = sq2 & 7;
                int rank1 = sq1 >> 3;
                int rank2 = sq2 >> 3;
                int rankDistance = Math.abs(rank2 - rank1);
                int fileDistance = Math.abs(file2 - file1);

                int chebyshev = Math.max(rankDistance, fileDistance);
                CHEBYSHEV_DISTANCE[sq1][sq2] = chebyshev;

                int manhattan = rankDistance + fileDistance;
                MANHATTAN_DISTANCE[sq1][sq2] = manhattan;
            }
            int[] centerManhattans = new int[]{
                    manhattan(sq1, 27), manhattan(sq1, 28), manhattan(sq1, 35), manhattan(sq1, 36)
            };
            Arrays.sort(centerManhattans);
            CENTER_MANHATTAN_DISTANCE[sq1] = centerManhattans[0];
        }
    }


}
