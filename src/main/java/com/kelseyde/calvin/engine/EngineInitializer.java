package com.kelseyde.calvin.engine;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.NNUE;
import com.kelseyde.calvin.search.ParallelSearcher;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.tables.tt.TranspositionTable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EngineInitializer {

    public static Engine loadEngine() {
        EngineConfig config = new EngineConfig();
        TranspositionTable transpositionTable = new TranspositionTable(config.defaultHashSizeMb);
        Search searcher = new ParallelSearcher(config, transpositionTable);
        Engine engine = new Engine(config, searcher);
        engine.setPosition(new Board());
        return engine;
    }

    public static NNUE.Network loadNetwork(String file, int inputSize, int hiddenSize) {
        try {
            InputStream inputStream = NNUE.Network.class.getClassLoader().getResourceAsStream(file);
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

            return new NNUE.Network(inputWeights, inputBiases, outputWeights, outputBias);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load NNUE network", e);
        }
    }

}
