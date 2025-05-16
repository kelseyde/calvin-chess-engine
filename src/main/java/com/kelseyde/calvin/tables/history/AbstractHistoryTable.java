package com.kelseyde.calvin.tables.history;

public abstract class AbstractHistoryTable {

    private final short bonusMax;
    private final short bonusScale;
    private final short malusMax;
    private final short malusScale;
    private final short moveCountMult;
    private final short scoreMax;

    public AbstractHistoryTable(short bonusMax, short bonusScale, short malusMax, short malusScale, short moveCountMult, short scoreMax) {
        this.bonusMax = bonusMax;
        this.bonusScale = bonusScale;
        this.malusMax = malusMax;
        this.malusScale = malusScale;
        this.moveCountMult = moveCountMult;
        this.scoreMax = scoreMax;
    }

    protected short bonus(int depth, int moveCount) {
        return (short) (Math.min(bonusScale * depth, bonusMax) - moveCountMult * (moveCount - 1));
    }

    protected short malus(int depth, int moveCount) {
        return (short) (-Math.min(malusScale * depth, malusMax) - moveCountMult * (moveCount - 1));
    }

    protected short gravity(short current, short update) {
        return (short) (current + update - current * Math.abs(update) / scoreMax);
    }

}
