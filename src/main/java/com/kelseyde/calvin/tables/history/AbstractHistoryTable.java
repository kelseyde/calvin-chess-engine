package com.kelseyde.calvin.tables.history;

public abstract class AbstractHistoryTable {

    private final short scoreMax;

    public AbstractHistoryTable(short scoreMax) {
        this.scoreMax = scoreMax;
    }

    protected short gravity(short current, short update) {
        return (short) (current + update - current * Math.abs(update) / scoreMax);
    }

}
