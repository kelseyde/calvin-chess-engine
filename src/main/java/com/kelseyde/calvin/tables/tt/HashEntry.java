package com.kelseyde.calvin.tables.tt;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.search.Score;

/**
 * Entry in the {@link TranspositionTable}. Contains a 64-bit key and a 64-bit value which encodes the relevant
 * information about the position.
 * </p>
 *
 * Key encoding:
 * 0-31: 32 bits representing half of the zobrist hash. Used to verify that the position truly matches.
 * 32-47: 16 bits representing the age. Used to gradually replace old entries.
 * 48-63: 16 bits representing the static eval of the position. Re-used to save calling the evaluation function again.
 * </p>
 *
 * Value encoding:
 * 0-11: the depth to which this position was last searched.
 * 12-15: the {@link HashFlag} indicating what type of node this is.
 * 16-31: the {@link Move} start square, end square, and special move flag.
 * 32-63: the eval of the position in centipawns.
 */
public record HashEntry(Move move, int score, int staticEval, HashFlag flag, int depth) {

    // The hash entry is packed into two 64-bit longs - therefore the byte size is 16.
    public static final int SIZE_BYTES = 16;

    /**
     * Check if the hash entry can be re-used in the current search. A TT-hit is useful either if it 1) contains an
     * exact score, 2) contains a fail-high >= current beta, or 3) contains a fail-low <= current alpha.
     */
    public boolean isWithinBounds(int alpha, int beta) {
        return flag.equals(HashFlag.EXACT) ||
                (hasScore() &&
                (flag.equals(HashFlag.UPPER) && score <= alpha ||
                flag.equals(HashFlag.LOWER) && score >= beta));
    }

    public boolean isSufficientDepth(int otherDepth) {
        return depth >= otherDepth;
    }

    public boolean hasScore() {
        return !Score.isUndefinedScore(score);
    }

    public static HashEntry of(long key, long value) {
        return new HashEntry(
                Value.getMove(value),
                Value.getScore(value),
                Key.getStaticEval(key),
                Value.getFlag(value),
                Value.getDepth(value)
        );
    }

    public static class Key {

        private static final long STATIC_EVAL_MASK    = 0xffff000000000000L;
        private static final long AGE_MASK            = 0x0000ffff00000000L;
        private static final long ZOBRIST_PART_MASK   = 0x00000000ffffffffL;

        public static long getZobristPart(long key) {
            return key & ZOBRIST_PART_MASK;
        }

        public static int getAge(long key) {
            return (int) ((key & AGE_MASK) >>> 32);
        }

        public static long setAge(long key, int age) {
            return (key & ~AGE_MASK) | ((long) age << 32);
        }

        public static int getStaticEval(long key) {
            return (short) ((key & STATIC_EVAL_MASK) >>> 48);
        }

        public static long of(long zobristKey, int staticEval, int age) {
            return (zobristKey & ZOBRIST_PART_MASK) | ((long) age << 32) | ((long) (staticEval & 0xFFFF) << 48);
        }

    }

    public static class Value {

        private static final long SCORE_MASK    = 0xffffffff00000000L;
        private static final long MOVE_MASK     = 0x00000000ffff0000L;
        private static final long FLAG_MASK     = 0x000000000000f000L;
        private static final long DEPTH_MASK    = 0x0000000000000fffL;

        public static int getScore(long value) {
            return (int) ((value & SCORE_MASK) >>> 32);
        }

        public static long setScore(long value, int score) {
            return (value & ~SCORE_MASK) | (long) score << 32;
        }

        public static Move getMove(long value) {
            long move = (value & MOVE_MASK) >>> 16;
            return move > 0 ? new Move((short) move) : null;
        }

        public static long setMove(long value, Move move) {
            return (value &~ MOVE_MASK) | (long) move.value() << 16;
        }

        public static HashFlag getFlag(long value) {
            long flag = (value & FLAG_MASK) >>> 12;
            return HashFlag.valueOf((int) flag);
        }

        public static int getDepth(long value) {
            return (int) (value & DEPTH_MASK);
        }

        public static long of(int score, Move move, HashFlag flag, int depth) {
            long moveValue = move != null ? move.value() : 0;
            long flagValue = HashFlag.value(flag);
            return (long) score << 32 | moveValue << 16 | flagValue << 12 | depth;
        }

    }

}