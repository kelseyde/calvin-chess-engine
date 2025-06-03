package com.kelseyde.calvin.tables.tt;

import com.kelseyde.calvin.board.Move;

/**
 * Entry in the {@link TranspositionTable}.
 * </p>
 * Records the move, score, static evaluation, flag, and depth of a position that has been searched. When stored in the
 * table, this information is packed into two 64-bit longs: a key and a value. The encoding scheme is as follows:
 * - Key: 0-31 (zobrist key), 32-47 (age), 48-63 (static eval)
 * - Value: 0-11 (depth), 12-15 (flag), 16-31 (move), 32-63 (score)
 */
public record HashEntry(Move move, int score, int staticEval, int flag, int depth, boolean pv) {

    public static HashEntry of(long key, long value) {
        final Move move       = Value.getMove(value);
        final int flag        = Value.getFlag(value);
        final int depth       = Value.getDepth(value);
        final int score       = Value.getScore(value);
        final int staticEval  = Key.getStaticEval(key);
        final boolean pv      = Value.isPv(value);
        return new HashEntry(move, score, staticEval, flag, depth, pv);
    }

    public static class Key {

        private static final long STATIC_EVAL_MASK    = 0xffff000000000000L;
        private static final long AGE_MASK            = 0x0000ffff00000000L;
        private static final long ZOBRIST_PART_MASK   = 0x00000000ffffffffL;

        public static boolean matches(long key1, long key2) {
            return getZobristPart(key1) == getZobristPart(key2);
        }

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
        private static final long PV_MASK       = 0x0000000000000f00L;
        private static final long DEPTH_MASK    = 0x00000000000000ffL;

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

        public static int getFlag(long value) {
            return (int) (value & FLAG_MASK) >>> 12;
        }

        public static int getDepth(long value) {
            return (int) (value & DEPTH_MASK);
        }

        public static boolean isPv(long value) {
            return (value & PV_MASK) >>> 8 == 1;
        }

        public static long setDepth(long value, int depth) {
            return (value & ~DEPTH_MASK) | (long) depth;
        }

        public static long of(int score, Move move, int flag, int depth, boolean pv) {
            depth = Math.min(depth, 255);
            long pvFlag = pv ? 1 : 0;
            long moveValue = move != null ? move.value() : 0;
            return (long) score << 32 | moveValue << 16 | (long) flag << 12 | pvFlag << 8 | depth;
        }

    }

}