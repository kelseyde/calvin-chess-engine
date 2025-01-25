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

        public static long of(long signature, Move move, int score, int staticEval) {
            // Take the lowest 16 bits of the signature
            final short signatureValue    = (short) (signature & 0xFFFF);
            final short moveValue         = move != null ? move.value() : 0;
            final short scoreValue        = (short) score;
            final short staticEvalValue   = (short) staticEval;
            return ((long) signatureValue << 48) |
                    ((long) moveValue << 32) |
                    ((long) scoreValue << 16) |
                    (long) staticEvalValue;
        }

        public static short getSignature(long key) {
            return (short) (key >>> 48);
        }

        public static Move getMove(long key) {
            short moveValue = (short) (key >>> 32);
            return moveValue != 0 ? new Move(moveValue) : null;
        }

        public static short getScore(long key) {
            return (short) (key >>> 16);
        }

        public static long setScore(long key, int score) {
            return (key & 0xFFFFFFFFFFFF0000L) | ((long) score & 0xFFFF) << 16;
        }

        public static short getStaticEval(long key) {
            return (short) key;
        }

    }

    public static class Value {

        public static short of(int depth, int flag, int age) {
            final byte depthValue = (byte) Math.min(Byte.MAX_VALUE, depth);
            final byte flagAndAgeValue = (byte) ((flag & 0b1111) << 4 | (age & 0b1111));
            return (short) ((depthValue << 8) | (flagAndAgeValue & 0xFF));
        }

        public static int getDepth(short value) {
            return (value >>> 8) & 0xFF;
        }

        public static int getFlag(short value) {
            return (value >>> 4) & 0b1111;
        }

        public static int getAge(short value) {
            return value & 0b1111;
        }

        public static short setAge(short value, int age) {
            return (short) ((value & 0xFFF0) | (age & 0b1111));
        }

    }

}