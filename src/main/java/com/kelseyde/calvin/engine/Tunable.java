package com.kelseyde.calvin.engine;

public class Tunable {
    public final String name;
    public int value;
    public final int min;
    public final int max;
    public final int step;

    public Tunable(String name, int value, int min, int max, int step) {
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public String toUCI() {
        return String.format("option name %s type spin default %d min %d max %d", name, value, min, max);
    }

    public String toSPSA() {
        final float spsaStep = (float) Math.max(0.5, Math.round((float) (max - min) / 20));
        final float learningRate = 0.002f;

        return String.format("%s, int, %s, %s, %s, %s, %s", name, value, min, max, spsaStep, learningRate);
    }

}
