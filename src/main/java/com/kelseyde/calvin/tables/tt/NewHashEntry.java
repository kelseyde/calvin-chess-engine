package com.kelseyde.calvin.tables.tt;

import com.kelseyde.calvin.board.Move;

/**
 * Entry in the {@link TranspositionTable}.
 * - 2 byte signature
 * - 2 byte move
 * - 2 byte score
 * - 2 byte static eval
 * - 1 byte depth
 * - 1 byte flag + age
 */
public record NewHashEntry(Move move, int score, int staticEval, int flag, int depth) {

    public static class Key {

        public static long of(long signature, Move move, int score, int staticEval) {
            // Take the lowest 16 bits of the signature
            final short signatureValue    = (short) (signature & 0xFFFF);
            final short moveValue         = move.value();
            final short scoreValue        = (short) score;
            final short staticEvalValue   = (short) staticEval;
            return (long) signatureValue << 48 |
                   (long) moveValue << 32 |
                   (long) scoreValue << 16 |
                   (long) staticEvalValue;
        }

        public static short getSignature(long key) {
            return (short) (key >>> 48);
        }

        public static Move getMove(long key) {
            return new Move((short) (key >>> 32));
        }

        public static short getScore(long key) {
            return (short) (key >>> 16);
        }

        public static short getStaticEval(long key) {
            return (short) key;
        }

    }

    public static class Value {

        public static short of(int depth, int flag, int age) {
            final byte depthValue = (byte) Math.min(Byte.MAX_VALUE, depth);
            final byte flagValue = (byte) flag;
            final byte ageValue = (byte) Math.min(Byte.MAX_VALUE, age);
            return (short) (depthValue << 8 | flagValue << 4 | ageValue);
        }

        public static int getDepth(short value) {
            return value >>> 8;
        }

        public static int getFlag(short value) {
            return (value & 0b0000111100000000) >>> 4;
        }

        public static int getAge(short value) {
            return value & 0b0000000011111111;
        }

    }

}
