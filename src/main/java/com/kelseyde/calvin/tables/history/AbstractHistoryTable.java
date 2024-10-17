package com.kelseyde.calvin.tables.history;

public abstract class AbstractHistoryTable {

    private final int bonusMax;
    private final int bonusScale;
    private final int malusMax;
    private final int malusScale;
    private final int scoreMax;

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
