package com.kelseyde.calvin.search;

public class Depth {

    private static final int FRACTIONAL = 1024;

    private int value;

    private Depth(int value) {
        this.value = value;
    }

    public static Depth of(int value) {
        return new Depth(value * FRACTIONAL);
    }

    public static Depth ofFractional(int value) {
        return new Depth(value);
    }

    public int depth() {
        return (value + FRACTIONAL / 2) / FRACTIONAL;
    }

    public int fractional() {
        return value;
    }

}
