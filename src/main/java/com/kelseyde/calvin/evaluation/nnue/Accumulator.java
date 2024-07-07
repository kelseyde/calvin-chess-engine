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
        short[] weights = Network.DEFAULT.inputWeights;
        for (int i = 0; i < Network.HIDDEN_LAYER_SIZE; i++) {
            whiteFeatures[i] += weights[i * Network.INPUT_LAYER_SIZE + wx1];
            blackFeatures[i] += weights[i * Network.INPUT_LAYER_SIZE + bx1];
        }
    }


    public void sub(int wx1, int bx1) {
        short[] weights = Network.DEFAULT.inputWeights;
        for (int i = 0; i < Network.HIDDEN_LAYER_SIZE; i++) {
            whiteFeatures[i] -= weights[i * Network.INPUT_LAYER_SIZE + wx1];
            blackFeatures[i] -= weights[i * Network.INPUT_LAYER_SIZE + bx1];
        }
    }

    public void addSub(int wx1, int bx1, int wx2, int bx2) {
        short[] weights = Network.DEFAULT.inputWeights;
        for (int i = 0; i < Network.HIDDEN_LAYER_SIZE; i++) {
            int offset = i * Network.INPUT_LAYER_SIZE;
            whiteFeatures[i] += (short) (weights[offset + wx1] - weights[offset + wx2]);
            blackFeatures[i] += (short) (weights[offset + bx1] - weights[offset + bx2]);
        }
    }

    public void addAddSub(int wx1, int bx1, int wx2, int bx2, int wx3, int bx3) {
        short[] weights = Network.DEFAULT.inputWeights;
        for (int i = 0; i < Network.HIDDEN_LAYER_SIZE; i++) {
            int offset = i * Network.INPUT_LAYER_SIZE;
            whiteFeatures[i] += (short) (weights[offset + wx1] + weights[offset + wx2] - weights[offset + wx3]);
            blackFeatures[i] += (short) (weights[offset + bx1] + weights[offset + bx2] - weights[offset + bx3]);
        }
    }

    public void addSubSub(int wx1, int bx1, int wx2, int bx2, int wx3, int bx3) {
        short[] weights = Network.DEFAULT.inputWeights;
        for (int i = 0; i < Network.HIDDEN_LAYER_SIZE; i++) {
            int offset = i * Network.INPUT_LAYER_SIZE;
            whiteFeatures[i] += (short) (weights[offset + wx1] - weights[offset + wx2] - weights[offset + wx3]);
            blackFeatures[i] += (short) (weights[offset + bx1] - weights[offset + bx2] - weights[offset + bx3]);
        }
    }
    
    public void addAddSubSub(int wx1, int bx1, int wx2, int bx2, int wx3, int bx3, int wx4, int bx4) {
        short[] weights = Network.DEFAULT.inputWeights;
        for (int i = 0; i < Network.HIDDEN_LAYER_SIZE; i++) {
            int offset = i * Network.INPUT_LAYER_SIZE;
            whiteFeatures[i] += (short) (weights[offset + wx1] + weights[offset + wx2] - weights[offset + wx3] - weights[offset + wx4]);
            blackFeatures[i] += (short) (weights[offset + bx1] + weights[offset + bx2] - weights[offset + bx3] - weights[offset + bx4]);
        }
    }

    public Accumulator copy() {
        return new Accumulator(
                Arrays.copyOf(whiteFeatures, whiteFeatures.length),
                Arrays.copyOf(blackFeatures, blackFeatures.length));
    }

}
