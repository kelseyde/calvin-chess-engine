package com.kelseyde.calvin.evaluation;

import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.Arrays;

public class Accumulator {

    public static final VectorSpecies<Short> SPECIES = ShortVector.SPECIES_PREFERRED;

    /**
     * Two feature vectors, one from white's perspective, one from black's.
     */
    public short[] whiteFeatures;
    public short[] blackFeatures;

    public Accumulator(int featureCount) {
        this.whiteFeatures = new short[featureCount];
        this.blackFeatures = new short[featureCount];
    }

    public Accumulator(short[] whiteFeatures, short[] blackFeatures) {
        this.whiteFeatures = whiteFeatures;
        this.blackFeatures = blackFeatures;
    }

    public void add(int wx1, int bx1) {
        short[] weights = Network.NETWORK.inputWeights();

        for (int i = 0; i < SPECIES.loopBound(whiteFeatures.length); i += SPECIES.length()) {
            ShortVector whiteVector = ShortVector.fromArray(SPECIES, whiteFeatures, i);
            ShortVector blackVector = ShortVector.fromArray(SPECIES, blackFeatures, i);

            ShortVector whiteAddVector = whiteVector.add(ShortVector.fromArray(SPECIES, weights, i + wx1 * Network.HIDDEN_SIZE));
            ShortVector blackAddVector = blackVector.add(ShortVector.fromArray(SPECIES, weights, i + bx1 * Network.HIDDEN_SIZE));

            whiteAddVector.intoArray(whiteFeatures, i);
            blackAddVector.intoArray(blackFeatures, i);
        }
    }

    public void sub(int wx1, int bx1) {
        short[] weights = Network.NETWORK.inputWeights();

        for (int i = 0; i < SPECIES.loopBound(whiteFeatures.length); i += SPECIES.length()) {
            ShortVector whiteVector = ShortVector.fromArray(SPECIES, whiteFeatures, i);
            ShortVector blackVector = ShortVector.fromArray(SPECIES, blackFeatures, i);

            ShortVector whiteSubVector = whiteVector.sub(ShortVector.fromArray(SPECIES, weights, i + wx1 * Network.HIDDEN_SIZE));
            ShortVector blackSubVector = blackVector.sub(ShortVector.fromArray(SPECIES, weights, i + bx1 * Network.HIDDEN_SIZE));

            whiteSubVector.intoArray(whiteFeatures, i);
            blackSubVector.intoArray(blackFeatures, i);
        }
    }

    public void addSub(int wx1, int bx1, int wx2, int bx2) {
        short[] weights = Network.NETWORK.inputWeights();

        for (int i = 0; i < SPECIES.loopBound(whiteFeatures.length); i += SPECIES.length()) {
            ShortVector whiteVector = ShortVector.fromArray(SPECIES, whiteFeatures, i);
            ShortVector blackVector = ShortVector.fromArray(SPECIES, blackFeatures, i);

            ShortVector whiteAddSubVector = whiteVector
                    .add(ShortVector.fromArray(SPECIES, weights, i + wx1 * Network.HIDDEN_SIZE))
                    .sub(ShortVector.fromArray(SPECIES, weights, i + wx2 * Network.HIDDEN_SIZE));

            ShortVector blackAddSubVector = blackVector
                    .add(ShortVector.fromArray(SPECIES, weights, i + bx1 * Network.HIDDEN_SIZE))
                    .sub(ShortVector.fromArray(SPECIES, weights, i + bx2 * Network.HIDDEN_SIZE));

            whiteAddSubVector.intoArray(whiteFeatures, i);
            blackAddSubVector.intoArray(blackFeatures, i);
        }
    }

    public void addSubSub(int wx1, int bx1, int wx2, int bx2, int wx3, int bx3) {
        short[] weights = Network.NETWORK.inputWeights();

        for (int i = 0; i < SPECIES.loopBound(whiteFeatures.length); i += SPECIES.length()) {
            ShortVector whiteVector = ShortVector.fromArray(SPECIES, whiteFeatures, i);
            ShortVector blackVector = ShortVector.fromArray(SPECIES, blackFeatures, i);

            ShortVector wx1Vector = ShortVector.fromArray(SPECIES, weights, i + wx1 * Network.HIDDEN_SIZE);
            ShortVector wx2Vector = ShortVector.fromArray(SPECIES, weights, i + wx2 * Network.HIDDEN_SIZE);
            ShortVector wx3Vector = ShortVector.fromArray(SPECIES, weights, i + wx3 * Network.HIDDEN_SIZE);

            ShortVector bx1Vector = ShortVector.fromArray(SPECIES, weights, i + bx1 * Network.HIDDEN_SIZE);
            ShortVector bx2Vector = ShortVector.fromArray(SPECIES, weights, i + bx2 * Network.HIDDEN_SIZE);
            ShortVector bx3Vector = ShortVector.fromArray(SPECIES, weights, i + bx3 * Network.HIDDEN_SIZE);

            ShortVector whiteResultVector = whiteVector.add(wx1Vector).sub(wx2Vector).sub(wx3Vector);
            ShortVector blackResultVector = blackVector.add(bx1Vector).sub(bx2Vector).sub(bx3Vector);

            whiteResultVector.intoArray(whiteFeatures, i);
            blackResultVector.intoArray(blackFeatures, i);
        }
    }

    public void addAddSubSub(int wx1, int bx1, int wx2, int bx2, int wx3, int bx3, int wx4, int bx4) {
        short[] weights = Network.NETWORK.inputWeights();

        for (int i = 0; i < SPECIES.loopBound(whiteFeatures.length); i += SPECIES.length()) {
            ShortVector whiteVector = ShortVector.fromArray(SPECIES, whiteFeatures, i);
            ShortVector blackVector = ShortVector.fromArray(SPECIES, blackFeatures, i);

            ShortVector wx1Vector = ShortVector.fromArray(SPECIES, weights, i + wx1 * Network.HIDDEN_SIZE);
            ShortVector wx2Vector = ShortVector.fromArray(SPECIES, weights, i + wx2 * Network.HIDDEN_SIZE);
            ShortVector wx3Vector = ShortVector.fromArray(SPECIES, weights, i + wx3 * Network.HIDDEN_SIZE);
            ShortVector wx4Vector = ShortVector.fromArray(SPECIES, weights, i + wx4 * Network.HIDDEN_SIZE);

            ShortVector bx1Vector = ShortVector.fromArray(SPECIES, weights, i + bx1 * Network.HIDDEN_SIZE);
            ShortVector bx2Vector = ShortVector.fromArray(SPECIES, weights, i + bx2 * Network.HIDDEN_SIZE);
            ShortVector bx3Vector = ShortVector.fromArray(SPECIES, weights, i + bx3 * Network.HIDDEN_SIZE);
            ShortVector bx4Vector = ShortVector.fromArray(SPECIES, weights, i + bx4 * Network.HIDDEN_SIZE);

            ShortVector whiteResultVector = whiteVector.add(wx1Vector).add(wx2Vector).sub(wx3Vector).sub(wx4Vector);
            ShortVector blackResultVector = blackVector.add(bx1Vector).add(bx2Vector).sub(bx3Vector).sub(bx4Vector);

            whiteResultVector.intoArray(whiteFeatures, i);
            blackResultVector.intoArray(blackFeatures, i);
        }
    }

    public Accumulator copy() {
        return new Accumulator(
                Arrays.copyOf(whiteFeatures, whiteFeatures.length),
                Arrays.copyOf(blackFeatures, blackFeatures.length));
    }

}