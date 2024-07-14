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

    public void add(int wx1, int bx1) {
        short[] weights = Network.DEFAULT.l0weights;
        int length = whiteFeatures.length;

        for (int i = 0; i < length; i++) {
            whiteFeatures[i] += weights[i * Network.L0_SIZE + wx1];
            blackFeatures[i] += weights[i * Network.L0_SIZE + bx1];
        }
    }

    public void sub(int wx1, int bx1) {
        short[] weights = Network.DEFAULT.l0weights;
        int length = whiteFeatures.length;

        for (int i = 0; i < length; i++) {
            whiteFeatures[i] -= weights[i * Network.L0_SIZE + wx1];
            blackFeatures[i] -= weights[i * Network.L0_SIZE + bx1];
        }
    }

    public void addSub(int wx1, int bx1, int wx2, int bx2) {
        short[] weights = Network.DEFAULT.l0weights;
        int length = whiteFeatures.length;

        for (int i = 0; i < length; i++) {
            whiteFeatures[i] += weights[i * Network.L0_SIZE + wx1];
            whiteFeatures[i] -= weights[i * Network.L0_SIZE + wx2];
            blackFeatures[i] += weights[i * Network.L0_SIZE + bx1];
            blackFeatures[i] -= weights[i * Network.L0_SIZE + bx2];
        }
    }

    public void addSubSub(int wx1, int bx1, int wx2, int bx2, int wx3, int bx3) {
        short[] weights = Network.DEFAULT.l0weights;
        int length = whiteFeatures.length;

        for (int i = 0; i < length; i++) {
            whiteFeatures[i] += weights[i * Network.L0_SIZE + wx1];
            whiteFeatures[i] -= weights[i * Network.L0_SIZE + wx2];
            whiteFeatures[i] -= weights[i * Network.L0_SIZE + wx3];
            blackFeatures[i] += weights[i * Network.L0_SIZE + bx1];
            blackFeatures[i] -= weights[i * Network.L0_SIZE + bx2];
            blackFeatures[i] -= weights[i * Network.L0_SIZE + bx3];
        }
    }

    public void addAddSubSub(int wx1, int bx1, int wx2, int bx2, int wx3, int bx3, int wx4, int bx4) {
        short[] weights = Network.DEFAULT.l0weights;
        int length = whiteFeatures.length;

        for (int i = 0; i < length; i++) {
            whiteFeatures[i] += weights[i * Network.L0_SIZE + wx1];
            whiteFeatures[i] += weights[i * Network.L0_SIZE + wx2];
            whiteFeatures[i] -= weights[i * Network.L0_SIZE + wx3];
            whiteFeatures[i] -= weights[i * Network.L0_SIZE + wx4];
            blackFeatures[i] += weights[i * Network.L0_SIZE + bx1];
            blackFeatures[i] += weights[i * Network.L0_SIZE + bx2];
            blackFeatures[i] -= weights[i * Network.L0_SIZE + bx3];
            blackFeatures[i] -= weights[i * Network.L0_SIZE + bx4];
        }
    }

    public Accumulator copy() {
        return new Accumulator(
                Arrays.copyOf(whiteFeatures, whiteFeatures.length),
                Arrays.copyOf(blackFeatures, blackFeatures.length));
    }
}