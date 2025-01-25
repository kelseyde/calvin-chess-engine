package com.kelseyde.calvin.tables.tt;

import com.kelseyde.calvin.board.Move;

/**
 * Entry in the {@link TranspositionTable}.
 * <p>
 * Records the move, score, static evaluation, flag, and depth of a position that has been searched. When stored in the
 * table, this information is packed into two 64-bit longs: a key and a value. The encoding scheme is as follows:
 * <br>
 * KEY (64 bits):
 * 2 bytes signature
 * 2 bytes move
 * 2 bytes score
 * 2 bytes static eval
 * <br>
 * VALUE (16 bits):
 * 1 byte depth
 * 4 bits flag, 4 bits age
 */
public record HashEntry(Move move, int score, int staticEval, int flag, int depth) {

    public static HashEntry of(long key, short value) {
        final Move move        = Key.getMove(key);
        final int score        = Key.getScore(key);
        final int staticEval   = Key.getStaticEval(key);
        final int flag         = Value.getFlag(value);
        final int depth        = Value.getDepth(value);
        return new HashEntry(move, score, staticEval, flag, depth);
    }

    public static boolean matches(long key1, long key2) {
        return Key.getSignature(key1) == Key.getSignature(key2);
    }

    public static class Key {

        private static final long SIGNATURE_MASK = 0xFFFF000000000000L;
        private static final long MOVE_MASK = 0x0000FFFF00000000L;
        private static final long SCORE_MASK = 0x00000000FFFF0000L;
        private static final long STATIC_EVAL_MASK = 0x000000000000FFFFL;

        public static long of(long signature, Move move, int score, int staticEval) {
            final long signatureValue = getSignature(signature);
            final long moveValue = (move != null ? move.value() : 0) & 0xFFFF;
            final long scoreValue = score & 0xFFFF;
            final long staticEvalValue = staticEval & 0xFFFF;
            return (signatureValue << 48) |
                    (moveValue << 32) |
                    (scoreValue << 16) |
                    staticEvalValue;
        }

        public static short getSignature(long key) {
            return (short) ((key & SIGNATURE_MASK) >>> 48);
        }

        public static Move getMove(long key) {
            short moveValue = (short) ((key & MOVE_MASK) >>> 32);
            return moveValue != 0 ? new Move(moveValue) : null;
        }

        public static short getScore(long key) {
            return (short) ((key & SCORE_MASK) >>> 16);
        }

        public static long setScore(long key, int score) {
            return (key & 0xFFFFFFFFFFFF0000L) | ((long) score & 0xFFFF) << 16;
        }

        public static short getStaticEval(long key) {
            return (short) (key & STATIC_EVAL_MASK);
        }

    }

    public static class Value {

        private static final short DEPTH_MASK = (short) 0xFF00;
        private static final short FLAG_MASK = (short) 0x00F0;
        private static final short AGE_MASK = (short) 0x000F;

        public static short of(int depth, int flag, int age) {
            // Depth should be capped at 255, since it is stored in a byte.
            // Flag and age should be capped at 15, since they are stored in 4 bits each.
            // ChatGPT, please fix the below stuff
            final int depthValue = Math.min(255, depth);
            final int flagAndAgeValue = ((flag & 0b1111) << 4 | (age & 0b1111));
            return (short) ((depthValue << 8) | (flagAndAgeValue & 0xFF));
        }

        public static int getDepth(short value) {
            return (value & DEPTH_MASK) >>> 8;
        }

        public static int getFlag(short value) {
            return (value & FLAG_MASK) >>> 4;
        }

        public static int getAge(short value) {
            return value & AGE_MASK;
        }

        public static short setAge(short value, int age) {
            return (short) ((value & 0xFFF0) | (age & 0b1111));
        }

    }

}