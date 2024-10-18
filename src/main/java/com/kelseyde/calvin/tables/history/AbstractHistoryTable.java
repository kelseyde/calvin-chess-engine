package com.kelseyde.calvin.tables.history;

public abstract class AbstractHistoryTable {

    private static final int MAX_BETA_DELTA = 400;
    private static final int MIN_BETA_DELTA = -MAX_BETA_DELTA;
    private static final int MAX_SCORE_SCALAR = 1000;
    private static final int MIN_SCORE_SCALAR = 0;

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
        return Math.min(16 * depth * depth + 32 * depth + 16, bonusMax);
    }

    protected int malus(int depth) {
        return -Math.min(malusScale * depth, malusMax);
    }

    public int scaledBonus(int depth, int beta, int score) {
        final boolean good = score >= beta;
        final int delta = Math.abs(Math.max(MIN_BETA_DELTA, Math.min(score - beta, MAX_BETA_DELTA)));
        final int scoreFactor = MIN_SCORE_SCALAR + (delta * (MAX_SCORE_SCALAR - MIN_SCORE_SCALAR)) / MAX_BETA_DELTA;
        final int depthFactor = depth * 1000;
        final int scale = good ? bonusScale : malusScale;
        final int max = good ? bonusMax : malusMax;
        final int factor = (depthFactor + scoreFactor) / 2;
        final int bonus = Math.min(factor * scale / 1000, max);
        return good ? bonus : -bonus;
    }

    protected int gravity(int current, int update) {
        return current + update - current * Math.abs(update) / scoreMax;
    }

//    public static void main(String[] args) {
//
//        TestHistoryTable historyTable = new TestHistoryTable(1200, 200, 1200, 200, 8192);
//
//        System.out.println("increasing delta");
//
//        System.out.println(historyTable.scaledBonus(1, 0, 0));
//        System.out.println(historyTable.scaledBonus(1, 0, 10));
//        System.out.println(historyTable.scaledBonus(1, 0, 100));
//        System.out.println(historyTable.scaledBonus(1, 0, 200));
//        System.out.println(historyTable.scaledBonus(1, 0, 400));
//        System.out.println(historyTable.scaledBonus(1, 0, 500));
//
//        System.out.println("increasing depth");
//
//        System.out.println(historyTable.scaledBonus(1, 0, 0));
//        System.out.println(historyTable.scaledBonus(2, 0, 0));
//        System.out.println(historyTable.scaledBonus(5, 0, 0));
//        System.out.println(historyTable.scaledBonus(10, 0, 0));
//        System.out.println(historyTable.scaledBonus(20, 0, 0));
//        System.out.println(historyTable.scaledBonus(30, 0, 0));
//
//        System.out.println("increasing depth more than delta");
//
//        System.out.println(historyTable.scaledBonus(1, 0, 0));
//        System.out.println(historyTable.scaledBonus(2, 0, 10));
//        System.out.println(historyTable.scaledBonus(5, 0, 100));
//        System.out.println(historyTable.scaledBonus(10, 0, 200));
//        System.out.println(historyTable.scaledBonus(20, 0, 400));
//
//        System.out.println("negative delta");
//
//        System.out.println(historyTable.scaledBonus(1, 0, 0));
//        System.out.println(historyTable.scaledBonus(1, 0, -10));
//        System.out.println(historyTable.scaledBonus(1, 100, 0));
//        System.out.println(historyTable.scaledBonus(1, 0, -200));
//        System.out.println(historyTable.scaledBonus(1, 0, -400));
//        System.out.println(historyTable.scaledBonus(1, 0, -500));
//
//    }
//
//    static class TestHistoryTable extends AbstractHistoryTable {
//        public TestHistoryTable(int bonusMax, int bonusScale, int malusMax, int malusScale, int scoreMax) {
//            super(bonusMax, bonusScale, malusMax, malusScale, scoreMax);
//        }
//    }

}
