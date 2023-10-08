package com.kelseyde.calvin.evaluation.placement;

import com.kelseyde.calvin.utils.BoardUtils;

public class PieceSquareTable {

    public static final int[] WHITE_PAWN_START_TABLE = new int[] {
            0,   0,   0,   0,   0,   0,   0,   0,
            5,  10,  10, -20, -20,  10,  10,   5,
            5,  -5, -10,   0,   0, -10,  -5,   5,
            0,   0,   0,  20,  20,   0,   0,   0,
            5,   5,  10,  25,  25,  10,   5,   5,
            10,  10,  20,  30,  30,  20,  10,  10,
            50,  50,  50,  50,  50,  50,  50,  50,
            0,   0,   0,   0,   0,   0,   0,   0
    };

    public static final int[] WHITE_PAWN_END_TABLE = new int[] {
            0,   0,   0,   0,   0,   0,   0,   0,
            10,  10,  10,  10,  10,  10,  10,  10,
            10,  10,  10,  10,  10,  10,  10,  10,
            20,  20,  20,  20,  20,  20,  20,  20,
            30,  30,  30,  30,  30,  30,  30,  30,
            50,  50,  50,  50,  50,  50,  50,  50,
            80,  80,  80,  80,  80,  80,  80,  80,
            0,   0,   0,   0,   0,   0,   0,   0,
    };

    public static final int[] WHITE_KNIGHT_TABLE = new int[]{
            -20, -10,  -10,  -10,  -10,  -10,  -10,  -20,
            -10,  -5,   -5,   -5,   -5,   -5,   -5,  -10,
            -10,  -5,   15,   15,   15,   15,   -5,  -10,
            -10,  -5,   15,   15,   15,   15,   -5,  -10,
            -10,  -5,   15,   15,   15,   15,   -5,  -10,
            -10,  -5,   10,   15,   15,   15,   -5,  -10,
            -10,  -5,   -5,   -5,   -5,   -5,   -5,  -10,
            -20,   0,  -10,  -10,  -10,  -10,    0,  -20
    };

    public static final int[] WHITE_BISHOP_TABLE = new int[]{
            -20, -10, -10, -10, -10, -10, -10, -20,
            -10,   5,   0,   0,   0,   0,   5, -10,
            -10,  10,  10,  10,  10,  10,  10, -10,
            -10,   0,  10,  10,  10,  10,   0, -10,
            -10,   5,   5,  10,  10,   5,   5, -10,
            -10,   0,   5,  10,  10,   5,   0, -10,
            -10,   0,   0,   0,   0,   0,   0, -10,
            -20, -10, -10, -10, -10, -10, -10, -20,
    };

    public static final int[] WHITE_ROOK_TABLE = new int[]{
            0,  0,  0,  5,  5,  0,  0,  0,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            5, 10, 10, 10, 10, 10, 10,  5,
            0,  0,  0,  0,  0,  0,  0,  0
    };

    public static final int[] WHITE_QUEEN_TABLE = new int[]{
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -10,  5,  5,  5,  5,  5,  0,-10,
              0,  0,  5,  5,  5,  5,  0, -5,
             -5,  0,  5,  5,  5,  5,  0, -5,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20,
    };

    public static final int[] WHITE_KING_START_TABLE = new int[] {
            20,  30,  10,   0,   0,   10,  30,  20,
            20,  20,   0,   0,   0,    0,  20,  20,
            -10, -20, -20, -20, -20, -20, -20, -10,
            20, -30, -30, -40, -40, -30, -30, -20,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30
    };

    public static final int[] WHITE_KING_END_TABLE = new int[] {
            -50, -30, -30, -30, -30, -30, -30, -50,
            -30, -25,   0,   0,   0,   0, -25, -30,
            -25, -20,  20,  25,  25,  20, -20, -25,
            -20, -15,  30,  40,  40,  30, -15, -20,
            -15, -10,  35,  45,  45,  35, -10, -15,
            -10,  -5,  20,  30,  30,  20,  -5, -10,
            -5,    0,   5,   5,   5,   5,   0,  -5,
            -20, -10, -10, -10, -10, -10, -10, -20,
    };

    public static final int[] BLACK_PAWN_START_TABLE = mirrorTable(WHITE_PAWN_START_TABLE);
    public static final int[] BLACK_PAWN_END_TABLE = mirrorTable(WHITE_PAWN_END_TABLE);
    public static final int[] BLACK_KNIGHT_TABLE = mirrorTable(WHITE_KNIGHT_TABLE);
    public static final int[] BLACK_BISHOP_TABLE = mirrorTable(WHITE_BISHOP_TABLE);
    public static final int[] BLACK_ROOK_TABLE = mirrorTable(WHITE_ROOK_TABLE);
    public static final int[] BLACK_QUEEN_TABLE = mirrorTable(WHITE_QUEEN_TABLE);
    public static final int[] BLACK_KING_START_TABLE = mirrorTable(WHITE_KING_START_TABLE);
    public static final int[] BLACK_KING_END_TABLE = mirrorTable(WHITE_KING_END_TABLE);

    private static int[] mirrorTable(int[] table) {
        int[] mirrorTable = new int[table.length];
        for (int square = 0; square < table.length; square++) {
            int file = BoardUtils.getFile(square);
            int rank = BoardUtils.getRank(square);
            int flippedRank = 7 - rank;
            int mirrorIndex = BoardUtils.squareIndex(flippedRank, file);
            mirrorTable[mirrorIndex] = table[square];
        }
        return mirrorTable;
    }

}
