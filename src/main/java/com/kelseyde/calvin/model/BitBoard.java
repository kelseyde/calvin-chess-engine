package com.kelseyde.calvin.model;

public class BitBoard {

    public static long shiftWest(long board) {
        return (board >>> 1) &~ BitBoards.FILE_H;
    }

    public static long shiftEast(long board) {
        return board << 1 &~ BitBoards.FILE_A;
    }

    public static long shiftSouth(long board) {
        return board >>> 8;
    }

    public static long shiftNorth(long board) {
        return board << 8;
    }

    public static long shiftNorthEast(long board) {
        return board << 9 &~ BitBoards.FILE_A;
    }

    public static long shiftSouthEast(long board) {
        return board >>> 7 &~ BitBoards.FILE_A;
    }

    public static long shiftSouthWest(long board) {
        return board >>> 9 &~ BitBoards.FILE_H;
    }

    public static long shiftNorthWest(long board) {
        return board << 7 &~ BitBoards.FILE_H;
    }

    public static int scanForward(long board) {
        return Long.numberOfTrailingZeros(board);
    }

    public static long popLSB(long board) {
        board = board &= (board - 1);
        return board;
    }

    public static void print(long board) {
        String s = Long.toBinaryString(board);
        for (int i = 7; i >= 0; i--) {
            for (int n = 0; n < 8; n++) {
                int index = squareIndex(i , n);
                boolean isBit = ((board >>> index) & 1) == 1;
                System.out.print(isBit ? 1 : 0);
            }
            System.out.println();
        }
        System.out.println();
    }

    public static int squareIndex(int rank, int file) {
        return 8 * rank + file;
    }

}
