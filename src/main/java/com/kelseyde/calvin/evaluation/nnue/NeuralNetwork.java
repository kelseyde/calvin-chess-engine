package com.kelseyde.calvin.evaluation.nnue;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class NeuralNetwork {

    public static final int INPUT_SIZE = 768;
    public static final int LAYER_1_SIZE = 256;

    public short[] featureWeights;
    public short[] featureBiases;
    public short[] outputWeights;
    public short outputBias;

    public NeuralNetwork(String filePath) throws FileNotFoundException {

        File file = new File(filePath);
        try (Scanner scanner = new Scanner(file)) {

        }


    }

}
