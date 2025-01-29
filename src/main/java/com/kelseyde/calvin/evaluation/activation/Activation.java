package com.kelseyde.calvin.evaluation.activation;

/**
 * Represents the activation function used by the neural network.
 * The {@link Activation::forward} method implements the entire NNUE inference using the specified activation function.
 */
public enum Activation {

    SCReLU(Screlu::forward),
    CReLU(Crelu::forward);

    private final ActivationFunction function;

    Activation(ActivationFunction function) {
        this.function = function;
    }

    public int forward(short[] us, short[] them, short[] weights, short bias) {
        return function.forward(us, them, weights, bias);
    }

    public interface ActivationFunction {
        int forward(short[] us, short[] them, short[] weights, short bias);
    }

}
