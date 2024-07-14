package com.kelseyde.calvin.evaluation;

public class Score {

    public static final int MATE_SCORE = 1000000;
    public static final int DRAW_SCORE = 0;

    public static boolean isMateScore(int eval) {
        return Math.abs(eval) >= Score.MATE_SCORE - 100;
    }

}
