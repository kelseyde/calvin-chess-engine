package com.kelseyde.calvin.nnue;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Network {

    public static final int INPUT_LAYER_SIZE = 768;
    public static final int HIDDEN_LAYER_SIZE = 256;

    public static final Network DEFAULT = loadNetwork();

    /**
     * The weights for each connection between the input layer and the hidden layer.
     * Size is INPUT_SIZE * HIDDEN_LAYER_SIZE = 768 * 256 = 196608
     */
    public short[] inputWeights;

    /**
     * Biases for the first hidden layer.
     * Size is HIDDEN_LAYER_SIZE = 256.
     */
    public short[] inputBiases;

    /**
     * Weights for each connection between the hidden layer and the output layer.
     * Size is HIDDEN_LAYER_SIZE * 2 = 512.
     */
    public short[] outputWeights;

    /**
     * Bias for the output layer.
     */
    public short outputBias;

    public Network(short[] inputWeights, short[] inputBiases, short[] outputWeights, short outputBias) {
        this.inputWeights = inputWeights;
        this.inputBiases = inputBiases;
        this.outputWeights = outputWeights;
        this.outputBias = outputBias;
    }

    public static Network loadNetwork() {
        System.out.println("Loading NNUE");
        Path path = Paths.get("/Users/kelseyde/git/dan/calvin/calvin-chess-engine/src/main/resources/nnue/256HL-3B5083B8.nnue");

        try {
            byte[] fileBytes = Files.readAllBytes(path);
            ByteBuffer buffer = ByteBuffer.wrap(fileBytes).order(ByteOrder.LITTLE_ENDIAN);

            int inputWeightsOffset = Network.INPUT_LAYER_SIZE * Network.HIDDEN_LAYER_SIZE;
            int inputBiasesOffset = Network.HIDDEN_LAYER_SIZE;
            int outputWeightsOffset = Network.HIDDEN_LAYER_SIZE * 2;

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
}
