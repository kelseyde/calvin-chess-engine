package com.kelseyde.calvin.tables.tt;

import com.kelseyde.calvin.board.Move;

/**
 * Entry in the {@link TranspositionTable}.
 * </p>
 * Records the move, score, static evaluation, flag, and depth of a position that has been searched. When stored in the
 * table, this information is packed into two 64-bit longs: a key and a value. The encoding scheme is as follows:
 * - Key: 0-31 (zobrist key), 32-47 (age), 48-63 (static eval)
 * - Value: 0-11 (depth), 12-15 (flag), 16-31 (move), 32-63 (score)
 *
 *
 * Key: 32 bits
 * Score: 16 bits
 * Eval: 16 bits
 *
 * Flag: 2 bits
 * Move: 16 bits
 * Depth: 8 bits
 * total: 90 bits
 */
public record HashEntry(Move move, int score, int staticEval, int flag, int depth) {

    public static HashEntry of(long key, int value) {
        final Move move       = Value.getMove(value);
        final int flag        = Value.getFlag(value);
        final int depth       = Value.getDepth(value);
        final int score       = Key.getScore(key);
        final int staticEval  = Key.getStaticEval(key);
        return new HashEntry(move, score, staticEval, flag, depth);
    }

    public static class Key {

        private static final long SIGNATURE_MASK    = 0x00000000ffffffffL;
        private static final long SCORE_MASK        = 0x0000ffff00000000L;
        private static final long STATIC_EVAL_MASK  = 0xffff000000000000L;

        public static long getZobristPart(long key) {
            return key & SIGNATURE_MASK;
        }

        public static int getScore(long key) {
            return (short) ((key & SCORE_MASK) >> 32);
        }

        public static long setScore(long key, int score) {
            return (key & ~SCORE_MASK) | ((long) score << 32);
        }

        public static int getStaticEval(long key) {
            return (short) ((key & STATIC_EVAL_MASK) >> 48);
        }

        public static long of(long signature, int score, int staticEval) {
            if (score > Short.MAX_VALUE || score < Short.MIN_VALUE) {
                throw new IllegalArgumentException("Score must be a 16-bit integer, is " + score);
            }
            if (staticEval > Short.MAX_VALUE || staticEval < Short.MIN_VALUE) {
                throw new IllegalArgumentException("Static eval must be a 16-bit integer, is " + staticEval);
            }
            return (signature & SIGNATURE_MASK) | ((long) score << 32) | ((long) staticEval << 48);
        }

    }

    public static class Value {

        private static final int MOVE_MASK  = 0xffff0000;
        private static final int FLAG_MASK  = 0x0000f000;
        private static final int DEPTH_MASK = 0x00000fff;

        public static Move getMove(int value) {
            long move = (value & MOVE_MASK) >>> 16;
            return move > 0 ? new Move((short) move) : null;
        }

        public static int getFlag(int value) {
            return (value & FLAG_MASK) >>> 12;
        }

        public static int getDepth(int value) {
            return value & DEPTH_MASK;
        }

        public static int of(Move move, int flag, int depth) {
            short moveValue = move != null ? move.value() : 0;
            return (int) ((moveValue << 16) | ((long) flag << 12) | depth);
        }

    }

}