package com.kelseyde.calvin.tuning.texel;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.evaluation.Evaluator;
import com.kelseyde.calvin.nnue.NNUE;
import com.kelseyde.calvin.utils.notation.FEN;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class TexelTuner {

    public TexelTuner(String fileName) {
        this.positionsFileName = fileName;
    }

    private final String positionsFileName;

    private Map<Board, Double> positions;
    private List<Map<Board, Double>> partitions;

    private Delta[] deltas;

    private double k = 1.26;

    private int threadCount = 10;

    public int[] tune(int[] initialParams, Function<int[], EngineConfig> createConfigFunction)
            throws IOException, ExecutionException, InterruptedException {

        positions = loadPositions();
        partitions = partitionPositions(positions, threadCount);
        initDeltas(initialParams.length);
        System.out.println("number of positions: " + positions.size());
        int[] bestParams = initialParams;
        double bestError = meanSquareError(bestParams, createConfigFunction);
        System.out.println("current best error: " + bestError);
        int iterations = 0;

        boolean improved = true;
        while (improved || !allDeltasOne()) {
            iterations++;
            Instant start = Instant.now();
            improved = false;
            int modifiedParams = 0;
            for (int i = 0; i < bestParams.length; i++) {
                int[] newParams = Arrays.copyOf(bestParams, bestParams.length);
                int delta = deltas[i].delta;
                newParams[i] += delta;
                double newError = meanSquareError(newParams, createConfigFunction);
                System.out.printf("tuning param %s of %s, error %s%n", i, bestParams.length, newError);

                if (newError < bestError) {
                    improved = true;
                    modifiedParams++;
                    bestError = newError;
                    bestParams = Arrays.copyOf(newParams, newParams.length);
                    int newDelta = delta * 2;
                    deltas[i].setDelta(newDelta);
                    deltas[i].setHalvedOrDoubled(Delta.DOUBLED);
                    System.out.printf("+%s improved param %s: %s%n", delta, i, bestParams[i]);

                } else {
                    newParams[i] -= delta * 2;
                    newError = meanSquareError(newParams, createConfigFunction);
                    if (newError < bestError) {
                        improved = true;
                        modifiedParams++;
                        bestError = newError;
                        bestParams = Arrays.copyOf(newParams, newParams.length);
                        int newDelta = delta * 2;
                        deltas[i].setDelta(newDelta);
                        deltas[i].setHalvedOrDoubled(Delta.DOUBLED);
                        System.out.printf("-%s improved param %s: %s%n", delta, i, bestParams[i]);
                    } else {
                        int newDelta = Math.max(1, delta / 2);
                        deltas[i].setDelta(newDelta);
                        deltas[i].setHalvedOrDoubled(Delta.HALVED);
                    }
                }
            }
            System.out.printf("tuned %s/%s params: %s%n", modifiedParams, bestParams.length, Arrays.toString(bestParams));
            System.out.printf("iteration %s completed in %s%n", iterations, Duration.between(start, Instant.now()));

        }
        System.out.printf("final params: %s, final error: %s%n", Arrays.toString(bestParams), bestError);
        return bestParams;

    }

    public double meanSquareError(int[] params, Function<int[], EngineConfig> createConfigFunction)
            throws ExecutionException, InterruptedException {
        int totalPositions = positions.size();
        List<CompletableFuture<Double>> threads = partitions.stream()
                .map(partition -> CompletableFuture.supplyAsync(() -> totalError(partition, params, createConfigFunction)))
                .toList();
        CompletableFuture.allOf(threads.toArray(CompletableFuture[]::new)).get();
        Double combinedResult = threads.stream().map(CompletableFuture::join).reduce(0.0, Double::sum);
        return combinedResult / totalPositions;
    }

    public double totalError(Map<Board, Double> partitionedPositions, int[] params, Function<int[], EngineConfig> createConfigFunction) {
        NNUE nnue = new NNUE();
        double totalError = 0.0;
        for (Map.Entry<Board, Double> entry : partitionedPositions.entrySet()) {
            Board board = entry.getKey();
            nnue.activateAll(board);
            int eval = nnue.evaluate(board);
            if (!board.isWhiteToMove()) eval = -eval;
            double prediction = prediction(eval);
            double actual = entry.getValue();
            double error = error(prediction, actual);
            totalError += error;
        }
        return totalError;
    }

    /**
     * Using a sigmoid function to transform the static evaluation into a prediction of the game outcome between 0 and 1,
     * 0 indicating a loss and 1 indicating a win.
     */
    private double prediction(int eval) {
        return 1.0 / (1.0 + Math.pow(10, (-k * eval / 400)));
    }

    private double error(double predicted, double actual) {
        return Math.pow(actual - predicted, 2);
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
        List<Map.Entry<Board, Double>> positionEntries = positions.entrySet().stream().toList();
        int positionsPerPartition = positions.size() / partitions;
        int currentIndex = 0;
        for (int i = 1; i < partitions + 1; i++) {
            int startIndex = currentIndex;
            int endIndex = Math.min(currentIndex + positionsPerPartition, positions.size());
            partitionedPositions.add(
                    positionEntries.subList(startIndex, endIndex).stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            currentIndex += positionsPerPartition;
        }
        return partitionedPositions;
    }

    public List<String> loadFens() throws IOException {
        String fileName = String.format("src/test/resources/texel/" + positionsFileName);
        Path path = Paths.get(fileName);
        return Files.readAllLines(path);
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

    private void initDeltas(int size) {
        deltas = new Delta[size];
        for (int i = 0; i < size; i++) {
            deltas[i] = new Delta(1, Delta.DOUBLED);
        }
    }

    private boolean allDeltasOne() {
        return Arrays.stream(deltas).allMatch(delta -> delta.delta == 1);
    }

    @Data
    @AllArgsConstructor
    private static class Delta {
        private static final int HALVED = 0;
        private static final int DOUBLED = 1;
        private int delta;
        private int halvedOrDoubled;
    }

}
