package com.kelseyde.calvin.search;

public class Depth {

    private static final int ONE_PLY = 1024;

    private int value;

    private Depth(int value) {
        this.value = value;
    }

    public static Depth of(int value) {
        return new Depth(value * ONE_PLY);
    }

    public static Depth of(Depth depth) {
        return new Depth(depth.value);
    }

    public static Depth ofFractional(int value) {
        return new Depth(value);
    }

    public int depth() {
        return (value + ONE_PLY / 2) / ONE_PLY;
//        return value / ONE_PLY;
    }

    public int fractional() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Depth)) {
            return false;
        }
        return value == ((Depth) other).value;
    }

    public void add(Depth other) {
        value += other.value;
    }

    public void sub(Depth other) {
        value -= other.value;
    }

    public boolean gt(Depth other) {
        return value > other.value;
    }

    public boolean gte(Depth other) {
        return value >= other.value;
    }

    public boolean lt(Depth other) {
        return value < other.value;
    }

    public boolean lte(Depth other) {
        return value <= other.value;
    }

    public boolean equals(int depth) {
        return depth() == depth;
    }

    public boolean gt(int depth) {
        return depth() > depth;
    }

    public boolean gte(int depth) {
        return depth() >= depth;
    }

    public boolean lt(int depth) {
        return depth() < depth;
    }

    public boolean lte(int depth) {
        return depth() <= depth;
    }

    public Depth plus(Depth other) {
        return new Depth(value + other.value);
    }

    public Depth minus(Depth other) {
        return new Depth(value - other.value);
    }


}
