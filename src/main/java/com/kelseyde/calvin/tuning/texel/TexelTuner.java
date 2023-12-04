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
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class TexelTuner {

    public TexelTuner(String fileName) {
        this.positionsFileName = fileName;
    }

    private static final double K = 0.20;

    private final String positionsFileName;

    private Delta[] deltas;

    public int[] tune(int[] initialParams, Function<int[], Evaluator> createEvaluatorFunction) throws IOException {

        List<String> positions = loadPositions();
        initDeltas(initialParams.length);
        System.out.println("number of positions: " + positions.size());
        Evaluator evaluator = createEvaluatorFunction.apply(initialParams);
        int[] bestParams = initialParams;
        double bestError = meanSquareError(evaluator, positions);

        boolean improved = true;
        while (improved) {
            improved = false;
            int modifiedParams = 0;
            for (int i = 0; i < bestParams.length; i++) {
                int[] newParams = Arrays.copyOf(bestParams, bestParams.length);
                int delta = deltas[i].delta;
                newParams[i] += delta;
                evaluator = createEvaluatorFunction.apply(newParams);
                double newError = meanSquareError(evaluator, positions);
                System.out.printf("tuning param %s of %s, error %s%n", i, bestParams.length, newError);

                if (newError < bestError) {
                    improved = true;
                    bestError = newError;
                    bestParams = Arrays.copyOf(newParams, newParams.length);
                    int newDelta = delta * 2;
                    deltas[i].setDelta(newDelta);
                    deltas[i].setHalvedOrDoubled(Delta.DOUBLED);
                    System.out.printf("+%s improved param %s: %s%n", delta, i, bestParams[i]);

                } else {
                    newParams[i] -= delta * 2;
                    evaluator = createEvaluatorFunction.apply(newParams);
                    newError = meanSquareError(evaluator, positions);
                    if (newError < bestError) {
                        improved = true;
                        bestError = newError;
                        bestParams = Arrays.copyOf(newParams, newParams.length);
                        int newDelta = delta * 2;
                        deltas[i].setDelta(newDelta);
                        deltas[i].setHalvedOrDoubled(Delta.DOUBLED);
                        System.out.printf("-%s improved param %s: %s%n", delta, i, bestParams[i]);
                    } else {
                        int newDelta = delta / 2;
                        deltas[i].setDelta(newDelta);
                        deltas[i].setHalvedOrDoubled(Delta.HALVED);
                    }
                }
            }
            System.out.printf("tuned %s params: %s%n", modifiedParams, Arrays.toString(bestParams));

        }
        System.out.printf("final params: %s, final error: %s%n", Arrays.toString(bestParams), bestError);
        return bestParams;

    }

    public double meanSquareError(Evaluator evaluator, List<String> positions) throws IOException {

        int numberOfPositions = positions.size();
        double totalError = 0.0;
        for (String position : positions) {
            String fen = position.split("\\[")[0];
            Board board = FEN.toBoard(fen);
            int eval = evaluator.evaluate(board);
            if (!board.isWhiteToMove()) eval = -eval;
            double prediction = prediction(eval);
            double actual = result(position);
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
        return 1 / (1 + Math.pow(10, -K * (float) eval / 400));
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

    private List<String> loadPositions() throws IOException {
        String fileName = String.format("src/test/resources/texel/" + positionsFileName);
        Path path = Paths.get(fileName);
        return Files.readAllLines(path);
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
