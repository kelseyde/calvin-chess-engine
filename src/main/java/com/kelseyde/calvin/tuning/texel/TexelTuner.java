package com.kelseyde.calvin.tuning.texel;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.Evaluator;
import com.kelseyde.calvin.utils.notation.FEN;

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

    public int[] tune(int[] initialParams, Function<int[], Evaluator> createEvaluatorFunction) throws IOException {

        List<String> positions = loadPositions();
        System.out.println("number of positions: " + positions.size());
        Evaluator evaluator = createEvaluatorFunction.apply(initialParams);
        int[] bestParams = initialParams;
        double bestError = meanSquareError(evaluator, positions);
        int adjustValue = 1;

        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 0; i < bestParams.length; i++) {
                int[] newParams = Arrays.copyOf(bestParams, bestParams.length);
                newParams[i] += adjustValue;
                evaluator = createEvaluatorFunction.apply(newParams);
                double newError = meanSquareError(evaluator, positions);
                System.out.printf("tuning param %s of %s, error %s%n", i, bestParams.length, newError);

                if (newError < bestError) {
                    improved = true;
                    bestError = newError;
                    bestParams = Arrays.copyOf(newParams, newParams.length);
                    System.out.printf("++ improved param %s: %s%n", i, bestParams[i]);

                } else {
                    newParams[i] -= adjustValue * 2;
                    evaluator = createEvaluatorFunction.apply(newParams);
                    newError = meanSquareError(evaluator, positions);
                    if (newError < bestError) {
                        improved = true;
                        bestError = newError;
                        bestParams = Arrays.copyOf(newParams, newParams.length);
                        System.out.printf("-- improved param %s: %s%n", i, bestParams[i]);
                    }
                }
            }

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
            evaluator.init(board);
            int eval = evaluator.get();
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
        String result = position.split("\\[")[1];
        return switch (result) {
            case "1.0]" -> 1.0;
            case "0.5]" -> 0.5;
            case "0.0]" -> 0.0;
            default -> throw new IllegalArgumentException("illegal result!");
        };
    }

    private List<String> loadPositions() throws IOException {
        String fileName = String.format("src/test/resources/texel/" + positionsFileName);
        Path path = Paths.get(fileName);
        return Files.readAllLines(path);
    }

}
