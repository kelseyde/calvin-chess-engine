package com.kelseyde.calvin.tuning.texel;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.evaluation.Evaluator;
import com.kelseyde.calvin.utils.notation.FEN;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class StochasticTexelTuner {

    private final String positionsFileName;
    private Map<Board, Double> positions;
    private double k = 1.26;
    private int threadCount = 10;

    public StochasticTexelTuner(String fileName) {
        this.positionsFileName = fileName;
    }

    public int[] tune(int[] initialParams, Function<int[], EngineConfig> createConfigFunction, int epochs, int miniBatchSize)
            throws IOException {

        positions = loadPositions();
        List<Map<Board, Double>> partitions = partitionPositions(positions, threadCount);
        System.out.println("number of positions: " + positions.size());
        int[] bestParams = initialParams;
        double bestError = meanSquareError(bestParams, createConfigFunction);

        for (int epoch = 1; epoch <= epochs; epoch++) {
            Instant startEpoch = Instant.now();

            Collections.shuffle(partitions);
            for (Map<Board, Double> partition : partitions) {
                List<Board> batch = new ArrayList<>(partition.keySet());
                Collections.shuffle(batch);

                for (int i = 0; i < batch.size(); i += miniBatchSize) {
                    List<Board> miniBatch = batch.subList(i, Math.min(i + miniBatchSize, batch.size()));
                    Evaluator evaluator = new Evaluator(createConfigFunction.apply(bestParams));
                    double learningRate = 0.01; // Example learning rate
                    for (Board board : miniBatch) {
                        int eval = evaluator.evaluate(board);
                        if (!board.isWhiteToMove()) eval = -eval;
                        double prediction = prediction(eval);
                        double actual = positions.get(board);
                        double gradient = (prediction - actual) * eval * (1 - prediction) * prediction; // Compute gradient
                        for (int j = 0; j < bestParams.length; j++) {
                            // TODO check if casting to int causes problems
                            bestParams[j] -= (int) (learningRate * gradient); // Update parameters using SGD
                        }
                    }
                }
            }

            double newError = meanSquareError(bestParams, createConfigFunction);
            if (newError < bestError) {
                bestError = newError;
            }

            System.out.printf("Epoch %d completed in %s, Error: %.6f, Params: %s%n", epoch,
                    Duration.between(startEpoch, Instant.now()), newError, Arrays.toString(bestParams));
        }

        System.out.printf("Final params: %s, final error: %.6f%n", Arrays.toString(bestParams), bestError);
        return bestParams;
    }

    private double meanSquareError(int[] params, Function<int[], EngineConfig> createConfigFunction) {
        double totalError = 0.0;
        Evaluator evaluator = new Evaluator(createConfigFunction.apply(params));
        for (Map.Entry<Board, Double> entry : positions.entrySet()) {
            Board board = entry.getKey();
            int eval = evaluator.evaluate(board);
            if (!board.isWhiteToMove()) eval = -eval;
            double prediction = prediction(eval);
            double actual = entry.getValue();
            totalError += error(prediction, actual);
        }
        return totalError / positions.size();
    }

    private double prediction(int eval) {
        return 1.0 / (1.0 + Math.pow(10, (-k * eval / 400)));
    }

    private double error(double predicted, double actual) {
        return Math.pow(actual - predicted, 2);
    }

    private Map<Board, Double> loadPositions() throws IOException {
        List<String> fens = loadFens();
        Map<Board, Double> positions = new HashMap<>();
        for (String fen : fens) {
            Board board = FEN.toBoard(fen.split("\\[")[0]);
            Double result = result(fen);
            positions.put(board, result);
        }
        return positions;
    }

    private List<String> loadFens() throws IOException {
        String fileName = String.format("src/test/resources/texel/" + positionsFileName);
        Path path = Paths.get(fileName);
        return Files.readAllLines(path);
    }

    private double result(String position) {
        String result = position.split("\"")[1];
        return switch (result) {
            case "1-0" -> 1.0;
            case "1/2-1/2" -> 0.5;
            case "0-1" -> 0.0;
            default -> throw new IllegalArgumentException("illegal result!");
        };
    }

    private List<Map<Board, Double>> partitionPositions(Map<Board, Double> positions, int partitions) {
        List<Map<Board, Double>> partitionedPositions = new ArrayList<>();
        List<Map.Entry<Board, Double>> positionEntries = new ArrayList<>(positions.entrySet());
        int positionsPerPartition = positions.size() / partitions;
        int currentIndex = 0;
        for (int i = 1; i <= partitions; i++) {
            int startIndex = currentIndex;
            int endIndex = Math.min(currentIndex + positionsPerPartition, positions.size());
            partitionedPositions.add(
                    positionEntries.subList(startIndex, endIndex).stream()
                            .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll));
            currentIndex += positionsPerPartition;
        }
        return partitionedPositions;
    }
}

