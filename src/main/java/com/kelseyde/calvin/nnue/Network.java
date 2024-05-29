package com.kelseyde.calvin.nnue;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

import javax.sound.sampled.Line;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * This class serves as a work-in-progress playground where I jot down all my notes related to neural networks and NNUE.
 * There will come a day when it turns into a fully-fledged NNUE to relace my hand-crafted evaluation function.
 * But not this day...
 */
public class Network {

    /*

    *** WHAT IS NNUE? ***

    NNUE (ƎUИИ Efficiently Updatable Neural Network) is a neural network architecture that takes advantage of the fact
    that, in games like chess, a single move does not change the state of the board that much. Therefore, minimal changes
    are required to update the network inputs between evaluations.



     */

    /**
     * M represents the number of features in the first layer of the network. The value is 64 * 64 * 5 * 2 = 40960.
     */
    private static final int M = 40960;

    private Board board;

    private Accumulator[] accumulators;

    private LinearLayer L_0;
    private float[] C_0;
    private LinearLayer L_1;
    private float[] C_1;
    private LinearLayer L_2;


    public void makeMove(Move move) {

        int startSquare = move.getStartSquare();
        boolean white = board.isWhiteToMove();
        boolean refreshRequired = board.pieceAt(startSquare) == Piece.KING;
        int us = colourIndex(white);
        int them = colourIndex(!white);

        if (refreshRequired) {
            this.accumulators[us] = refreshAccumulator(L_0, getActiveFeatures(), white);
        } else {
            this.accumulators[us] = updateAccumulator(L_0, this.accumulators[us], getRemovedFeatures(), getAddedFeatures(), white);
        }
        this.accumulators[them] = updateAccumulator(L_0, this.accumulators[them], getRemovedFeatures(), getAddedFeatures(), false);

    }

    public float evaluate() {

        float[] buffer = new float[M];

        // We need to prepare the input first! We will put the accumulator for
        // the side to move first, and the other second.
        float[] input = new float[2 * M];
        boolean white = board.isWhiteToMove();
        int us = colourIndex(white);
        int them = colourIndex(!white);

        for (int i = 0; i < M; ++i) {
            input[    i] = this.accumulators[us].features(true)[i];
            input[M + i] = this.accumulators[them].features(false)[i];
        }

        float[] curr_output = buffer;
        float[] curr_input = input;
        float[] next_output;

        // Evaluate one layer and move both input and output forward.
        // The last output becomes the next input.
        next_output = crelu(2 * L_0.outputsSize, curr_input);
        curr_input = curr_output;
        curr_output = next_output;

        next_output = linear(L_1, curr_input);
        curr_input = curr_output;
        curr_output = next_output;

        next_output = crelu(L_1.outputsSize, curr_input);
        curr_input = curr_output;
        curr_output = next_output;

        float final_output = linear(L_2, curr_input)[0];

        // We're done. The last layer should have put 1 value out under *curr_output.
        return final_output;

    }


    private static Accumulator refreshAccumulator(LinearLayer layer,
                                                  int[] activeFeatures,
                                                  boolean white) {

        Accumulator accumulator = new Accumulator();
        int colour = colourIndex(white);

        // First we copy the layer bias, that's our starting point
        for (int i = 0; i < M; i++) {
            accumulator.features[colour][i] += layer.biases[i];
        }

        // Then we just accumulate all the columns for the active features. That's what accumulators do!
        for (int a : activeFeatures) {
            for (int i = 0; i < M; i++) {
                accumulator.features[colour][i] += layer.weights[a][i];
            }
        }

        return accumulator;
    }

    private static Accumulator updateAccumulator(LinearLayer layer,
                                                 Accumulator previousAccumulator,
                                                 int[] removedFeatures,
                                                 int[] addedFeatures,
                                                 boolean white) {

        Accumulator accumulator = new Accumulator();
        int colour = colourIndex(white);

        // First we copy the previous values, that's our starting point
        System.arraycopy(previousAccumulator.features[colour], 0, accumulator.features[colour], 0, M);

        // Then we subtract the weights of the removed features
        for (int r : removedFeatures) {
            for (int i = 0; i < M; i++) {
                // Just subtract r-th column
                accumulator.features[colour][i] -= layer.weights[r][i];
            }
        }

        // Similar for the added features, but add instead of subtracting
        for (int a : addedFeatures) {
            for (int i = 0; i < M; i++) {
                accumulator.features[colour][i] += layer.weights[a][i];
            }
        }

        return accumulator;
    }

    private int[] getActiveFeatures() {
        return new int[1];
    }

    private int[] getAddedFeatures() {
        return new int[1];
    }

    private int[] getRemovedFeatures() {
        return new int[1];
    }

    private float[] linear(LinearLayer layer, float[] input) {

        float[] outputs = new float[layer.outputsSize];

        // First copy the biases to the output. We will be adding columns on top of it.
        System.arraycopy(layer.biases, 0, outputs, 0, layer.outputsSize);

        for (int i = 0; i < layer.inputSize; ++i) {
            for (int j = 0; j < layer.outputsSize; ++j) {
                outputs[j] += input[i] * layer.weights[i][j];
            }
        }

        return outputs;
    }

    float[] crelu(int size, float[] input) {

        float[] outputs = new float[size];

        for (int i = 0; i < size; ++i) {
            outputs[i] = min(max(input[i], 0), 1);
        }

        return outputs;

    }


    private static int featureIndex(int square, Piece piece, int kingSquare, boolean white) {
        int colourIndex = colourIndex(white);
        int pieceIndex = pieceIndex(piece);
        int simpleIndex = pieceIndex * 2 + colourIndex;
        return square + (simpleIndex + kingSquare * 10) * 64;
    }

    private static int pieceIndex(Piece piece) {
        return switch (piece) {
            case PAWN -> 1;
            case KNIGHT -> 2;
            case BISHOP -> 3;
            case ROOK -> 4;
            case QUEEN -> 5;
            case KING -> 0;
        };
    }

    private static int colourIndex(boolean white) {
        return white ? 0 : 1;
    }

    public static class Accumulator {

        /**
         * Two feature vectors, one from white's perspective, one from black's.
         */
        public float[][] features;

        public Accumulator() {
            this.features = new float[2][M];
        }

        public float[] features(boolean white) {
            int colourIndex = Network.colourIndex(white);
            return features[colourIndex];
        }

    }

    public static class LinearLayer {

        public float[][] weights;
        public float[] biases;

        public int inputSize;
        public int outputsSize;

    }

}
