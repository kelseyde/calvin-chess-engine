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

    protected short bonus(int depth) {
        return (short) Math.min(bonusScale * depth, bonusMax);
    }

    protected short malus(int depth) {
        return (short) -Math.min(malusScale * depth, malusMax);
    }

    protected short gravity(short current, short update) {
        return (short) (current + update - current * Math.abs(update) / scoreMax);
    }

}
