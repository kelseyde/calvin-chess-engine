package com.kelseyde.calvin.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BitBoard {

    private long board;

    public BitBoard() {
        this.board = 0L;
    }

    public BitBoard xor(BitBoard other) {
        return new BitBoard(board ^ other.board);
    }

    public BitBoard or(BitBoard other) {
        return new BitBoard(board | other.board);
    }

    public BitBoard and(BitBoard other) {
        return new BitBoard(board & other.board);
    }

    public BitBoard not(BitBoard other) {
        return new BitBoard(board & ~other.board);
    }

    public BitBoard copy() {
        return new BitBoard(board);
    }

    public BitBoard inverse() {
        return new BitBoard(~board);
    }

    public void flipBit(int index) {
        board ^= (1L << index);
    }

    public void setBit(int index) {
        board |= (1L << index);
    }

    public void unsetBit(int index) {
        board &= ~(1L << index);
    }

    public boolean getBit(int index) {
        return ((board >>> index) & 1) == 1;
    }

    public boolean isNotZero() {
        return board != 0;
    }

    public BitBoard shiftWest() {
        return new BitBoard((board >>> 1)).not(BitBoards.FILE_H);
    }

    public BitBoard shiftEast() {
        return new BitBoard((board << 1)).not(BitBoards.FILE_A);
    }

    public BitBoard shiftSouth() {
        return new BitBoard(board >>> 8);
    }

    public BitBoard shiftNorth() {
        return new BitBoard(board << 8);
    }

    public BitBoard shiftNorthEast() {
        return new BitBoard((board << 9)).not(BitBoards.FILE_A);
    }

    public BitBoard shiftSouthEast() {
        return new BitBoard((board >>> 7)).not(BitBoards.FILE_A);
    }

    public BitBoard shiftSouthWest() {
        return new BitBoard((board >>> 9)).not(BitBoards.FILE_H);
    }

    public BitBoard shiftNorthWest() {
        return new BitBoard((board << 7)).not(BitBoards.FILE_H);
    }

    public int scanForward(){
        return Long.numberOfTrailingZeros(board);
    }

    public void popLSB() {
        board = board & (board-1);
    }

    public void print() {
        String s = Long.toBinaryString(board);
        for (int i = 7; i >= 0; i--) {
            for (int n = 0; n < 8; n++) {
                System.out.print(getBit(squareIndex(i , n)) ? 1 : 0);
            }
            System.out.println();
        }
        System.out.println();
    }

    public int squareIndex(int rank, int file) {
        return 8 * rank + file;
    }

}
