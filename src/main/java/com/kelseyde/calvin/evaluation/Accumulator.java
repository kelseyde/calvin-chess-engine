package com.kelseyde.calvin.evaluation;

import java.util.Arrays;

public class Accumulator {

    /**
     * Two feature vectors, one from white's perspective, one from black's.
     */
    public final short[] whiteFeatures;
    public final short[] blackFeatures;

    public Accumulator(int featureCount) {
        this.whiteFeatures = new short[featureCount];
        this.blackFeatures = new short[featureCount];
    }

    public Accumulator(short[] whiteFeatures, short[] blackFeatures) {
        this.whiteFeatures = whiteFeatures;
        this.blackFeatures = blackFeatures;
    }

    public void add(int wx1, int bx1) {
        short[] weights = NNUE.Network.NETWORK.inputWeights();
        int hiddenSize = NNUE.Network.HIDDEN_SIZE;

        for (int i = 0; i < whiteFeatures.length; i++) {
            whiteFeatures[i] += weights[i + wx1 * hiddenSize];
            blackFeatures[i] += weights[i + bx1 * hiddenSize];
        }
    }

    public void addSub(int wx1, int bx1, int wx2, int bx2) {
        short[] weights = NNUE.Network.NETWORK.inputWeights();
        int hiddenSize = NNUE.Network.HIDDEN_SIZE;

        for (int i = 0; i < whiteFeatures.length; i++) {
            whiteFeatures[i] += (short) (weights[i + wx1 * hiddenSize] - weights[i + wx2 * hiddenSize]);
            blackFeatures[i] += (short) (weights[i + bx1 * hiddenSize] - weights[i + bx2 * hiddenSize]);
        }
    }

    public void addSubSub(int wx1, int bx1, int wx2, int bx2, int wx3, int bx3) {
        short[] weights = NNUE.Network.NETWORK.inputWeights();
        int hiddenSize = NNUE.Network.HIDDEN_SIZE;

        for (int i = 0; i < whiteFeatures.length; i++) {
            whiteFeatures[i] += (short) (weights[i + wx1 * hiddenSize] - weights[i + wx2 * hiddenSize] - weights[i + wx3 * hiddenSize]);
            blackFeatures[i] += (short) (weights[i + bx1 * hiddenSize] - weights[i + bx2 * hiddenSize] - weights[i + bx3 * hiddenSize]);
        }
    }

    public void addAddSubSub(int wx1, int bx1, int wx2, int bx2, int wx3, int bx3, int wx4, int bx4) {
        short[] weights = NNUE.Network.NETWORK.inputWeights();
        int hiddenSize = NNUE.Network.HIDDEN_SIZE;

        for (int i = 0; i < whiteFeatures.length; i++) {
            whiteFeatures[i] += (short) (weights[i + wx1 * hiddenSize] + weights[i + wx2 * hiddenSize] - weights[i + wx3 * hiddenSize] - weights[i + wx4 * hiddenSize]);
            blackFeatures[i] += (short) (weights[i + bx1 * hiddenSize] + weights[i + bx2 * hiddenSize] - weights[i + bx3 * hiddenSize] - weights[i + bx4 * hiddenSize]);
        }
    }

    public Accumulator copy() {
        return new Accumulator(
                Arrays.copyOf(whiteFeatures, whiteFeatures.length),
                Arrays.copyOf(blackFeatures, blackFeatures.length));
    }

}