package com.kelseyde.calvin.evaluation.activation;

import static com.kelseyde.calvin.evaluation.NNUE.NETWORK;

public class Screlu {

    private final static int[] SCRELU_TABLE = new int[Short.MAX_VALUE - Short.MIN_VALUE + 1];

    static {
        for (int i = Short.MIN_VALUE; i <= Short.MAX_VALUE; i++) {
            SCRELU_TABLE[i - (int) Short.MIN_VALUE] = screlu((short) (i));
        }
    }

    private static int screlu(short i) {
        int v = Math.max(0, Math.min(i, NETWORK.quantisations()[0]));
        return v * v;
    }

    public static int forward(short[] us, short[] them) {

        final int qa = NETWORK.quantisations()[0];
        final int qb = NETWORK.quantisations()[1];
        final int qab = qa * qb;
        final int scale = NETWORK.scale();
        final short[] weights = NETWORK.outputWeights();
        final int hiddenSize = NETWORK.hiddenSize();

        int eval = 0;

        // Iterate over all the elements in 'us' and 'them' arrays.
        for (int i = 0; i < hiddenSize; i++) {

            eval += SCRELU_TABLE[us[i] - (int) Short.MIN_VALUE] * (int) weights[i]
                    + SCRELU_TABLE[them[i] - (int) Short.MIN_VALUE] * (int) weights[i + NETWORK.hiddenSize()];
        }

        // Since squaring the inputs also squares quantisation, divide by 'qa'.
        eval /= qa;

        // Add the output bias, scale to centipawn space, and divide by the quantisation factor.
        eval += NETWORK.outputBias();
        eval *= scale;
        eval /= qab;

        return eval;
    }

}
