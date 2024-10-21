package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits;

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

    protected int threatIndex(int from, int to, long threats) {
        int fromThreatened = Bits.contains(threats, from) ? 1 : 0;
        int toThreatened = Bits.contains(threats, to) ? 1 : 0;
        return fromThreatened << 1 | toThreatened;
    }

}
