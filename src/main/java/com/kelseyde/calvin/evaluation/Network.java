package com.kelseyde.calvin.evaluation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record Network(short[] inputWeights, short[] inputBiases, short[] outputWeights, short outputBias) {

    public static final int INPUT_SIZE = 768;
    public static final int HIDDEN_SIZE = 256;

    public static final String FILE = "calvin300mil_wdl0.nnue";

    public static final Network NETWORK = load();

    public static Network load() {
        try {
            InputStream inputStream = Network.class.getClassLoader().getResourceAsStream(FILE);
            if (inputStream == null) {
                throw new FileNotFoundException("NNUE file not found in resources");
            }

            byte[] fileBytes = inputStream.readAllBytes();
            inputStream.close();
            ByteBuffer buffer = ByteBuffer.wrap(fileBytes).order(ByteOrder.LITTLE_ENDIAN);

            int inputWeightsOffset = INPUT_SIZE * HIDDEN_SIZE;
            int inputBiasesOffset = HIDDEN_SIZE;
            int outputWeightsOffset = HIDDEN_SIZE * 2;

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
