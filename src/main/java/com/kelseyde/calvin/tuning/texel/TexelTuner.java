package com.kelseyde.calvin.tuning.texel;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.evaluation.Evaluator;
import com.kelseyde.calvin.utils.notation.FEN;

public class TexelTuner {

    private static final double K = 0.20;

    private final Evaluation evaluator = new Evaluator(new Board());

    private double meanSquareError(int k) {

        String[] positions = loadPositions();
        int numberOfPositions = positions.length;
        double error = 0.0;
        for (String position : positions) {
            int eval = evaluate(position);
            double prediction = prediction(eval);
            double actual = result(position);
            error += error(prediction, actual);
        }
        return error / numberOfPositions;

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
        return 0.0;
    }

    private int evaluate(String position) {
        String fen = position.split("\\[")[0];
        Board board = FEN.toBoard(fen);
        evaluator.init(board);
        return evaluator.get();
    }

    private String[] loadPositions() {
        return null;
    }

}
