package com.kelseyde.calvin.tables.history;

public abstract class AbstractHistoryTable {

    private static final int MAX_BETA_DELTA = 400;
    private static final int MIN_BETA_DELTA = -MAX_BETA_DELTA;

    private static final int MAX_DEPTH_RANGE = 30;
    private static final int MIN_DEPTH_RANGE = 1;

    private static final int MIN_DEPTH_SCALAR = 0;
    private static final int MAX_DEPTH_SCALAR = 1000;

    private static final int MIN_SCORE_SCALAR = 0;
    private static final int MAX_SCORE_SCALAR = 1000;

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
        boolean good = score >= beta;
        int scale = good ? bonusScale : malusScale;
        int max = good ? bonusMax : malusMax;
        int delta = Math.abs(Math.max(MIN_BETA_DELTA, Math.min(score - beta, MAX_BETA_DELTA)));
        int clampedDepth = Math.max(MIN_DEPTH_SCALAR, Math.min(depth, MAX_DEPTH_SCALAR));

        int scoreFactor = MIN_SCORE_SCALAR + (delta * (MAX_SCORE_SCALAR - MIN_SCORE_SCALAR)) / MAX_BETA_DELTA;
        int depthFactor = MIN_DEPTH_RANGE + (clampedDepth * (MAX_DEPTH_SCALAR - MIN_DEPTH_SCALAR)) / MAX_DEPTH_RANGE;

        int depthBonus = Math.min(depthFactor, max / 2);
        int scoreBonus = Math.min(scoreFactor, max / 2);
        int bonus = depthBonus + scoreBonus;
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
//        System.out.println("increasing delta already high depth");
//
//        System.out.println(historyTable.scaledBonus(60, 0, 0));
//        System.out.println(historyTable.scaledBonus(60, 0, 10));
//        System.out.println(historyTable.scaledBonus(60, 0, 100));
//        System.out.println(historyTable.scaledBonus(60, 0, 200));
//        System.out.println(historyTable.scaledBonus(60, 0, 400));
//        System.out.println(historyTable.scaledBonus(60, 0, 500));
//
//        System.out.println("max depth and delta");
//
//        System.out.println(historyTable.scaledBonus(6000, 0, 0));
//        System.out.println(historyTable.scaledBonus(0, 0, 1000));
//
//    }
//
//    static class TestHistoryTable extends AbstractHistoryTable {
//        public TestHistoryTable(int bonusMax, int bonusScale, int malusMax, int malusScale, int scoreMax) {
//            super(bonusMax, bonusScale, malusMax, malusScale, scoreMax);
//        }
//    }

}
