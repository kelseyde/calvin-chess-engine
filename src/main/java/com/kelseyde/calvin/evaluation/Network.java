package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.evaluation.activation.Activation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents the neural network used by the engine in its evaluation function.
 * The network is loaded from the configured file on startup. The network file location, architecture, and activation
 * function are all configurable in the code via the {@link Network.Builder} builder.
 */
public record Network(int inputSize,
                      int hiddenSize,
                      Activation activation,
                      int[] quantisations,
                      int scale,
                      short[] inputWeights,
                      short[] inputBiases,
                      short[] outputWeights,
                      short outputBias) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String file;
        private int inputSize;
        private int hiddenSize;
        private Activation activation;
        private int[] quantisations;
        private int scale;

        public Builder file(String file) {
            this.file = file;
            return this;
        }

        public Builder inputSize(int inputSize) {
            this.inputSize = inputSize;
            return this;
        }

        public Builder activation(Activation activation) {
            this.activation = activation;
            return this;
        }

        public Builder hiddenSize(int hiddenSize) {
            this.hiddenSize = hiddenSize;
            return this;
        }

        public Builder quantisations(int[] quantisations) {
            this.quantisations = quantisations;
            return this;
        }

        public Builder scale(int scale) {
            this.scale = scale;
            return this;
        }

        public Network build() {
            return loadNetwork();
        }

        public Network loadNetwork() {

            try {
                InputStream inputStream = Network.class.getClassLoader().getResourceAsStream(file);
                if (inputStream == null) {
                    throw new FileNotFoundException("NNUE file not found in resources");
                }

                byte[] fileBytes = inputStream.readAllBytes();
                inputStream.close();
                ByteBuffer buffer = ByteBuffer.wrap(fileBytes).order(ByteOrder.LITTLE_ENDIAN);

                int inputWeightsOffset = inputSize * hiddenSize;
                int inputBiasesOffset = hiddenSize;
                int outputWeightsOffset = hiddenSize * 2;

                short[] inputWeights = new short[inputWeightsOffset];
                short[] inputBiases = new short[inputBiasesOffset];
                short[] outputWeights = new short[outputWeightsOffset];

                for (int i = 0; i < inputWeightsOffset; i++) {
                    inputWeights[i] = buffer.getShort();
                }

                for (int i = 0; i < inputBiasesOffset; i++) {
                    inputBiases[i] = buffer.getShort();
                }

                for (int i = 0; i < outputWeightsOffset; i++) {
                    outputWeights[i] = buffer.getShort();
                }

                short outputBias = buffer.getShort();

                while (buffer.hasRemaining()) {
                    if (buffer.getShort() != 0) {
                        throw new RuntimeException("Failed to load NNUE network: invalid file format");
                    }
                }

                return new Network(
                        inputSize, hiddenSize, activation, quantisations, scale,
                        inputWeights, inputBiases, outputWeights, outputBias
                );

            } catch (IOException e) {
                throw new RuntimeException("Failed to load NNUE network", e);
            }

        }

    }

}
