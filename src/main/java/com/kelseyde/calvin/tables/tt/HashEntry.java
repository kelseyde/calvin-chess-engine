package com.kelseyde.calvin.tables.tt;

import com.kelseyde.calvin.board.Move;

/**
 * Entry in the {@link TranspositionTable}.
 * Contains a 64-bit key and a 32-bit value which encode the relevant information about the position.
 * </p>
 *
 * Key encoding:
 * 0-31: 32 bits of the zobrist key.
 * 32-37: 6 bits representing the age of the entry.
 * 38-39: 2 bits representing the {@link HashFlag} of the entry.
 * 40-47: 8 bits representing the depth that was searched.
 *
 * </p>
 *
 * Value encoding:
 * 0-31: 32 bits representing the score of the position.
 * 32-47: 16 bits representing the move that was played.
 *
 * @see <a href="https://www.chessprogramming.org/Transposition_Table">Chess Programming Wiki</a>
 */
public class HashEntry {

    public static final int SIZE_BYTES = 32;

    // Masks for the key
    private static final long ZOBRIST_PART_MASK = 0x00000000ffffffffL;
    private static final long GENERATION_MASK = 0x0000ffff00000000L;
    private static final long STATIC_EVAL_MASK = 0xffff000000000000L;
    private static final long FLAG_MASK = 0x000000000000f000L;
    private static final long DEPTH_MASK = 0x0000000000000fffL;


    private long key;
    private short move;
    private int score;

    public HashEntry(long key, short move, int score) {
        this.key = key;
        this.move = move;
        this.score = score;
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
     * Gets the generation part of this entry's key.
     */
    public int getAge() {
        return (int) ((key & GENERATION_MASK) >>> 32);
    }

    /**
     * Sets the generation part of this entry's key.
     */
    public void setAge(int generation) {
        key = (key & ~GENERATION_MASK) | ((long) generation << 32);
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
        this.key = (key & ~STATIC_EVAL_MASK) | ((long) (staticEval & 0xFFFF) << 48);
    }

    /**
     * Gets the score from this entry's value.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score in this entry's value.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Creates a new {@link HashEntry} with the adjusted score.
     */
    public HashEntry withAdjustedScore(int newScore) {
        return new HashEntry(key, move, newScore);
    }

    /**
     * Sets the move in this entry's value.
     */
    public void setMove(Move move) {
        this.move = move != null ? move.value() : 0;
    }

    /**
     * Gets the move from this entry's value.
     */
    public Move getMove() {
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
        return (int) (value & DEPTH_MASK);
    }

    public boolean isSufficientDepth(int depth) {
        return getDepth() >= depth;
    }

    /**
     * Check if the hit from the transposition table is 'useful' in the current search. A TT-hit is useful either if it
     * 1) contains an exact evaluation, so we don't need to search any further, 2) contains a fail-high greater than our
     * current beta value, or 3) contains a fail-low lesser than our current alpha value.
     */
    public boolean isWithinBounds(int alpha, int beta) {
        return getFlag().equals(HashFlag.EXACT) ||
                (getFlag().equals(HashFlag.UPPER) && getScore() <= alpha) ||
                (getFlag().equals(HashFlag.LOWER) && getScore() >= beta);
    }

    /**
     * Creates a new {@link HashEntry} with the specified parameters.
     *
     * @param zobristKey the Zobrist key
     * @param score the score
     * @param move the move
     * @param flag the flag
     * @param depth the depth
     * @param generation the generation
     * @return a new {@link HashEntry}
     */
    public static HashEntry of(long zobristKey, int score, int staticEval, Move move, HashFlag flag, int depth, int generation) {
        // Build the key using 32 bits for the zobrist part, 16 bits for the generation part, and 16 bits for the static evaluation part.
        long key = (zobristKey & ZOBRIST_PART_MASK) | ((long) generation << 32) | ((long) (staticEval & 0xFFFF) << 48);
        // Get the 16-bit encoded move
        long moveValue = move != null ? move.value() : 0;
        // Get the 3-bit encoded flag
        long flagValue = HashFlag.value(flag);
        // Combine the score, move, flag and depth to create the hash entry value
        long value = (long) score << 32 | moveValue << 16 | flagValue << 12 | depth;
        return new HashEntry(key, value);
    }

}