package com.kelseyde.calvin.evaluation.nnue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Network {

    public static final int L0_SIZE = 768;
    public static final int L1_SIZE = 256;

    public static Network DEFAULT = loadNetwork();

    /**
     * The weights for each connection between the input layer and the hidden layer.
     * Size is INPUT_SIZE * HIDDEN_LAYER_SIZE = 768 * 256 = 196608
     */
    public short[] l0weights;

    /**
     * Biases for the first hidden layer.
     * Size is HIDDEN_LAYER_SIZE = 256.
     */
    public short[] l0biases;

    /**
     * Weights for each connection between the hidden layer and the output layer.
     * Size is HIDDEN_LAYER_SIZE = 256
     */
    public short[] l1weights;

    /**
     * Bias for the output layer.
     */
    public short l1bias;

    public Network(short[] l0weights, short[] inputBiases, short[] outputWeights, short outputBias) {
        this.l0weights = l0weights;
        this.l0biases = inputBiases;
        this.l1weights = outputWeights;
        this.l1bias = outputBias;
    }

    public static Network loadNetwork() {
        try {
            InputStream inputStream = Network.class.getClassLoader().getResourceAsStream("nnue/256HL-3B5083B8.nnue");
            if (inputStream == null) {
                throw new FileNotFoundException("NNUE file not found in resources");
            }

            byte[] fileBytes = inputStream.readAllBytes();
            inputStream.close();
            ByteBuffer buffer = ByteBuffer.wrap(fileBytes).order(ByteOrder.LITTLE_ENDIAN);

            int inputWeightsOffset = L0_SIZE * L1_SIZE;
            int inputBiasesOffset = L1_SIZE;
            int outputWeightsOffset = L1_SIZE * 2;

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

            return new Network(inputWeights, inputBiases, outputWeights, outputBias);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load NNUE network", e);
        }
    }

    public static void main(String[] args) {
        DEFAULT = loadNetwork();
    }
}
