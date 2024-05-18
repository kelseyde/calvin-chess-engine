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
    public static long pawnSingleMoves(long pawns, long occupied, boolean isWhite) {
        return isWhite ?
                shiftNorth(pawns) & ~occupied & ~Bits.RANK_8 :
                shiftSouth(pawns) & ~occupied & ~Bits.RANK_1;
    }

    /**
     * Calculate double pawn moves.
     */
    public static long pawnDoubleMoves(long pawns, long occupied, boolean isWhite) {
        return isWhite ?
                shiftNorth(pawnSingleMoves(pawns, occupied, true)) & ~occupied & Bits.RANK_4 :
                shiftSouth(pawnSingleMoves(pawns, occupied, false)) & ~occupied & Bits.RANK_5;
    }

    /**
     * Calculate pawn push promotions.
     */
    public static long pawnPushPromotions(long pawns, long occupied, boolean isWhite) {
        return isWhite ?
                shiftNorth(pawns) & ~occupied & Bits.RANK_8 :
                shiftSouth(pawns) & ~occupied & Bits.RANK_1;
    }

    /**
     * Calculate left captures by pawns.
     */
    public static long pawnLeftCaptures(long pawns, long opponents, boolean isWhite) {
        return isWhite ?
                shiftNorthWest(pawns) & opponents & ~Bits.FILE_H & ~Bits.RANK_8 :
                shiftSouthWest(pawns) & opponents & ~Bits.FILE_H & ~Bits.RANK_1;
    }

    /**
     * Calculate right captures by pawns.
     */
    public static long pawnRightCaptures(long pawns, long opponents, boolean isWhite) {
        return isWhite ?
                shiftNorthEast(pawns) & opponents & ~Bits.FILE_A & ~Bits.RANK_8 :
                shiftSouthEast(pawns) & opponents & ~Bits.FILE_A & ~Bits.RANK_1;
    }

    /**
     * Calculate left en passant captures by pawns.
     */
    public static long pawnLeftEnPassants(long pawns, long enPassantFile, boolean isWhite) {
        return isWhite ?
                shiftNorthWest(pawns) & enPassantFile & Bits.RANK_6 & ~Bits.FILE_H :
                shiftSouthWest(pawns) & enPassantFile & Bits.RANK_3 & ~Bits.FILE_H;
    }

    /**
     * Calculate right en passant captures by pawns.
     */
    public static long pawnRightEnPassants(long pawns, long enPassantFile, boolean isWhite) {
        return isWhite ?
                shiftNorthEast(pawns) & enPassantFile & Bits.RANK_6 & ~Bits.FILE_A :
                shiftSouthEast(pawns) & enPassantFile & Bits.RANK_3 & ~Bits.FILE_A;
    }

    /**
     * Calculate left capture promotions by pawns.
     */
    public static long pawnLeftCapturePromotions(long pawns, long opponents, boolean isWhite) {
        return isWhite ?
                shiftNorthWest(pawns) & opponents & ~Bits.FILE_H & Bits.RANK_8 :
                shiftSouthWest(pawns) & opponents & ~Bits.FILE_H & Bits.RANK_1;
    }

    /**
     * Calculate right capture promotions by pawns.
     */
    public static long pawnRightCapturePromotions(long pawns, long opponents, boolean isWhite) {
        return isWhite ?
                shiftNorthEast(pawns) & opponents & ~Bits.FILE_A & Bits.RANK_8 :
                shiftSouthEast(pawns) & opponents & ~Bits.FILE_A & Bits.RANK_1;
    }

    /**
     * Determine if a pawn is passed.
     */
    public static boolean isPassedPawn(int pawn, long opponentPawns, boolean isWhite) {
        long passedPawnMask = isWhite ? Bits.WHITE_PASSED_PAWN_MASK[pawn] : Bits.BLACK_PASSED_PAWN_MASK[pawn];
        return (passedPawnMask & opponentPawns) == 0;
    }

    /**
     * Determine if a pawn is isolated.
     */
    public static boolean isIsolatedPawn(int file, long friendlyPawns) {
        return (Bits.ADJACENT_FILE_MASK[file] & friendlyPawns) == 0;
    }

    /**
     * Determine if a pawn is doubled.
     */
    public static boolean isDoubledPawn(int file, long friendlyPawns) {
        long fileMask = Bits.FILE_MASKS[file];
        return countBits(friendlyPawns & fileMask) > 1;
    }

    /**
     * Get the pawn shield bitboard.
     */
    public static long getPawnShield(int kingFile, long pawns) {
        long tripleFileMask = Bits.TRIPLE_FILE_MASK[kingFile];
        return tripleFileMask & pawns;
    }

    /**
     * Count the number of pawn protectors.
     */
    public static int countPawnProtectors(int pawn, long friendlyPawns, boolean isWhite) {
        long protectionMask = isWhite ? Bits.WHITE_PROTECTED_PAWN_MASK[pawn] : Bits.BLACK_PROTECTED_PAWN_MASK[pawn];
        return countBits(protectionMask & friendlyPawns);
    }

    /**
     * Determine if a file is open.
     */
    public static boolean isOpenFile(int file, long friendlyPawns, long opponentPawns) {
        long fileMask = Bits.FILE_MASKS[file];
        return (fileMask & friendlyPawns) == 0 && (fileMask & opponentPawns) == 0;
    }

    /**
     * Determine if a file is semi-open.
     */
    public static boolean isSemiOpenFile(int file, long friendlyPawns, long opponentPawns) {
        long fileMask = Bits.FILE_MASKS[file];
        return (fileMask & friendlyPawns) == 0 && (fileMask & opponentPawns) != 0;
    }

    /**
     * Print the bitboard in a human-readable format.
     */
    public static void print(long board) {
        String s = Long.toBinaryString(board);
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
        return switch (file) {
            case -1 -> 0L;
            case 0 -> Bits.FILE_A;
            case 1 -> Bits.FILE_B;
            case 2 -> Bits.FILE_C;
            case 3 -> Bits.FILE_D;
            case 4 -> Bits.FILE_E;
            case 5 -> Bits.FILE_F;
            case 6 -> Bits.FILE_G;
            case 7 -> Bits.FILE_H;
            default -> throw new IllegalArgumentException("Invalid file " + file);
        };
    }
}
