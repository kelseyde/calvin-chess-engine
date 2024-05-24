package com.kelseyde.calvin.transposition.pawn;

public record PawnHashEntry(long key,
                            long whiteScore,
                            long blackScore) {

    private static final long MG_SCORE_MASK = 0x00000000ffffffffL;
    private static final long EG_SCORE_MASK = 0xffffffff00000000L;

    public static int mgScore(long pawnScore) {
        return (int) (pawnScore & MG_SCORE_MASK);
    }

    public static int egScore(long pawnScore) {
        return (int) ((pawnScore & EG_SCORE_MASK) >>> 32);
    }

    public static long encode(int mgScore, int egScore) {
        return (long) mgScore | ((long) egScore) << 32;
    }

}
