package com.kelseyde.calvin.tables.history;

public abstract class AbstractHistoryTable {

    private final short bonusMax;
    private final short bonusScale;
    private final short malusMax;
    private final short malusScale;
    private final short scoreMax;

    public AbstractHistoryTable(short bonusMax, short bonusScale, short malusMax, short malusScale, short scoreMax) {
        this.bonusMax = bonusMax;
        this.bonusScale = bonusScale;
        this.malusMax = malusMax;
        this.malusScale = malusScale;
        this.scoreMax = scoreMax;
    }

    protected short bonus(int depth, int scoreDiff) {
        final short depthBonus = (short) (depth * bonusScale);
        final short scoreDiffBonus = (short) (scoreDiff * 16);
        return (short) Math.min(depthBonus + scoreDiffBonus, bonusMax);
    }

    protected short malus(int depth, int scoreDiff) {
        final short depthMalus = (short) (depth * malusScale);
        final short scoreDiffMalus = (short) (scoreDiff * 16);
        return (short) -Math.min(depthMalus + scoreDiffMalus, malusMax);
    }

    protected short gravity(short current, short update) {
        return (short) (current + update - current * Math.abs(update) / scoreMax);
    }

    public static int ilog2(int value) {
        return value <= 0 ? 0 : Integer.SIZE - Integer.numberOfLeadingZeros(value) - 1;
    }

}
