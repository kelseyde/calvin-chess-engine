package com.kelseyde.calvin.tables.history;

public abstract class AbstractHistoryTable {

    public record HistoryConfig(short base, short scale, short moveMult, short max) {

        public HistoryConfig(int base, int scale, int moveMult, int max) {
            this((short) base, (short) scale, (short) moveMult, (short) max);
        }

    }

    private final short scoreMax;

    public final HistoryConfig bonus;
    public final HistoryConfig malus;

    public AbstractHistoryTable(HistoryConfig bonus,
                                HistoryConfig malus,
                                short scoreMax) {
        this.bonus = bonus;
        this.malus = malus;
        this.scoreMax = scoreMax;
    }

    public short bonus(int depth, int moveCount) {
        return (short) Math.min(bonus.base + depth * bonus.scale + (moveCount - 1) * bonus.moveMult, bonus.max);
    }

    public short malus(int depth, int moveCount) {
        return (short) -Math.min(malus.base + depth * malus.scale - (moveCount - 1) * malus.moveMult, malus.max);
    }

    protected short gravity(short current, short update) {
        return (short) (current + update - current * Math.abs(update) / scoreMax);
    }

}
