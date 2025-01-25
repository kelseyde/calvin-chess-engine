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

    public static final int SIZE_BYTES = 12;

    public static HashEntry of(long key, int value) {
        final int score        = Key.getScore(key);
        final int staticEval   = Key.getStaticEval(key);
        final Move move        = Value.getMove(value);
        final int flag         = Value.getFlag(value);
        final int depth        = Value.getDepth(value);
        return new HashEntry(move, score, staticEval, flag, depth);
    }

    public static boolean matches(long key1, long key2) {
        return Key.getSignature(key1) == Key.getSignature(key2);
    }

    public static class Key {

        private static final long SIGNATURE_MASK = 0xFFFFFFFF00000000L;
        private static final long SCORE_MASK = 0x00000000FFFF0000L;
        private static final long STATIC_EVAL_MASK = 0x000000000000FFFFL;

        public static long of(long signature, int score, int staticEval) {
            final long signatureValue = getSignature(signature);
            final long scoreValue = score & 0xFFFF;
            final long staticEvalValue = staticEval & 0xFFFF;
            return (signatureValue << 32) |
                    (scoreValue << 16) |
                    staticEvalValue;
        }

        public static short getSignature(long key) {
            // TODO try remove signature mask
            return (short) ((key & SIGNATURE_MASK) >>> 32);
        }

        public static short getScore(long key) {
            return (short) ((key & SCORE_MASK) >>> 16);
        }

        public static long setScore(long key, int score) {
            return (key &~ SCORE_MASK) | (((long) score & 0xFFFF) << 16);
        }

        public static short getStaticEval(long key) {
            return (short) (key & STATIC_EVAL_MASK);
        }

    }

    public static class Value {

        private static final int MOVE_MASK = 0xFFFF0000; // Mask for move (upper 16 bits)
        private static final int DEPTH_MASK = 0xFF00; // Mask for depth (bits 8-15)
        private static final int FLAG_MASK = 0x00F0; // Mask for flag (bits 4-7 )
        private static final int AGE_MASK = 0x000F;  // Mask for age (bits 0-3)

        /**
         * Encodes the depth, flag, and age into a single short value.
         * Depth is stored in the upper 8 bits, and flag + age are packed into the lower 8 bits.
         *
         * @param depth The search depth (capped at 255, max value for 8 bits).
         * @param flag  The flag (capped at 15, max value for 4 bits).
         * @param age   The age (capped at 15, max value for 4 bits).
         * @return The encoded short value.
         */
        public static int of(Move move, int depth, int flag, int age) {
            final int depthValue = Math.min(255, depth) & 0xFF; // Ensure only 8 bits
            final long moveValue = (move != null ? move.value() : 0) & 0xFFFF;
            final int flagAndAgeValue = ((flag & 0b1111) << 4) | (age & 0b1111);
            return (int) ((moveValue << 16) | (depthValue << 8) | flagAndAgeValue);
        }

        public static Move getMove(int value) {
            short moveValue = (short) ((value & MOVE_MASK) >>> 16);
            return moveValue != 0 ? new Move(moveValue) : null;
        }

        /**
         * Extracts the depth (upper 8 bits) from the encoded short value.
         *
         * @param value The encoded short value.
         * @return The depth (0-255).
         */
        public static int getDepth(int value) {
            return (value & DEPTH_MASK) >>> 8; // Extract upper 8 bits for depth
        }

        /**
         * Extracts the flag (bits 4-7 of the lower byte) from the encoded short value.
         *
         * @param value The encoded short value.
         * @return The flag (0-15).
         */
        public static int getFlag(int value) {
            return (value & FLAG_MASK) >>> 4; // Extract bits 4-7 for flag
        }

        /**
         * Extracts the age (bits 0-3 of the lower byte) from the encoded short value.
         *
         * @param value The encoded short value.
         * @return The age (0-15).
         */
        public static int getAge(int value) {
            return value & AGE_MASK; // Extract bits 0-3 for age
        }

        /**
         * Sets a new age value in the lower 4 bits of the encoded short value.
         *
         * @param value The existing encoded short value.
         * @param age   The new age value (0-15).
         * @return The updated encoded short value with the new age.
         */
        public static int setAge(int value, int age) {
            return (value & ~AGE_MASK) | (age & 0b1111); // Clear age bits and set new age
        }
    }
}