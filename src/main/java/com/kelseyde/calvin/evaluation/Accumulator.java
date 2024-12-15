package com.kelseyde.calvin.evaluation;

import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.Arrays;

public class Accumulator {

    private static final VectorSpecies<Short> SPECIES = ShortVector.SPECIES_PREFERRED;
    private static final int HIDDEN_SIZE = NNUE.NETWORK.hiddenSize();
    private static final short[] WEIGHTS = NNUE.NETWORK.inputWeights();
    private static final short[] BIASES = NNUE.NETWORK.inputBiases();

    /**
     * Two feature vectors, one from white's perspective, one from black's.
     */
    public final short[] whiteFeatures;
    public final short[] blackFeatures;
    private final int featureCount;
    private final int loopLength;

    public Accumulator(int featureCount) {
        this.whiteFeatures = new short[featureCount];
        this.blackFeatures = new short[featureCount];
        this.featureCount = featureCount;
        this.loopLength = SPECIES.loopBound(featureCount);
    }

    public Accumulator(short[] whiteFeatures, short[] blackFeatures) {
        this.whiteFeatures = whiteFeatures;
        this.blackFeatures = blackFeatures;
        this.featureCount = whiteFeatures.length;
        this.loopLength = SPECIES.loopBound(this.featureCount);
    }

    public void reset(boolean whitePerspective) {
        short[] features = whitePerspective ? whiteFeatures : blackFeatures;

        for (int i = 0; i < SPECIES.loopBound(HIDDEN_SIZE); i += SPECIES.length()) {
            ShortVector.fromArray(SPECIES, BIASES, i).intoArray(features, i);
        }
    }

