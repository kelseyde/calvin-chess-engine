package com.kelseyde.calvin.transposition;

import com.kelseyde.calvin.board.Move;
import lombok.AllArgsConstructor;

/**
 * Entry in the {@link TranspositionTable}. Contains a short key and a 64-bit value which encodes the relevant
 * information about the position, along with an int metadata that contains generation and static evaluation.
 * </p>
 *
 * Key encoding:
 * 0-15: 16 bits representing part of the zobrist hash. Used to verify that the position truly matches.
 *
 * Metadata encoding:
 * 0-15: 16 bits representing the generation of the entry, i.e., how old it is. Used to gradually replace old entries.
 * 16-31: 16 bits representing the static eval of the position. Re-used to save calling the evaluation function again.
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

    private static final long SCORE_MASK = 0xffffffff00000000L;
    private static final long MOVE_MASK = 0x00000000ffff0000L;
    private static final long FLAG_MASK = 0x000000000000f000L;
    private static final long DEPTH_MASK = 0x0000000000000fffL;

    private short key;  // 16 bits representing part of the zobrist key
    private long value; // 64 bits representing the value encoding
    private int metadata; // 32 bits representing generation (0-15) and static evaluation (16-31)

    /**
     * Extracts the 16-bits representing the zobrist part of the given zobrist key.
     */
    public static short zobristPart(long zobrist) {
        return (short) (zobrist & 0xFFFF);
    }

    /**
     * Returns the 16-bits representing the zobrist part of the hash entry key.
     */
    public short getZobristPart() {
        return key;
    }

    /**
     * Gets the generation part of this entry's metadata.
     */
    public int getGeneration() {
        return (metadata & 0xFFFF);
    }

    /**
     * Sets the generation part of this entry's metadata.
     */
    public void setGeneration(int generation) {
        metadata = (metadata & 0xFFFF0000) | (generation & 0xFFFF);
    }

    /**
     * Gets the static eval part of this entry's metadata.
     */
    public int getStaticEval() {
        return (short) (metadata >>> 16); // Cast to short to interpret as signed 16-bit integer
    }

    /**
     * Sets the static eval part of this entry's metadata.
     */
    public void setStaticEval(int staticEval) {
        metadata = (metadata & 0x0000FFFF) | (staticEval << 16);
    }

    /**
     * Gets the score from this entry's value.
     */
    public int getScore() {
        long score = (value & SCORE_MASK) >>> 32;
        return (int) score;
    }

    /**
     * Sets the score in this entry's value.
     */
    public void setScore(int score) {
        value = (value & ~SCORE_MASK) | ((long) score << 32);
    }

    /**
     * Creates a new {@link HashEntry} with the adjusted score.
     */
    public HashEntry withAdjustedScore(int score) {
        long newValue = (value & ~SCORE_MASK) | ((long) score << 32);
        return new HashEntry(key, newValue, metadata);
    }

    /**
     * Sets the move in this entry's value.
     */
    public void setMove(Move move) {
        value = (value & ~MOVE_MASK) | ((long) move.value() << 16);
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
        // Extract the 16-bit zobrist part from the Zobrist key
        short key = zobristPart(zobristKey);
        // Get the 16-bit encoded move
        long moveValue = move != null ? move.value() : 0;
        // Get the 3-bit encoded flag
        long flagValue = HashFlag.value(flag);
        // Combine the score, move, flag, and depth to create the hash entry value
        long value = ((long) score << 32) | (moveValue << 16) | (flagValue << 12) | depth;
        // Combine generation and staticEval into metadata
        int metadata = (staticEval << 16) | (generation & 0xFFFF);
        return new HashEntry(key, value, metadata);
    }

}