package com.kelseyde.calvin.evaluation.nnue;

import jdk.incubator.vector.ShortVector;

import static com.kelseyde.calvin.evaluation.nnue.Accumulator.SPECIES;

public class OldAccumulator {

    public void addWeights(short[] features, short[] weights, int offset) {
        int length = features.length;
        int i = 0;

        for (; i < SPECIES.loopBound(length); i += SPECIES.length()) {
            var vFeatures = ShortVector.fromArray(SPECIES, features, i);
            var vWeights = ShortVector.fromArray(SPECIES, weights, i + offset);
            var vResult = vFeatures.add(vWeights);
            vResult.intoArray(features, i);
        }

        for (; i < length; i++) {
            features[i] -= weights[i + offset];
        }
    }

    public void subtractWeights(short[] features, short[] weights, int offset) {
        int length = features.length;
        int i = 0;

        for (; i < SPECIES.loopBound(length); i += SPECIES.length()) {
            var vFeatures = ShortVector.fromArray(SPECIES, features, i);
            var vWeights = ShortVector.fromArray(SPECIES, weights, i + offset);
            var vResult = vFeatures.sub(vWeights);
            vResult.intoArray(features, i);
        }

        for (; i < length; i++) {
            features[i] -= weights[i + offset];
        }
    }

}
