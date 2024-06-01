package com.kelseyde.calvin.transposition.pawn;

/**
 * Represents an entry in the pawn hash table, storing a key representing the pawn structure, and a value representing
 * the evaluation.
 * </p>
 * The evaluation value is split into four parts. The upper 32 bits are the score for white; the lower 32 bits are the
 * score for black. Within each score, the upper 16 bits are the endgame score, and the lower 16 bits are the middlegame
 * score.
 */
public record PawnHashEntry(long key, long value) {

    // A rough estimate: 2 8-byte fields and some overhead for object headers
    public static final int SIZE_BYTES = 24;

    private static final long WHITE_SCORE_MASK = 0xffffffff00000000L;
    private static final long BLACK_SCORE_MASK = 0x00000000ffffffffL;
    private static final int LOWER_HALF_SCORE_MASK = 0xFFFF;

    /**
     * Creates a new PawnHashEntry with the given pawn key, white score, and black score.
     *
     * @param pawnKey the key representing the pawn structure.
     * @param whiteScore the white score.
     * @param blackScore the black score.
     * @return a new PawnHashEntry.
     */
    public static PawnHashEntry of(long pawnKey, int whiteScore, int blackScore) {
        long value = ((long) whiteScore << 32) | ((long) blackScore & BLACK_SCORE_MASK);
        return new PawnHashEntry(pawnKey, value);
    }

    /**
     * Extracts the middle-game score from the encoded integer.
     *
     * @param encodedScore the encoded score containing middlegame and endgame scores.
     * @return the middlegame score.
     */
    public static int mgScore(int encodedScore) {
        return (short) (encodedScore & LOWER_HALF_SCORE_MASK);
    }

    /**
     * Extracts the end-game score from the encoded integer.
     *
     * @param encodedScore the encoded score containing middlegame and endgame scores.
     * @return the endgame score.
     */
    public static int egScore(int encodedScore) {
        return (short) (encodedScore >> 16);
    }

    /**
     * Encodes middle-game and end-game scores into a single integer.
     *
     * @param mgScore the middle-game score.
     * @param egScore the end-game score.
     * @return the combined score as an integer.
     */
    public static int encode(int mgScore, int egScore) {
        return (mgScore & LOWER_HALF_SCORE_MASK) | (egScore << 16);
    }

    /**
     * Retrieves the white score from the value, middlegame and endgame combined.
     */
    public int whiteScore() {
        return (int) ((value & WHITE_SCORE_MASK) >>> 32);
    }

    /**
     * Retrieves the black score from the value, middlegame and endgame combined.
     */
    public int blackScore() {
        return (int) (value & BLACK_SCORE_MASK);
    }

}
