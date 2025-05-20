package com.kelseyde.calvin.tables.score;

import com.kelseyde.calvin.engine.EngineConfig;

public class ScoreStabilityTracker {

    private static final int ALPHA_NUMERATOR = 3;
    private static final int ALPHA_DENOMINATOR = 10;
    private static final int SCALE_FACTOR = 100;

    private int stableIterations;
    private int avgRootScoreDelta;
    private boolean firstUpdate;

    public ScoreStabilityTracker(EngineConfig config) {
        stableIterations = 0;
        firstUpdate = true;
    }

    public int getStableIterations() {
        return stableIterations;
    }

    public int getAvgRootScoreDelta() {
        return avgRootScoreDelta;
    }

    public void updateBestScoreStability(int scorePrevious, int scoreCurrent) {
        stableIterations = updateStableIterations(scorePrevious, scoreCurrent);
        avgRootScoreDelta = updateAverageRootScoreDelta(scorePrevious, scoreCurrent);
    }

    private int updateStableIterations(int scorePrevious, int scoreCurrent) {
        boolean stable = scoreCurrent >= scorePrevious - 10 && scoreCurrent <= scorePrevious + 10;
        return stable ? stableIterations + 1 : 0;
    }

    private int updateAverageRootScoreDelta(int scorePrevious, int scoreCurrent) {
        int scoreDelta = Math.abs(scoreCurrent - scorePrevious);
        if (firstUpdate) {
            firstUpdate = false;
            return scoreDelta * SCALE_FACTOR;
        } else {
            // Update exponential moving average
            return (ALPHA_NUMERATOR * scoreDelta * SCALE_FACTOR +
                    (ALPHA_DENOMINATOR - ALPHA_NUMERATOR) * avgRootScoreDelta) /
                    ALPHA_DENOMINATOR;
        }
    }

    public void reset() {
        stableIterations = 0;
        avgRootScoreDelta = 0;
        firstUpdate = true;
    }

}
