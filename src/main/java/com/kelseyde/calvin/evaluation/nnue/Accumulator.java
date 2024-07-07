package com.kelseyde.calvin.evaluation.nnue;

import java.util.Arrays;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorSpecies;

public class Accumulator {

    private static final VectorSpecies<Short> SPECIES = ShortVector.SPECIES_PREFERRED;

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
        int length = whiteFeatures.length;
        int i = 0;

        for (; i < SPECIES.loopBound(length); i += SPECIES.length()) {
            ShortVector whiteVector = ShortVector.fromArray(SPECIES, whiteFeatures, i);
            ShortVector blackVector = ShortVector.fromArray(SPECIES, blackFeatures, i);

            ShortVector whiteAddVector = whiteVector.add(ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + wx1));
            ShortVector blackAddVector = blackVector.add(ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + bx1));

            whiteAddVector.intoArray(whiteFeatures, i);
            blackAddVector.intoArray(blackFeatures, i);
        }

        // Process any remaining elements
        for (; i < length; i++) {
            int offset = i * Network.INPUT_LAYER_SIZE;
            whiteFeatures[i] += weights[offset + wx1];
            blackFeatures[i] += weights[offset + bx1];
        }
    }

    public void sub(int wx1, int bx1) {
        short[] weights = Network.DEFAULT.inputWeights;
        int length = whiteFeatures.length;
        int i = 0;

        for (; i < SPECIES.loopBound(length); i += SPECIES.length()) {
            ShortVector whiteVector = ShortVector.fromArray(SPECIES, whiteFeatures, i);
            ShortVector blackVector = ShortVector.fromArray(SPECIES, blackFeatures, i);

            ShortVector whiteSubVector = whiteVector.sub(ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + wx1));
            ShortVector blackSubVector = blackVector.sub(ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + bx1));

            whiteSubVector.intoArray(whiteFeatures, i);
            blackSubVector.intoArray(blackFeatures, i);
        }

        // Process any remaining elements
        for (; i < length; i++) {
            int offset = i * Network.INPUT_LAYER_SIZE;
            whiteFeatures[i] -= weights[offset + wx1];
            blackFeatures[i] -= weights[offset + bx1];
        }
    }

    public void addSub(int wx1, int bx1, int wx2, int bx2) {
        short[] weights = Network.DEFAULT.inputWeights;
        int length = whiteFeatures.length;
        int i = 0;

        for (; i < SPECIES.loopBound(length); i += SPECIES.length()) {
            ShortVector whiteVector = ShortVector.fromArray(SPECIES, whiteFeatures, i);
            ShortVector blackVector = ShortVector.fromArray(SPECIES, blackFeatures, i);

            ShortVector whiteAddSubVector = whiteVector
                    .add(ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + wx1))
                    .sub(ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + wx2));

            ShortVector blackAddSubVector = blackVector
                    .add(ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + bx1))
                    .sub(ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + bx2));

            whiteAddSubVector.intoArray(whiteFeatures, i);
            blackAddSubVector.intoArray(blackFeatures, i);
        }

        // Process any remaining elements
        for (; i < length; i++) {
            int offset = i * Network.INPUT_LAYER_SIZE;
            whiteFeatures[i] += (short) (weights[offset + wx1] - weights[offset + wx2]);
            blackFeatures[i] += (short) (weights[offset + bx1] - weights[offset + bx2]);
        }
    }

    public void addSubSub(int wx1, int bx1, int wx2, int bx2, int wx3, int bx3) {
        short[] weights = Network.DEFAULT.inputWeights;
        int length = whiteFeatures.length;
        int i = 0;

        for (; i < SPECIES.loopBound(length); i += SPECIES.length()) {
            ShortVector whiteVector = ShortVector.fromArray(SPECIES, whiteFeatures, i);
            ShortVector blackVector = ShortVector.fromArray(SPECIES, blackFeatures, i);

            ShortVector wx1Vector = ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + wx1);
            ShortVector wx2Vector = ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + wx2);
            ShortVector wx3Vector = ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + wx3);

            ShortVector bx1Vector = ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + bx1);
            ShortVector bx2Vector = ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + bx2);
            ShortVector bx3Vector = ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + bx3);

            ShortVector whiteResultVector = whiteVector.add(wx1Vector).sub(wx2Vector).sub(wx3Vector);
            ShortVector blackResultVector = blackVector.add(bx1Vector).sub(bx2Vector).sub(bx3Vector);

            whiteResultVector.intoArray(whiteFeatures, i);
            blackResultVector.intoArray(blackFeatures, i);
        }

        // Process any remaining elements
        for (; i < length; i++) {
            int offset = i * Network.INPUT_LAYER_SIZE;
            whiteFeatures[i] += (short) (weights[offset + wx1] - weights[offset + wx2] - weights[offset + wx3]);
            blackFeatures[i] += (short) (weights[offset + bx1] - weights[offset + bx2] - weights[offset + bx3]);
        }
    }

    public void addAddSubSub(int wx1, int bx1, int wx2, int bx2, int wx3, int bx3, int wx4, int bx4) {
        short[] weights = Network.DEFAULT.inputWeights;
        int length = whiteFeatures.length;
        int i = 0;

        for (; i < SPECIES.loopBound(length); i += SPECIES.length()) {
            ShortVector whiteVector = ShortVector.fromArray(SPECIES, whiteFeatures, i);
            ShortVector blackVector = ShortVector.fromArray(SPECIES, blackFeatures, i);

            ShortVector wx1Vector = ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + wx1);
            ShortVector wx2Vector = ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + wx2);
            ShortVector wx3Vector = ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + wx3);
            ShortVector wx4Vector = ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + wx4);

            ShortVector bx1Vector = ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + bx1);
            ShortVector bx2Vector = ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + bx2);
            ShortVector bx3Vector = ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + bx3);
            ShortVector bx4Vector = ShortVector.fromArray(SPECIES, weights, i * Network.INPUT_LAYER_SIZE + bx4);

            ShortVector whiteResultVector = whiteVector.add(wx1Vector).add(wx2Vector).sub(wx3Vector).sub(wx4Vector);
            ShortVector blackResultVector = blackVector.add(bx1Vector).add(bx2Vector).sub(bx3Vector).sub(bx4Vector);

            whiteResultVector.intoArray(whiteFeatures, i);
            blackResultVector.intoArray(blackFeatures, i);
        }

        // Process any remaining elements
        for (; i < length; i++) {
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