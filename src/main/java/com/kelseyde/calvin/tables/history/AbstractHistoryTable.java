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
        short base = bonus.base;
        short scale = bonus.scale;
        short moveMult = bonus.moveMult;
        short max = bonus.max;
        return (short) Math.min(base + depth * scale + (moveCount - 1) * moveMult, max);
    }

    public short malus(int depth, int moveCount) {
        short base = malus.base;
        short scale = malus.scale;
        short moveMult = malus.moveMult;
        short max = malus.max;
        return (short) -Math.min(base + depth * scale - (moveCount - 1) * moveMult, max);
    }

    protected short gravity(short current, short update) {
        return (short) (current + update - current * Math.abs(update) / scoreMax);
    }

}
