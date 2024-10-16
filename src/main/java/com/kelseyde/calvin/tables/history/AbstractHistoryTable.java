package com.kelseyde.calvin.tables.history;

public abstract class AbstractHistoryTable {

    protected final int bonusMax;
    protected final int bonusScale;
    protected final int malusMax;
    protected final int malusScale;
    protected final int scoreMax;

    public AbstractHistoryTable(int bonusMax, int bonusScale, int malusMax, int malusScale, int scoreMax) {
        this.bonusMax = bonusMax;
        this.bonusScale = bonusScale;
        this.malusMax = malusMax;
        this.malusScale = malusScale;
        this.scoreMax = scoreMax;
    }

    protected int bonus(int depth) {
        return Math.min(bonusScale * depth, bonusMax);
    }

    protected int malus(int depth) {
        return -Math.min(malusScale * depth, malusMax);
    }

    protected int gravity(int current, int update) {
        return current + update - current * Math.abs(update) / scoreMax;
    }

}
