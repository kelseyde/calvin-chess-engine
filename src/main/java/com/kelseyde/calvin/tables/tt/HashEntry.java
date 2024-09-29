package com.kelseyde.calvin.tables.tt;

import com.kelseyde.calvin.board.Move;

/**
 * Entry in the {@link TranspositionTable}.
 * Contains a 64-bit key containing part of the zobrist hash plus metadata, a 32-bit score, and a 16-bit move.
 * </p>
 *
 * Key encoding:
 * 0-31: 32 bits of the zobrist key.
 * 32-37: 6 bits representing the age of the entry.
 * 38-39: 2 bits representing the {@link HashFlag} of the entry.
 * 40-47: 8 bits representing the depth that was searched.
 * 48-63: 16 bits representing the static evaluation of the position.
 * </p>
 *
 *
 * @see <a href="https://www.chessprogramming.org/Transposition_Table">Chess Programming Wiki</a>
 */
public class HashEntry {

    public static final int SIZE_BYTES = 26;

    // Masks for the key
    private static final long ZOBRIST_PART_MASK = 0x00000000FFFFFFFFL; // 32 bits for Zobrist key
    private static final long AGE_MASK = 0x0000003F00000000L;          // 6 bits for age (bits 32-37)
    private static final long FLAG_MASK = 0x000000C000000000L;         // 2 bits for HashFlag (bits 38-39)
    private static final long DEPTH_MASK = 0x0000FF0000000000L;        // 8 bits for depth (bits 40-47)
    private static final long STATIC_EVAL_MASK = 0xFFFF000000000000L;  // 16 bits for static evaluation (bits 48-63)

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
     * Gets the age part of this entry's key.
     */
    public int getAge() {
        // Extract the 6-bit age part from bits 32-37
        return (int) ((key & AGE_MASK) >>> 32);
    }

    /**
     * Sets the age part of this entry's key.
     */
    public void setAge(int age) {
        // Clamp the age to the maximum value representable by 6 bits
        age = Math.min(age, 63);
        // Clear the age bits and set the new generation value
        key = (key & ~AGE_MASK) | ((long) age << 32);
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
        return move > 0 ? new Move(move) : null;
    }

    /**
     * Gets the flag from this entry's key.
     */
    public HashFlag getFlag() {
        // Extract the 2-bit flag from bits 38-39
        long flag = (key & FLAG_MASK) >>> 38;
        return HashFlag.valueOf((int) flag);
    }

    /**
     * Gets the depth from this entry's key.
     */
    public int getDepth() {
        // Extract the 8-bit depth from bits 40-47
        return (int) ((key & DEPTH_MASK) >>> 40);
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
     * @param age the generation
     * @return a new {@link HashEntry}
     */
    public static HashEntry of(long zobristKey, int score, int staticEval, Move move, HashFlag flag, int depth, int age) {

        // clamp values to their respective bit sizes
        age = Math.min(age, 63);
        depth = Math.min(depth, 255);
        staticEval = staticEval & 0xFFFF;

        // encode the move and flag
        short moveValue = move != null ? move.value() : 0;
        byte flagValue = (byte) HashFlag.value(flag);

        long key = zobristKey & ZOBRIST_PART_MASK;
        key |= (long) age << 32;
        key |= (long) flagValue << 38;
        key |= (long) depth << 40;
        key |= (long) staticEval << 48;

        return new HashEntry(key, moveValue, score);
    }

}