    public void add(Feature feature, boolean whitePerspective, boolean mirror) {
        final int offset = feature.index(whitePerspective, mirror) * HIDDEN_SIZE;
        final short[] features = whitePerspective ? whiteFeatures : blackFeatures;

        for (int i = 0; i < loopLength; i += SPECIES.length()) {

            ShortVector.fromArray(SPECIES, features, i)
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + offset))
                    .intoArray(features, i);

        }
    }

    public void apply(AccumulatorUpdate update, boolean whiteMirror, boolean blackMirror) {
        switch (update.getUpdateType()) {
            case ADD -> add(update, whiteMirror, blackMirror);
            case ADD_SUB -> addSub(update, whiteMirror, blackMirror);
            case ADD_SUB_SUB -> addSubSub(update, whiteMirror, blackMirror);
            case ADD_ADD_SUB_SUB -> addAddSubSub(update, whiteMirror, blackMirror);
        }
    }

    public void add(AccumulatorUpdate update, boolean whiteMirror, boolean blackMirror) {

        final Feature add1 = update.adds[0];
        final int wOffset = add1.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset = add1.index(false, blackMirror) * HIDDEN_SIZE;

        for (int i = 0; i < loopLength; i += SPECIES.length()) {

            ShortVector.fromArray(SPECIES, whiteFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset))
                    .intoArray(whiteFeatures, i);

            ShortVector.fromArray(SPECIES, blackFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset))
                    .intoArray(blackFeatures, i);

        }
    }

    public void addSub(AccumulatorUpdate update, boolean whiteMirror, boolean blackMirror) {

        final Feature add1 = update.adds[0];
        final Feature sub1 = update.subs[0];

        final int wOffset1 = add1.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset1 = add1.index(false, blackMirror) * HIDDEN_SIZE;
        final int wOffset2 = sub1.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset2 = sub1.index(false, blackMirror) * HIDDEN_SIZE;

        for (int i = 0; i < loopLength; i += SPECIES.length()) {

            ShortVector.fromArray(SPECIES, whiteFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset1))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset2))
                    .intoArray(whiteFeatures, i);

            ShortVector.fromArray(SPECIES, blackFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset1))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset2))
                    .intoArray(blackFeatures, i);

        }
    }

    public void addSubSub(AccumulatorUpdate update, boolean whiteMirror, boolean blackMirror) {

        final Feature add1 = update.adds[0];
        final Feature sub1 = update.subs[0];
        final Feature sub2 = update.subs[1];

        final int wOffset1 = add1.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset1 = add1.index(false, blackMirror) * HIDDEN_SIZE;
        final int wOffset2 = sub1.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset2 = sub1.index(false, blackMirror) * HIDDEN_SIZE;
        final int wOffset3 = sub2.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset3 = sub2.index(false, blackMirror) * HIDDEN_SIZE;

        for (int i = 0; i < loopLength; i += SPECIES.length()) {

            ShortVector.fromArray(SPECIES, whiteFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset1))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset2))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset3))
                    .intoArray(whiteFeatures, i);

            ShortVector.fromArray(SPECIES, blackFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset1))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset2))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset3))
                    .intoArray(blackFeatures, i);

        }
    }

    public void addAddSubSub(AccumulatorUpdate update, boolean whiteMirror, boolean blackMirror) {

        final Feature add1 = update.adds[0];
        final Feature add2 = update.adds[1];
        final Feature sub1 = update.subs[0];
        final Feature sub2 = update.subs[1];

        final int wOffset1 = add1.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset1 = add1.index(false, blackMirror) * HIDDEN_SIZE;
        final int wOffset2 = add2.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset2 = add2.index(false, blackMirror) * HIDDEN_SIZE;
        final int wOffset3 = sub1.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset3 = sub1.index(false, blackMirror) * HIDDEN_SIZE;
        final int wOffset4 = sub2.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset4 = sub2.index(false, blackMirror) * HIDDEN_SIZE;

        for (int i = 0; i < loopLength; i += SPECIES.length()) {

            ShortVector.fromArray(SPECIES, whiteFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset1))
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset2))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset3))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset4))
                    .intoArray(whiteFeatures, i);

            ShortVector.fromArray(SPECIES, blackFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset1))
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset2))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset3))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset4))
                    .intoArray(blackFeatures, i);

        }
    }

    public Accumulator copy() {
        return new Accumulator(
                Arrays.copyOf(whiteFeatures, whiteFeatures.length),
                Arrays.copyOf(blackFeatures, blackFeatures.length));
    }

    public static class AccumulatorUpdate {

        public AccumulatorUpdate() {}

        public Feature[] adds = new Feature[2];
        public Feature[] subs = new Feature[2];

        public int addCount = 0;
        public int subCount = 0;

        public void pushAdd(Feature add) {
            adds[addCount++] = add;
        }

        public void pushSub(Feature sub) {
            subs[subCount++] = sub;
        }

        public void addSub(Feature add, Feature sub) {
            pushAdd(add);
            pushSub(sub);
        }

        public void addSubSub(Feature add, Feature sub1, Feature sub2) {
            pushAdd(add);
            pushSub(sub1);
            pushSub(sub2);
        }

        public void addAddSubSub(Feature add1, Feature add2, Feature sub1, Feature sub2) {
            pushAdd(add1);
            pushAdd(add2);
            pushSub(sub1);
            pushSub(sub2);
        }

        public UpdateType getUpdateType() {
            if (addCount == 1 && subCount == 0) {
                return UpdateType.ADD;
            }
            else if (addCount == 1 && subCount == 1) {
                return UpdateType.ADD_SUB;
            }
            else if (addCount == 1 && subCount == 2) {
                return UpdateType.ADD_SUB_SUB;
            }
            else if (addCount == 2 && subCount == 2) {
                return UpdateType.ADD_ADD_SUB_SUB;
            }
            else {
                throw new IllegalStateException("Unexpected update type");
            }
        }

    }

    public enum UpdateType {
        ADD,
        ADD_SUB,
        ADD_SUB_SUB,
        ADD_ADD_SUB_SUB
    }

}