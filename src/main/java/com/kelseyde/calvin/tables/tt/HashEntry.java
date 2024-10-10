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
public class HashEntry {

    public static final int SIZE_BYTES = 16;

    private static final long ZOBRIST_PART_MASK = 0x00000000ffffffffL;
    private static final long AGE_MASK = 0x0000ffff00000000L;
    private static final long STATIC_EVAL_MASK = 0xffff000000000000L;
    private static final long SCORE_MASK = 0xffffffff00000000L;
    private static final long MOVE_MASK = 0x00000000ffff0000L;
    private static final long FLAG_MASK = 0x000000000000f000L;
    private static final long DEPTH_MASK = 0x0000000000000fffL;

    private long key;
    private long value;

    public HashEntry(long key, long value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Extracts the 48-bits representing the zobrist part of the given zobrist key.
     */
    public static long zobristPart(long zobrist) {
        return zobrist & ZOBRIST_PART_MASK;
    }


    /**
     * Returns the 48-bits representing zobrist part of the hash entry key.
     */
    public long getZobristPart() {
        return key & ZOBRIST_PART_MASK;
    }

    /**
     * Gets the age part of this entry's key.
     */
    public int getAge() {
        return age(key);
    }

    /**
     * Sets the age part of this entry's key.
     */
    public void setAge(int age) {
        key = (key & ~AGE_MASK) | ((long) age << 32);
    }

    public static int age(long key) {
        return (int) ((key & AGE_MASK) >>> 32);
    }

    public static long withAge(long key, int age) {
        return (key & ~AGE_MASK) | ((long) age << 32);
    }

    /**
     * Gets the static eval part of this entry's key.
     */
    public int getStaticEval() {
        return (short) ((key & STATIC_EVAL_MASK) >>> 48);
    }

    /**
     * Sets the static eval part of this entry's key.
     */
    public void setStaticEval(int staticEval) {
        key = (key & ~STATIC_EVAL_MASK) | ((long) (staticEval & 0xFFFF) << 48);
    }

    /**
     * Gets the score from this entry's value.
     */
    public int getScore() {
        return score(value);
    }

    public static int score(long value) {
        long score = (value & SCORE_MASK) >>> 32;
        return (int) score;
    }

    public static long withScore(long value, int score) {
        return (value & ~SCORE_MASK) | (long) score << 32;
    }

    /**
     * Sets the score in this entry's value.
     */
    public void setScore(int score) {
        value = (value &~ SCORE_MASK) | (long) score << 32;
    }

    /**
     * Creates a new {@link HashEntry} with the adjusted score.
     */
    public HashEntry withAdjustedScore(int score) {
        long newValue = (value &~ SCORE_MASK) | (long) score << 32;
        return new HashEntry(key, newValue);
    }

    /**
     * Sets the move in this entry's value.
     */
    public void setMove(Move move) {
        value = (value &~ MOVE_MASK) | (long) move.value() << 16;
    }

    /**
     * Gets the move from this entry's value.
     */
    public Move getMove() {
        return move(value);
    }

    public static Move move(long value) {
        long move = (value & MOVE_MASK) >>> 16;
        return move > 0 ? new Move((short) move) : null;
    }

    /**
     * Gets the flag from this entry's value.
     */
    public HashFlag getFlag() {
        long flag = (value & FLAG_MASK) >>> 12;
        return HashFlag.valueOf((int) flag);
    }

    /**
     * Gets the depth from this entry's value.
     */
    public int getDepth() {
        return depth(value);
    }

    public static int depth(long value) {
        return (int) (value & DEPTH_MASK);
    }

    public boolean isSufficientDepth(int depth) {
        return getDepth() >= depth;
    }

    public boolean hasScore() {
        return !Score.isUndefinedScore(getScore());
    }

    /**
     * Check if the hit from the transposition table is 'useful' in the current search. A TT-hit is useful either if it
     * 1) contains an exact evaluation, so we don't need to search any further, 2) contains a fail-high greater than our
     * current beta value, or 3) contains a fail-low lesser than our current alpha value.
     */
    public boolean isWithinBounds(int alpha, int beta) {
        return getFlag().equals(HashFlag.EXACT) ||
                (hasScore() &&
                (getFlag().equals(HashFlag.UPPER) && getScore() <= alpha ||
                getFlag().equals(HashFlag.LOWER) && getScore() >= beta));
    }

    /**
     * Creates a new {@link HashEntry} with the specified parameters.
     */
    public static HashEntry of(long zobristKey, int score, int staticEval, Move move, HashFlag flag, int depth, int age) {
        long key = key(zobristKey, staticEval, age);
        long value = value(score, move, flag, depth);
        return new HashEntry(key, value);
    }

    public static long key(long zobristKey, int staticEval, int age) {
        return (zobristKey & ZOBRIST_PART_MASK) | ((long) age << 32) | ((long) (staticEval & 0xFFFF) << 48);
    }

    public static long value(int score, Move move, HashFlag flag, int depth) {
        long moveValue = move != null ? move.value() : 0;
        long flagValue = HashFlag.value(flag);
        return (long) score << 32 | moveValue << 16 | flagValue << 12 | depth;
    }

}