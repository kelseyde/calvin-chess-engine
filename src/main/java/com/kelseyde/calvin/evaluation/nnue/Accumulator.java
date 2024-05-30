package com.kelseyde.calvin.evaluation.nnue;

import java.util.Arrays;

public class Accumulator {

    /**
     * Two feature vectors, one from white's perspective, one from black's.
     */
    public short[] whiteFeatures;
    public short[] blackFeatures;

    public Accumulator(int featuresCount) {
        this.whiteFeatures = new short[featuresCount];
        this.blackFeatures = new short[featuresCount];
    }

    public Accumulator(short[] whiteFeatures, short[] blackFeatures) {
        this.whiteFeatures = whiteFeatures;
        this.blackFeatures = blackFeatures;
    }

    public Accumulator copy() {
        return new Accumulator(
                Arrays.copyOf(whiteFeatures, whiteFeatures.length),
                Arrays.copyOf(blackFeatures, blackFeatures.length));
    }

}
