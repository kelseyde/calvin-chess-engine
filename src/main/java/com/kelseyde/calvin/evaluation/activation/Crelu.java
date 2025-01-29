package com.kelseyde.calvin.evaluation.activation;

import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import static com.kelseyde.calvin.evaluation.NNUE.NETWORK;

/**
 * Implementation of NNUE inference using the CReLU (Clipped Rectified Linear Unit) activation function.
 * CReLU is defined as follows:
 * <p>
 *     CReLU(x) = clamp(x, 0, 1)
 * </p>
 */
public class Crelu {

    static final VectorSpecies<Short> SPECIES = ShortVector.SPECIES_PREFERRED;
    static final int UPPER_BOUND = SPECIES.loopBound(NETWORK.hiddenSize());
    static final int LOOP_LENGTH = SPECIES.length();

    static final ShortVector FLOOR = ShortVector.broadcast(SPECIES, 0);
    static final ShortVector CEIL = ShortVector.broadcast(SPECIES, NETWORK.quantisations()[0]);

    public static int forward(short[] us, short[] them, short[] weights, short bias) {

        final int qa = NETWORK.quantisations()[0];
        final int qb = NETWORK.quantisations()[1];
        final int qab = qa * qb;
        final int scale = NETWORK.scale();

        int eval = bias;

        // Forward-pass through the network, using the clipped ReLU activation function.
        // Implementation uses the Java Vector API to perform SIMD operations on multiple features at once.
        for (int i = 0; i < UPPER_BOUND; i += LOOP_LENGTH) {

            // Clip the 'us' inputs to the range [0, 255], multiply by the weights, and add to the running sum.
            eval += ShortVector.fromArray(SPECIES, us, i)
                    .min(CEIL)
                    .max(FLOOR)
                    .mul(ShortVector.fromArray(SPECIES, weights, i))
                    .reduceLanes(VectorOperators.ADD);

            // Clip the 'them' inputs to the range [0, 255], multiply by the weights, and add to the running sum.
            eval += ShortVector.fromArray(SPECIES, them, i)
                    .min(CEIL)
                    .max(FLOOR)
                    .mul(ShortVector.fromArray(SPECIES, weights, i + NETWORK.hiddenSize()))
                    .reduceLanes(VectorOperators.ADD);

        }

        // Scale the result to centipawn space, and divide by the quantisation factor.
        eval *= scale;
        eval /= qab;

        return eval;

    }

}
