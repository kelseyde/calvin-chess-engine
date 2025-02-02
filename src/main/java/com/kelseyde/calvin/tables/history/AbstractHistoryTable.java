package com.kelseyde.calvin.tables.history;

public abstract class AbstractHistoryTable {

    private final short bonusMax;
    private final short bonusScale;
    private final short bonusOffset;
    private final short malusMax;
    private final short malusScale;
    private final short malusOffset;
    private final short scoreMax;

    public AbstractHistoryTable(short bonusMax, short bonusScale, short bonusOffset,
                                short malusMax, short malusScale, short malusOffset,
                                short scoreMax) {
        this.bonusMax = bonusMax;
        this.bonusScale = bonusScale;
        this.bonusOffset = bonusOffset;
        this.malusMax = malusMax;
        this.malusScale = malusScale;
        this.malusOffset = malusOffset;
        this.scoreMax = scoreMax;
    }

    protected short bonus(int depth) {
        return clamp((short) (bonusScale * depth - bonusOffset), bonusMax);
    }

    protected short malus(int depth) {
        return (short) -clamp((short) (malusScale * depth - malusOffset), malusMax);
    }

    protected short gravity(short current, short update) {
        return (short) (current + update - current * Math.abs(update) / scoreMax);
    }

    protected short clamp(short value, short max) {
        return (short) Math.min(Math.max(value, 0), max);
    }

}
