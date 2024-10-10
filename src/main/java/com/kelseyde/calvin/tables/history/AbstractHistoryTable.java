package com.kelseyde.calvin.tables.history;

public abstract class AbstractHistoryTable {

    private final int bonus;
    private final int malus;
    private final int max;

    public AbstractHistoryTable(int bonus, int malus, int max) {
        this.bonus = bonus;
        this.malus = malus;
        this.max = max;
    }

    protected int bonus(int depth) {
        return Math.min(16 * depth * depth + 32 * depth + 16, bonus);
    }

    protected int malus(int depth) {
        return -Math.min(16 * depth * depth + 32 * depth + 16, malus);
    }

    protected int gravity(int current, int update) {
        return current + update - current * Math.abs(update) / max;
    }

}
