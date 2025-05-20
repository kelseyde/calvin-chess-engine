package com.kelseyde.calvin.tables.score;

import com.kelseyde.calvin.engine.EngineConfig;

/**
 * Keep track of the stability of the score returned from each iteration of the iterative deepening loop.
 * If the score has remained stable for many iterations, we can assume that the position is calm and non-tactical,
 * and (hopefully) use this information during search.
 */
public class ScoreStabilityTracker {

    private int avgRootScoreDelta;
    private boolean firstUpdate;

    public ScoreStabilityTracker() {
        firstUpdate = true;
    }

    public void updateAverageRootScoreDelta(int scorePrevious, int scoreCurrent) {
        final int numerator = 3;
        final int denominator = 10;
        final int scaleFactor = 100;
        int scoreDelta = Math.abs(scoreCurrent - scorePrevious);
        if (firstUpdate) {
            firstUpdate = false;
            avgRootScoreDelta = scoreDelta * scaleFactor;
        } else {
            // Update exponential moving average
            avgRootScoreDelta = (numerator * scoreDelta * scaleFactor + (denominator - numerator) * avgRootScoreDelta) / denominator;
        }
    }

    public int getAvgRootScoreDelta() {
        return avgRootScoreDelta;
    }

    public void reset() {
        avgRootScoreDelta = 0;
        firstUpdate = true;
    }

}
