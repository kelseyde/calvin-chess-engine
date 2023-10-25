package com.kelseyde.calvin.board.bitboard;

import com.kelseyde.calvin.utils.BoardUtils;

public class BitboardUtils {

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
                shiftNorth(pawnSingleMoves(pawns, occupied, isWhite)) &~ occupied & Bits.RANK_4 :
                shiftSouth(pawnSingleMoves(pawns, occupied, isWhite)) &~ occupied & Bits.RANK_5;
    }

    public static long pawnPushPromotions(long pawns, long occupied, boolean isWhite) {
        return isWhite ?
                BitboardUtils.shiftNorth(pawns) &~ occupied & Bits.RANK_8 :
                BitboardUtils.shiftSouth(pawns) &~ occupied & Bits.RANK_1;
    }

    public static long pawnLeftCaptures(long pawns, long opponents, boolean isWhite) {
        return isWhite ?
                BitboardUtils.shiftNorthWest(pawns) & opponents &~ Bits.FILE_H &~ Bits.RANK_8 :
                BitboardUtils.shiftSouthWest(pawns) & opponents &~ Bits.FILE_H &~ Bits.RANK_1;
    }

    public static long pawnRightCaptures(long pawns, long opponents, boolean isWhite) {
        return isWhite ?
                BitboardUtils.shiftNorthEast(pawns) & opponents &~ Bits.FILE_A &~ Bits.RANK_8:
                BitboardUtils.shiftSouthEast(pawns) & opponents &~ Bits.FILE_A &~ Bits.RANK_1;
    }

    public static long pawnLeftEnPassants(long pawns, long enPassantFile, boolean isWhite) {
        return isWhite ?
                BitboardUtils.shiftNorthWest(pawns) & enPassantFile & Bits.RANK_6 &~ Bits.FILE_H :
                BitboardUtils.shiftSouthWest(pawns) & enPassantFile & Bits.RANK_3 &~ Bits.FILE_H;
    }

    public static long pawnRightEnPassants(long pawns, long enPassantFile, boolean isWhite) {
        return isWhite ?
                BitboardUtils.shiftNorthEast(pawns) & enPassantFile &~ Bits.FILE_A & Bits.RANK_6 :
                BitboardUtils.shiftSouthEast(pawns) & enPassantFile &~ Bits.FILE_A & Bits.RANK_3;
    }

    public static long pawnLeftCapturePromotions(long pawns, long opponents, boolean isWhite) {
        return isWhite ?
                BitboardUtils.shiftNorthWest(pawns) & opponents &~ Bits.FILE_H & Bits.RANK_8 :
                BitboardUtils.shiftSouthWest(pawns) & opponents &~ Bits.FILE_H & Bits.RANK_1;
    }

    public static long pawnRightCapturePromotions(long pawns, long opponents, boolean isWhite) {
        return isWhite ?
                BitboardUtils.shiftNorthEast(pawns) & opponents &~ Bits.FILE_A & Bits.RANK_8 :
                BitboardUtils.shiftSouthEast(pawns) & opponents &~ Bits.FILE_A & Bits.RANK_1;
    }

    /**
     * Get the index of the least-significant bit in the bitboard
     */
    public static int getLSB(long board) {
        return Long.numberOfTrailingZeros(board);
    }

    /**
     * Get a bitboard with the least-significant bit removed from the given bitboard.
     */
    public static long popLSB(long board) {
        return board & (board - 1);
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
