package com.kelseyde.calvin.board;

import com.kelseyde.calvin.utils.BoardUtils;

/**
 * Contains utility methods for performing bitwise operations on bitboards.
 */
public class Bitwise {

    /**
     * Get the index of the least-significant bit in the bitboard. Used for iterating through all the set bits.
     */
    public static int getNextBit(long board) {
        return Long.numberOfTrailingZeros(board);
    }

    /**
     * Returns the bitboard with the least-significant bit removed. Used for iterating through all the set bits.
     */
    public static long popBit(long board) {
        return board & (board - 1);
    }

    /**
     * Count the number of set bits in the bitboard.
     */
    public static int countBits(long board) {
        return Long.bitCount(board);
    }

    /**
     * Shift the bitboard north (up one rank).
     */
    public static long shiftNorth(long board) {
        return board << 8;
    }

    /**
     * Shift the bitboard south (down one rank).
     */
    public static long shiftSouth(long board) {
        return board >>> 8;
    }

    /**
     * Shift the bitboard east (right one file).
     */
    public static long shiftEast(long board) {
        return (board << 1) & ~Bits.FILE_A;
    }

    /**
     * Shift the bitboard west (left one file).
     */
    public static long shiftWest(long board) {
        return (board >>> 1) & ~Bits.FILE_H;
    }

    /**
     * Shift the bitboard north-east (up one rank and right one file).
     */
    public static long shiftNorthEast(long board) {
        return (board << 9) & ~Bits.FILE_A;
    }

    /**
     * Shift the bitboard south-east (down one rank and right one file).
     */
    public static long shiftSouthEast(long board) {
        return (board >>> 7) & ~Bits.FILE_A;
    }

    /**
     * Shift the bitboard north-west (up one rank and left one file).
     */
    public static long shiftNorthWest(long board) {
        return (board << 7) & ~Bits.FILE_H;
    }

    /**
     * Shift the bitboard south-west (down one rank and left one file).
     */
    public static long shiftSouthWest(long board) {
        return (board >>> 9) & ~Bits.FILE_H;
    }

    /**
     * Calculate single pawn moves.
     */
    public static long pawnSingleMoves(long pawns, long occupied, boolean white) {
        return white ?
                shiftNorth(pawns) & ~occupied & ~Bits.RANK_8 :
                shiftSouth(pawns) & ~occupied & ~Bits.RANK_1;
    }

    /**
     * Calculate double pawn moves.
     */
    public static long pawnDoubleMoves(long pawns, long occupied, boolean white) {
        return white ?
                shiftNorth(pawnSingleMoves(pawns, occupied, true)) & ~occupied & Bits.RANK_4 :
                shiftSouth(pawnSingleMoves(pawns, occupied, false)) & ~occupied & Bits.RANK_5;
    }

    /**
     * Calculate pawn push promotions.
     */
    public static long pawnPushPromotions(long pawns, long occupied, boolean white) {
        return white ?
                shiftNorth(pawns) & ~occupied & Bits.RANK_8 :
                shiftSouth(pawns) & ~occupied & Bits.RANK_1;
    }

    /**
     * Calculate left captures by pawns.
     */
    public static long pawnLeftCaptures(long pawns, long opponents, boolean white) {
        return white ?
                shiftNorthWest(pawns) & opponents & ~Bits.FILE_H & ~Bits.RANK_8 :
                shiftSouthWest(pawns) & opponents & ~Bits.FILE_H & ~Bits.RANK_1;
    }

    /**
     * Calculate right captures by pawns.
     */
    public static long pawnRightCaptures(long pawns, long opponents, boolean white) {
        return white ?
                shiftNorthEast(pawns) & opponents & ~Bits.FILE_A & ~Bits.RANK_8 :
                shiftSouthEast(pawns) & opponents & ~Bits.FILE_A & ~Bits.RANK_1;
    }

    /**
     * Calculate left en passant captures by pawns.
     */
    public static long pawnLeftEnPassants(long pawns, long enPassantFile, boolean white) {
        return white ?
                shiftNorthWest(pawns) & enPassantFile & Bits.RANK_6 & ~Bits.FILE_H :
                shiftSouthWest(pawns) & enPassantFile & Bits.RANK_3 & ~Bits.FILE_H;
    }

    /**
     * Calculate right en passant captures by pawns.
     */
    public static long pawnRightEnPassants(long pawns, long enPassantFile, boolean white) {
        return white ?
                shiftNorthEast(pawns) & enPassantFile & Bits.RANK_6 & ~Bits.FILE_A :
                shiftSouthEast(pawns) & enPassantFile & Bits.RANK_3 & ~Bits.FILE_A;
    }

    /**
     * Calculate left capture promotions by pawns.
     */
    public static long pawnLeftCapturePromotions(long pawns, long opponents, boolean white) {
        return white ?
                shiftNorthWest(pawns) & opponents & ~Bits.FILE_H & Bits.RANK_8 :
                shiftSouthWest(pawns) & opponents & ~Bits.FILE_H & Bits.RANK_1;
    }

    /**
     * Calculate right capture promotions by pawns.
     */
    public static long pawnRightCapturePromotions(long pawns, long opponents, boolean white) {
        return white ?
                shiftNorthEast(pawns) & opponents & ~Bits.FILE_A & Bits.RANK_8 :
                shiftSouthEast(pawns) & opponents & ~Bits.FILE_A & Bits.RANK_1;
    }

    /**
     * Print the bitboard in a human-readable format.
     */
    public static void print(long board) {
        for (int i = 7; i >= 0; i--) {
            for (int n = 0; n < 8; n++) {
                int index = BoardUtils.squareIndex(i, n);
                boolean isBit = ((board >>> index) & 1) == 1;
                System.out.print(isBit ? 1 : 0);
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Get the bitboard for a specific file.
     */
    public static long getFileBitboard(int file) {
        return 0x0101010101010101L << file;
    }
}
