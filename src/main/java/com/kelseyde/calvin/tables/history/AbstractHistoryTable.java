package com.kelseyde.calvin.tables.history;

public abstract class AbstractHistoryTable {

    public record HistoryBonus(short base, short scale, short moveMult, short max) {

        public HistoryBonus(int base, int scale, int moveMult, int max) {
            this((short) base, (short) scale, (short) moveMult, (short) max);
        }

        public short bonus(int depth, int moveCount) {
            return (short) Math.min(base + depth * scale - (moveCount - 1) * moveMult, max);
        }

    }

    private final short scoreMax;

    public final HistoryBonus bonus;
    public final HistoryBonus malus;

    public AbstractHistoryTable(HistoryBonus bonus,
                                HistoryBonus malus,
                                short scoreMax) {
        this.bonus = bonus;
        this.malus = malus;
        this.scoreMax = scoreMax;
    }

    public short bonus(int depth, int moveCount) {
        return bonus.bonus(depth, moveCount);
    }

    public short malus(int depth, int moveCount) {
        return (short) -malus.bonus(depth, moveCount);
    }

    protected short gravity(short current, short update) {
        return (short) (current + update - current * Math.abs(update) / scoreMax);
    }

}
