package com.kelseyde.calvin.evaluation.activation;

import static com.kelseyde.calvin.evaluation.NNUE.NETWORK;

public class Crelu {

    public static int forward(short[] us, short[] them) {

        final int qa = NETWORK.quantisations()[0];
        final int qb = NETWORK.quantisations()[1];
        final int qab = qa * qb;
        final int scale = NETWORK.scale();
        final short[] weights = NETWORK.outputWeights();
        final int hiddenSize = NETWORK.hiddenSize();

        int eval = NETWORK.outputBias();

        // Iterate over all elements in the 'us' and 'them' arrays.
        for (int i = 0; i < hiddenSize; i++) {

            // Clip the 'us' input to the range [0, qa] and multiply by its corresponding weight.
            short usInput = (short) Math.max(0, Math.min(us[i], qa));
            int usTerm = usInput * weights[i];

            // Clip the 'them' input to the range [0, qa] and multiply by its corresponding weight.
            short themInput = (short) Math.max(0, Math.min(them[i], qa));
            int themTerm = themInput * weights[i + hiddenSize];

            // Accumulate the results.
            eval += usTerm + themTerm;
        }

        // Scale the result to centipawn space, and divide by the quantisation factor.
        eval *= scale;
        eval /= qab;

        return eval;
    }

}
