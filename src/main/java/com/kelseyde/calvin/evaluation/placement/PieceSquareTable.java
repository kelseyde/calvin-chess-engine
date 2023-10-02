package com.kelseyde.calvin.evaluation.placement;

import com.kelseyde.calvin.board.piece.PieceType;
import com.kelseyde.calvin.utils.BoardUtils;

public class PieceSquareTable {

    private static final int[] WHITE_PAWN_TABLE = new int[] {
            0,   0,   0,   0,   0,   0,   0,   0,
            5,  10,  10, -20, -20,  10,  10,   5,
            5,  -5, -10,   0,   0, -10,  -5,   5,
            0,   0,   0,  20,  20,   0,   0,   0,
            5,   5,  10,  25,  25,  10,   5,   5,
            10,  10,  20,  30,  30,  20,  10,  10,
            50,  50,  50,  50,  50,  50,  50,  50,
            0,   0,   0,   0,   0,   0,   0,   0
    };

    private static final int[] WHITE_KNIGHT_TABLE = new int[]{
            -20, -10,  -10,  -10,  -10,  -10,  -10,  -20,
            -10,  -5,   -5,   -5,   -5,   -5,   -5,  -10,
            -10,  -5,   15,   15,   15,   15,   -5,  -10,
            -10,  -5,   15,   15,   15,   15,   -5,  -10,
            -10,  -5,   15,   15,   15,   15,   -5,  -10,
            -10,  -5,   10,   15,   15,   15,   -5,  -10,
            -10,  -5,   -5,   -5,   -5,   -5,   -5,  -10,
            -20,   0,  -10,  -10,  -10,  -10,    0,  -20
    };

    private static final int[] WHITE_BISHOP_TABLE = new int[]{
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10,-10,-10,-10,-10,-20,
    };

    private static final int[] WHITE_ROOK_TABLE = new int[]{
            0,  0,  0,  5,  5,  0,  0,  0,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            5, 10, 10, 10, 10, 10, 10,  5,
            0,  0,  0,  0,  0,  0,  0,  0
    };

    private static final int[] WHITE_QUEEN_TABLE = new int[]{
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -10,  5,  5,  5,  5,  5,  0,-10,
              0,  0,  5,  5,  5,  5,  0, -5,
             -5,  0,  5,  5,  5,  5,  0, -5,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20,
    };

    private static final int[] WHITE_KING_TABLE = new int[] {
            20,  30,  10,   0,   0,   10,  30,  20,
            20,  20,   0,   0,   0,    0,  20,  20,
            -10, -20, -20, -20, -20, -20, -20, -10,
            20, -30, -30, -40, -40, -30, -30, -20,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30
    };

    private static final int[] BLACK_PAWN_TABLE = getMirrorTable(WHITE_PAWN_TABLE);
    private static final int[] BLACK_KNIGHT_TABLE = getMirrorTable(WHITE_KNIGHT_TABLE);
    private static final int[] BLACK_BISHOP_TABLE = getMirrorTable(WHITE_BISHOP_TABLE);
    private static final int[] BLACK_ROOK_TABLE = getMirrorTable(WHITE_ROOK_TABLE);
    private static final int[] BLACK_QUEEN_TABLE = getMirrorTable(WHITE_QUEEN_TABLE);
    private static final int[] BLACK_KING_TABLE = getMirrorTable(WHITE_KING_TABLE);

    public static int scorePiece(PieceType type, int square, boolean isWhite) {
        return getPieceSquareTable(type, isWhite)[square];
    }

    public static int[] getPieceSquareTable(PieceType type, boolean isWhite) {
        return switch (type) {
            case PAWN   -> isWhite ? WHITE_PAWN_TABLE   : BLACK_PAWN_TABLE;
            case KNIGHT -> isWhite ? WHITE_KNIGHT_TABLE : BLACK_KNIGHT_TABLE;
            case BISHOP -> isWhite ? WHITE_BISHOP_TABLE : BLACK_BISHOP_TABLE;
            case ROOK   -> isWhite ? WHITE_ROOK_TABLE   : BLACK_ROOK_TABLE;
            case QUEEN  -> isWhite ? WHITE_QUEEN_TABLE  : BLACK_QUEEN_TABLE;
            case KING   -> isWhite ? WHITE_KING_TABLE   : BLACK_KING_TABLE;
        };
    }

    private static int[] getMirrorTable(int[] table) {
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
