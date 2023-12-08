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
    
    public static long shiftNorth(long board) {
        return board << 8;
    }

    public static long shiftSouth(long board) {
        return board >>> 8;
    }

    public static long shiftEast(long board) {
        return board << 1 &~ Bits.FILE_A;
    }

    public static long shiftWest(long board) {
        return (board >>> 1) &~ Bits.FILE_H;
    }

    public static long shiftNorthEast(long board) {
        return board << 9 &~ Bits.FILE_A;
    }

    public static long shiftSouthEast(long board) {
        return board >>> 7 &~ Bits.FILE_A;
    }

    public static long shiftNorthWest(long board) {
        return board << 7 &~ Bits.FILE_H;
    }

    public static long shiftSouthWest(long board) {
        return board >>> 9 &~ Bits.FILE_H;
    }

    public static long pawnSingleMoves(long pawns, long occupied, boolean isWhite) {
        return isWhite ?
                shiftNorth(pawns) &~ occupied &~ Bits.RANK_8 :
                shiftSouth(pawns) &~ occupied &~ Bits.RANK_1;
    }

    public static long pawnDoubleMoves(long pawns, long occupied, boolean isWhite) {
        return isWhite ?
                shiftNorth(pawnSingleMoves(pawns, occupied, true)) &~ occupied & Bits.RANK_4 :
                shiftSouth(pawnSingleMoves(pawns, occupied, false)) &~ occupied & Bits.RANK_5;
    }

    public static long pawnPushPromotions(long pawns, long occupied, boolean isWhite) {
        return isWhite ?
                Bitwise.shiftNorth(pawns) &~ occupied & Bits.RANK_8 :
                Bitwise.shiftSouth(pawns) &~ occupied & Bits.RANK_1;
    }

    public static long pawnLeftCaptures(long pawns, long opponents, boolean isWhite) {
        return isWhite ?
                Bitwise.shiftNorthWest(pawns) & opponents &~ Bits.FILE_H &~ Bits.RANK_8 :
                Bitwise.shiftSouthWest(pawns) & opponents &~ Bits.FILE_H &~ Bits.RANK_1;
    }

    public static long pawnRightCaptures(long pawns, long opponents, boolean isWhite) {
        return isWhite ?
                Bitwise.shiftNorthEast(pawns) & opponents &~ Bits.FILE_A &~ Bits.RANK_8:
                Bitwise.shiftSouthEast(pawns) & opponents &~ Bits.FILE_A &~ Bits.RANK_1;
    }

    public static long pawnLeftEnPassants(long pawns, long enPassantFile, boolean isWhite) {
        return isWhite ?
                Bitwise.shiftNorthWest(pawns) & enPassantFile & Bits.RANK_6 &~ Bits.FILE_H :
                Bitwise.shiftSouthWest(pawns) & enPassantFile & Bits.RANK_3 &~ Bits.FILE_H;
    }

    public static long pawnRightEnPassants(long pawns, long enPassantFile, boolean isWhite) {
        return isWhite ?
                Bitwise.shiftNorthEast(pawns) & enPassantFile &~ Bits.FILE_A & Bits.RANK_6 :
                Bitwise.shiftSouthEast(pawns) & enPassantFile &~ Bits.FILE_A & Bits.RANK_3;
    }

    public static long pawnLeftCapturePromotions(long pawns, long opponents, boolean isWhite) {
        return isWhite ?
                Bitwise.shiftNorthWest(pawns) & opponents &~ Bits.FILE_H & Bits.RANK_8 :
                Bitwise.shiftSouthWest(pawns) & opponents &~ Bits.FILE_H & Bits.RANK_1;
    }

    public static long pawnRightCapturePromotions(long pawns, long opponents, boolean isWhite) {
        return isWhite ?
                Bitwise.shiftNorthEast(pawns) & opponents &~ Bits.FILE_A & Bits.RANK_8 :
                Bitwise.shiftSouthEast(pawns) & opponents &~ Bits.FILE_A & Bits.RANK_1;
    }

    public static boolean isPassedPawn(int pawn, long opponentPawns, boolean isWhite) {
        long passedPawnMask = isWhite ? Bits.WHITE_PASSED_PAWN_MASK[pawn] : Bits.BLACK_PASSED_PAWN_MASK[pawn];
        return (passedPawnMask & opponentPawns) == 0;
    }

    public static boolean isIsolatedPawn(int file, long friendlyPawns) {
        return (Bits.ADJACENT_FILE_MASK[file] & friendlyPawns) == 0;
    }

    public static boolean isDoubledPawn(int file, long friendlyPawns) {
        long fileMask = Bits.FILE_MASKS[file];
        return countBits(friendlyPawns & fileMask) > 1;
    }

    public static long getPawnShield(int kingFile, long pawns) {
        long tripleFileMask = Bits.TRIPLE_FILE_MASK[kingFile];
        return tripleFileMask & pawns;
    }

    public static int countPawnProtectors(int pawn, long friendlyPawns, boolean isWhite) {
        long protectionMask = isWhite ? Bits.WHITE_PROTECTED_PAWN_MASK[pawn] : Bits.BLACK_PROTECTED_PAWN_MASK[pawn];
        return countBits(protectionMask & friendlyPawns);
    }

    public static void print(long board) {
        String s = Long.toBinaryString(board);
        for (int i = 7; i >= 0; i--) {
            for (int n = 0; n < 8; n++) {
                int index = BoardUtils.squareIndex(i , n);
                boolean isBit = ((board >>> index) & 1) == 1;
                System.out.print(isBit ? 1 : 0);
            }
            System.out.println();
        }
        System.out.println();
    }

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
