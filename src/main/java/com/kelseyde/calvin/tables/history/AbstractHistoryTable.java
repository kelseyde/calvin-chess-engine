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
        // formula from Berserk
        return (short) Math.min(1070, 4 * depth * depth + 100 * depth - 73);
    }

    protected short malus(int depth) {
        // formula from Berserk
        return (short) -Math.min(520, 3 * depth * depth + 120 * depth - 33);
    }

    protected short gravity(short current, short update) {
        return (short) (current + update - current * Math.abs(update) / scoreMax);
    }

}
