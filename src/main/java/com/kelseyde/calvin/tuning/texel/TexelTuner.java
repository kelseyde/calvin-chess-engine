package com.kelseyde.calvin.tuning.texel;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.Evaluator;
import com.kelseyde.calvin.utils.notation.FEN;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class TexelTuner {

    public TexelTuner(String fileName) {
        this.positionsFileName = fileName;
    }

    private final String positionsFileName;

    private Map<Board, Double> positions;

    private Delta[] deltas;

    private double k = 1.26;

    public int[] tune(int[] initialParams, Function<int[], Evaluator> createEvaluatorFunction) throws IOException {

        /**
         * TODO
         * -stochastic gradient descent/gradient descent/ADAM
          */

        positions = loadPositions();
        initDeltas(initialParams.length);
        System.out.println("number of positions: " + positions.size());
        Evaluator evaluator = createEvaluatorFunction.apply(initialParams);
        int[] bestParams = initialParams;
        double bestError = meanSquareError(evaluator);
        int iterations = 0;

        boolean improved = true;
        while (improved) {
            iterations++;
            Instant start = Instant.now();
            improved = false;
            int modifiedParams = 0;
            for (int i = 0; i < bestParams.length; i++) {
                int[] newParams = Arrays.copyOf(bestParams, bestParams.length);
                int delta = deltas[i].delta;
                newParams[i] += delta;
                evaluator = createEvaluatorFunction.apply(newParams);
                double newError = meanSquareError(evaluator);
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
                    evaluator = createEvaluatorFunction.apply(newParams);
                    newError = meanSquareError(evaluator);
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

    public double meanSquareError(Evaluator evaluator) throws IOException {

        int numberOfPositions = positions.size();
        double totalError = 0.0;
        for (Map.Entry<Board, Double> entry : positions.entrySet()) {
            Board board = entry.getKey();
            int eval = evaluator.evaluate(board);
            if (!board.isWhiteToMove()) eval = -eval;
            double prediction = prediction(eval);
            double actual = entry.getValue();
            double error = error(prediction, actual);
            totalError += error;
        }
        return totalError / numberOfPositions;

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

    public List<String> loadFens() throws IOException {
        String fileName = String.format("src/test/resources/texel/" + positionsFileName);
        Path path = Paths.get(fileName);
        return Files.readAllLines(path);
    }

//    public double tuneScalingConstant(Evaluator evaluator) throws IOException {
//        List<String> positions = loadFens();
//        System.out.println("number of positions: " + positions.size());
//        double bestError = meanSquareError(evaluator, positions);
//
//        boolean improved = true;
//        while (improved) {
//            improved = false;
//            k += 0.01;
//            System.out.println("1 new k = " + k);
//            double newError = meanSquareError(evaluator, positions);
//            if (newError < bestError) {
//                improved = true;
//                bestError = newError;
//                System.out.println("improved k " + k + " " + bestError);
//            } else {
//                k -= 0.02;
//                System.out.println("2 new k = " + k);
//                newError = meanSquareError(evaluator, positions);
//                if (newError < bestError) {
//                    improved = true;
//                    bestError = newError;
//                    System.out.println("improved k " + k + " " + bestError);
//                }
//            }
//        }
//        System.out.println("final k " + k);
//        return k;
//    }
//
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

    @Data
    @AllArgsConstructor
    private static class Delta {
        private static final int HALVED = 0;
        private static final int DOUBLED = 1;
        private int delta;
        private int halvedOrDoubled;
    }

}
