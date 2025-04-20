package com.kelseyde.calvin.tables.tt;

import com.kelseyde.calvin.board.Move;

import java.util.Objects;

/**
 * Entry in the {@link TranspositionTable}.
 * </p>
 * Records the move, score, static evaluation, flag, and depth of a position that has been searched. When stored in the
 * table, this information is packed into a 64-bit long (key) and a 32-bit integer (value). The encoding scheme is as follows:
 * - Key: 0-31 (zobrist key), 32-39 (depth), 40-55 (static eval), 56-57 (flag), 58 (pv), 59-63 (unused)
 * - Value: 0-15 (score), 16-31 (move)
 */
public class HashEntry {

    public Move move;
    public int score;
    public int staticEval;
    public int flag;
    public int depth;
    public boolean pv;
    public boolean exists;

    public HashEntry() {}

    public void init(long key, int value) {
        this.move = Value.getMove(value);
        this.score = Value.getScore(value);
        this.staticEval = Key.getStaticEval(key);
        if (this.staticEval == Short.MIN_VALUE)
            this.staticEval = Integer.MIN_VALUE;
        this.flag = Key.getFlag(key);
        this.depth = Key.getDepth(key);
        this.pv = Key.isPv(key);
        this.exists = true;
    }

    public static class Key {

        private static final long ZOBRIST_PART_MASK = 0x00000000ffffffffL;
        private static final long DEPTH_MASK = 0x000000ff00000000L;
        private static final long STATIC_EVAL_MASK = 0x00ffff0000000000L;
        private static final long FLAG_MASK = 0x0300000000000000L;
        private static final long PV_MASK = 0x0400000000000000L;

        public static long getZobristPart(long key) {
            return key & ZOBRIST_PART_MASK;
        }

        public static int getDepth(long key) {
            return (int) ((key & DEPTH_MASK) >>> 32);
        }

        public static int getStaticEval(long key) {
            return (short) ((key & STATIC_EVAL_MASK) >>> 40);
        }

        public static int getFlag(long key) {
            return (int) ((key & FLAG_MASK) >>> 56);
        }

        public static boolean isPv(long key) {
            return ((key & PV_MASK) >>> 58) == 1;
        }

        public static long of(long zobristKey, int depth, int staticEval, int flag, boolean pv) {
            if (staticEval == Integer.MIN_VALUE)
                staticEval = Short.MIN_VALUE;
            depth = Math.min(depth, 255);
            long pvBit = pv ? 1L : 0L;
            return (zobristKey & ZOBRIST_PART_MASK) |
                    ((long) depth << 32) |
                    ((long) (staticEval & 0xFFFF) << 40) |
                    ((long) (flag & 0x3) << 56) |
                    (pvBit << 58);
        }
    }

    public static class Value {

        private static final int SCORE_MASK = 0x0000ffff;
        private static final int MOVE_MASK = 0xffff0000;

        public static int getScore(int value) {
            return (short) (value & SCORE_MASK);
        }

        public static int setScore(int value, int score) {
            return (value & MOVE_MASK) | (score & SCORE_MASK);
        }

        public static Move getMove(int value) {
            int move = (value & MOVE_MASK) >>> 16;
            return move > 0 ? new Move((short) move) : null;
        }

        public static int of(int score, Move move) {
            int moveValue = move != null ? move.value() : 0;
            return (moveValue << 16) | (score & 0xFFFF);
        }
    }

    private static void assertRange(int value, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException("Value out of range: " + value);
        }
    }

    public Move move() {
        return move;
    }

    public int score() {
        return score;
    }

    public int staticEval() {
        return staticEval;
    }

    public int flag() {
        return flag;
    }

    public int depth() {
        return depth;
    }

    public boolean pv() {
        return pv;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (HashEntry) obj;
        return Objects.equals(this.move, that.move) &&
                this.score == that.score &&
                this.staticEval == that.staticEval &&
                this.flag == that.flag &&
                this.depth == that.depth &&
                this.pv == that.pv;
    }

    @Override
    public int hashCode() {
        return Objects.hash(move, score, staticEval, flag, depth, pv);
    }

    @Override
    public String toString() {
        return "HashEntry[" +
                "move=" + move + ", " +
                "score=" + score + ", " +
                "staticEval=" + staticEval + ", " +
                "flag=" + flag + ", " +
                "depth=" + depth + ", " +
                "pv=" + pv + ']';
    }

}