package com.kelseyde.calvin.search;

public class Depth {

    // Quantization factor for fractional depth
    public static final int ONE_PLY = 1024;

    private Depth() {}

    public static int toInt(int depth) {
        return depth / ONE_PLY;
    }

    public static int toFractional(int depth) {
        return depth * ONE_PLY;
    }

}
