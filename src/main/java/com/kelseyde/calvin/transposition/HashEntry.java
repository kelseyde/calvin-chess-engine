package com.kelseyde.calvin.transposition;

import com.kelseyde.calvin.board.Move;

/**
 * Individual entry in the transposition table containing the 64-bit zobrist key, and a 64-bit encoding of the score,
 * move, flag and depth:
 * - score: 32 bits     (-1000000-1000000, capturing negative -> positive checkmate score)
 * - move: 16 bits      (0-5 = start square, 6-11 = end square, 12-15 = special move flag, see {@link Move})
 * - flag: 4 bits       (0-2, capturing three possible flag values + 1 bit padding)
 * - depth: 12 bits     (0-265, max depth = 256 = 8 bits + 4 bit padding)
 */
public record HashEntry(long key, long value) {

    // Constants for bit masks and shift operations
    private static final int SCORE_SHIFT = 32;
    private static final int MOVE_SHIFT = 16;
    private static final int FLAG_SHIFT = 12;
    private static final long CLEAR_SCORE_MASK = 0x00000000FFFFFFFFL;
    private static final int DEPTH_MASK = 0xfff;
    private static final int FLAG_MASK = 0xf;

    // Constants for default values
    private static final int DEFAULT_MOVE_VALUE = 0;

    public int getScore() {
        return (int) (value >>> SCORE_SHIFT);
    }

    public Move getMove() {
        long moveValue = (value >> MOVE_SHIFT) & 0xffff;
        return moveValue > 0 ? new Move((short) moveValue) : null;
    }

    public HashFlag getFlag() {
        int flagValue = (int) ((value >>> FLAG_SHIFT) & FLAG_MASK);
        return HashFlag.valueOf(flagValue);
    }

    public int getDepth() {
        return (int) (value & DEPTH_MASK);
    }

    public static HashEntry of(long zobristKey, int score, Move move, HashFlag flag, int depth) {
        long moveValue = move != null ? move.getValue() : DEFAULT_MOVE_VALUE;
        long flagValue = HashFlag.value(flag);
        long value = ((long) score << SCORE_SHIFT) | (moveValue << MOVE_SHIFT) | (flagValue << FLAG_SHIFT) | depth;
        return new HashEntry(zobristKey, value);
    }

    public static HashEntry withScore(HashEntry entry, int score) {
        long value = (entry.value() & ~CLEAR_SCORE_MASK) | ((long) score << SCORE_SHIFT);
        return new HashEntry(entry.key(), value);
    }

}
