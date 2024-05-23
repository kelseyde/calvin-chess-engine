package com.kelseyde.calvin.transposition;

import com.kelseyde.calvin.board.Move;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Entry in the {@link TranspositionTable}. Contains a 64-bit key and a 64-bit value which encode the relevant information
 * about the position.
 * </p>
 *
 * Key encoding:
 * 0-31: the half of the zobrist hash NOT used to generate the entry's index. Used to verify that the position does match.
 * 32-63: the generation of the entry, i.e. how old it is. Used in the replacement scheme to gradually phase out old entries.
 * </p>
 *
 * Value encoding:
 * 0-11: the depth to which this position was last searched.
 * 12-15: the {@link HashFlag} indicating what type of node this is.
 * 16-31: the {@link Move} start square, end square, and special move flag.
 * 32-63: the eval of the position in centipawns.
 */
@AllArgsConstructor
public class HashEntry {

    private static final long ZOBRIST_MASK = 0x00000000ffffffffL;
    private static final long GENERATION_MASK = 0xffffffff00000000L;
    private static final long SCORE_MASK = 0xffffffff00000000L;
    private static final long MOVE_MASK = 0x00000000ffff0000L;
    private static final long FLAG_MASK = 0x000000000000f000L;
    private static final long DEPTH_MASK = 0x0000000000000fffL;

    private long key;
    private long value;

    public static long halfZobrist(long zobrist) {
        return zobrist & ZOBRIST_MASK;
    }

    public long getHalfZobrist() {
        return key & ZOBRIST_MASK;
    }

    public int getGeneration() {
        return (int) ((key & GENERATION_MASK) >>> 32);
    }

    public void setGeneration(int generation) {
        key = (key &~ GENERATION_MASK) | (long) generation << 32;
    }

    public int getScore() {
        long score = (value & SCORE_MASK) >>> 32;
        return (int) score;
    }

    public void setScore(int score) {
        value = (value &~ SCORE_MASK) | (long) score << 32;
    }

    public HashEntry withAdjustedScore(int score) {
        long newValue = (value &~ SCORE_MASK) | (long) score << 32;
        return new HashEntry(key, newValue);
    }

    public void setMove(Move move) {
        value = (value &~ MOVE_MASK) | (long) move.value() << 16;
    }

    public Move getMove() {
        long move = (value & MOVE_MASK) >>> 16;
        return move > 0 ? new Move((short) move) : null;
    }

    public HashFlag getFlag() {
        long flag = (value & FLAG_MASK) >>> 12;
        return HashFlag.valueOf((int) flag);
    }

    public int getDepth() {
        return (int) (value & DEPTH_MASK);
    }

    public static HashEntry of(long zobristKey, int score, Move move, HashFlag flag, int depth, int generation) {
        long key = halfZobrist(zobristKey) | generation;
        long moveValue = move != null ? move.value() : 0;
        long flagValue = HashFlag.value(flag);
        long value = (long) score << 32 | moveValue << 16 | flagValue << 12 | depth;
        return new HashEntry(key, value);
    }

//    public static HashEntry withScore(HashEntry entry, int score) {
//        long value = (entry.value() &~ SCORE_MASK) | (long) score << 32;
//        return new HashEntry(entry.key(), value);
//    }

}
