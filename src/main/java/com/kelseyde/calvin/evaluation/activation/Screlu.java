package com.kelseyde.calvin.evaluation.activation;

import jdk.incubator.vector.*;

import static com.kelseyde.calvin.evaluation.NNUE.NETWORK;
import static jdk.incubator.vector.VectorOperators.S2I;

/**
 * Implementation of NNUE inference using the SCReLU (Squared Clipped Rectified Linear Unit) activation function.
 * SCReLU is defined as follows:
 * <p>
 *     SCReLU(x) = clamp(x, 0, 1)^2
 * </p>
 */
public class Screlu {

    static final VectorSpecies<Short> SPECIES = ShortVector.SPECIES_PREFERRED;
    static final int UPPER_BOUND = SPECIES.loopBound(NETWORK.hiddenSize());
    static final int LOOP_LENGTH = SPECIES.length();

    static final ShortVector FLOOR = ShortVector.broadcast(SPECIES, 0);
    static final ShortVector CEIL = ShortVector.broadcast(SPECIES, NETWORK.quantisations()[0]);

    static final int QA = NETWORK.quantisations()[0];
    static final int QB = NETWORK.quantisations()[1];
    static final int QAB = QA * QB;

    public static int forward(short[] us, short[] them) {

        final int scale = NETWORK.scale();
        final short[] weights = NETWORK.outputWeights();

        // Forward-pass through the network, using the squared clipped ReLU activation function.
        // Implementation uses the Java Vector API to perform SIMD operations on multiple features at once.
        // Unrolled 4 times for better performance

        IntVector sum1 = IntVector.zero(IntVector.SPECIES_PREFERRED);
        IntVector sum2 = IntVector.zero(IntVector.SPECIES_PREFERRED);
        IntVector sum3 = IntVector.zero(IntVector.SPECIES_PREFERRED);
        IntVector sum4 = IntVector.zero(IntVector.SPECIES_PREFERRED);

        for (int i = 0; i < UPPER_BOUND; i += 4 * LOOP_LENGTH) {
            // Unroll 1
            ShortVector usInputs1     = ShortVector.fromArray(SPECIES, us, i);
            ShortVector themInputs1   = ShortVector.fromArray(SPECIES, them, i);
            ShortVector usWeights1    = ShortVector.fromArray(SPECIES, weights, i);
            ShortVector themWeights1  = ShortVector.fromArray(SPECIES, weights, i + NETWORK.hiddenSize());

            // Unroll 2
            ShortVector usInputs2     = ShortVector.fromArray(SPECIES, us, i + LOOP_LENGTH);
            ShortVector themInputs2   = ShortVector.fromArray(SPECIES, them, i + LOOP_LENGTH);
            ShortVector usWeights2    = ShortVector.fromArray(SPECIES, weights, i + LOOP_LENGTH);
            ShortVector themWeights2  = ShortVector.fromArray(SPECIES, weights, i + NETWORK.hiddenSize() + LOOP_LENGTH);

            // Unroll 3
            ShortVector usInputs3     = ShortVector.fromArray(SPECIES, us, i + 2 * LOOP_LENGTH);
            ShortVector themInputs3   = ShortVector.fromArray(SPECIES, them, i + 2 * LOOP_LENGTH);
            ShortVector usWeights3    = ShortVector.fromArray(SPECIES, weights, i + 2 * LOOP_LENGTH);
            ShortVector themWeights3  = ShortVector.fromArray(SPECIES, weights, i + NETWORK.hiddenSize() + 2 * LOOP_LENGTH);

            // Unroll 4
            ShortVector usInputs4     = ShortVector.fromArray(SPECIES, us, i + 3 * LOOP_LENGTH);
            ShortVector themInputs4   = ShortVector.fromArray(SPECIES, them, i + 3 * LOOP_LENGTH);
            ShortVector usWeights4    = ShortVector.fromArray(SPECIES, weights, i + 3 * LOOP_LENGTH);
            ShortVector themWeights4  = ShortVector.fromArray(SPECIES, weights, i + NETWORK.hiddenSize() + 3 * LOOP_LENGTH);

            // Clip the inputs to the range [0, 255].
            usInputs1 = usInputs1.max(FLOOR).min(CEIL);
            themInputs1 = themInputs1.max(FLOOR).min(CEIL);
            usInputs2 = usInputs2.max(FLOOR).min(CEIL);
            themInputs2 = themInputs2.max(FLOOR).min(CEIL);
            usInputs3 = usInputs3.max(FLOOR).min(CEIL);
            themInputs3 = themInputs3.max(FLOOR).min(CEIL);
            usInputs4 = usInputs4.max(FLOOR).min(CEIL);
            themInputs4 = themInputs4.max(FLOOR).min(CEIL);

            // Multiply the inputs by the weights.
            final ShortVector usTerms1 = usInputs1.mul(usWeights1);
            final ShortVector themTerms1 = themInputs1.mul(themWeights1);
            final ShortVector usTerms2 = usInputs2.mul(usWeights2);
            final ShortVector themTerms2 = themInputs2.mul(themWeights2);
            final ShortVector usTerms3 = usInputs3.mul(usWeights3);
            final ShortVector themTerms3 = themInputs3.mul(themWeights3);
            final ShortVector usTerms4 = usInputs4.mul(usWeights4);
            final ShortVector themTerms4 = themInputs4.mul(themWeights4);

            // Split the inputs and weighted terms into low and high parts, to enable 32-bit multiplication.
            final Vector<Integer> usInputsLo1     = usInputs1.convert(S2I, 0);
            final Vector<Integer> usInputsHi1     = usInputs1.convert(S2I, 1);
            final Vector<Integer> themInputsLo1   = themInputs1.convert(S2I, 0);
            final Vector<Integer> themInputsHi1   = themInputs1.convert(S2I, 1);

            final Vector<Integer> usInputsLo2     = usInputs2.convert(S2I, 0);
            final Vector<Integer> usInputsHi2     = usInputs2.convert(S2I, 1);
            final Vector<Integer> themInputsLo2   = themInputs2.convert(S2I, 0);
            final Vector<Integer> themInputsHi2   = themInputs2.convert(S2I, 1);

            final Vector<Integer> usInputsLo3     = usInputs3.convert(S2I, 0);
            final Vector<Integer> usInputsHi3     = usInputs3.convert(S2I, 1);
            final Vector<Integer> themInputsLo3   = themInputs3.convert(S2I, 0);
            final Vector<Integer> themInputsHi3   = themInputs3.convert(S2I, 1);

            final Vector<Integer> usInputsLo4     = usInputs4.convert(S2I, 0);
            final Vector<Integer> usInputsHi4     = usInputs4.convert(S2I, 1);
            final Vector<Integer> themInputsLo4   = themInputs4.convert(S2I, 0);
            final Vector<Integer> themInputsHi4   = themInputs4.convert(S2I, 1);

            final Vector<Integer> usTermsLo1      = usTerms1.convert(S2I, 0);
            final Vector<Integer> usTermsHi1      = usTerms1.convert(S2I, 1);
            final Vector<Integer> themTermsLo1    = themTerms1.convert(S2I, 0);
            final Vector<Integer> themTermsHi1    = themTerms1.convert(S2I, 1);

            final Vector<Integer> usTermsLo2      = usTerms2.convert(S2I, 0);
            final Vector<Integer> usTermsHi2      = usTerms2.convert(S2I, 1);
            final Vector<Integer> themTermsLo2    = themTerms2.convert(S2I, 0);
            final Vector<Integer> themTermsHi2    = themTerms2.convert(S2I, 1);

            final Vector<Integer> usTermsLo3      = usTerms3.convert(S2I, 0);
            final Vector<Integer> usTermsHi3      = usTerms3.convert(S2I, 1);
            final Vector<Integer> themTermsLo3    = themTerms3.convert(S2I, 0);
            final Vector<Integer> themTermsHi3    = themTerms3.convert(S2I, 1);

            final Vector<Integer> usTermsLo4      = usTerms4.convert(S2I, 0);
            final Vector<Integer> usTermsHi4      = usTerms4.convert(S2I, 1);
            final Vector<Integer> themTermsLo4    = themTerms4.convert(S2I, 0);
            final Vector<Integer> themTermsHi4    = themTerms4.convert(S2I, 1);

            // Multiply the inputs by the weighted terms, and add the results to the running sum.
            sum1 = sum1.add(usInputsLo1.mul(usTermsLo1)
                    .add(usInputsHi1.mul(usTermsHi1))
                    .add(themInputsLo1.mul(themTermsLo1))
                    .add(themInputsHi1.mul(themTermsHi1)));

            sum2 = sum2.add(usInputsLo2.mul(usTermsLo2)
                    .add(usInputsHi2.mul(usTermsHi2))
                    .add(themInputsLo2.mul(themTermsLo2))
                    .add(themInputsHi2.mul(themTermsHi2)));

            sum3 = sum3.add(usInputsLo3.mul(usTermsLo3)
                    .add(usInputsHi3.mul(usTermsHi3))
                    .add(themInputsLo3.mul(themTermsLo3))
                    .add(themInputsHi3.mul(themTermsHi3)));

            sum4 = sum4.add(usInputsLo4.mul(usTermsLo4)
                    .add(usInputsHi4.mul(usTermsHi4))
                    .add(themInputsLo4.mul(themTermsLo4))
                    .add(themInputsHi4.mul(themTermsHi4)));
        }

        int eval = (int) (sum1.add(sum2).add(sum3).add(sum4)).reduceLanesToLong(VectorOperators.ADD);

        // Since squaring the inputs also squares quantisation, we need to divide that out.
        eval /= QA;

        // Add the output bias, scale the result to centipawn space, and divide by the quantisation factor.
        eval += NETWORK.outputBias();
        eval *= scale;
        eval /= QAB;

        return eval;
    }
}