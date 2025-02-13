package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Colour;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.Arrays;

/**
 * The accumulator keeps track of the activations of the hidden layer of the neural network. It is incrementally updated
 * during search to avoid recomputing the entire network each time evaluation is called. The activations are accumulated
 * from both white's and black's perspective, so that during evaluation the 'side to move' and 'not side to move' can be
 * easily flipped.
 * </p>
 * The Java Vector API is used to give the accumulator updates a performance boost via SIMD instructions.
 */
public class Accumulator {

    private static final int HIDDEN_SIZE = NNUE.NETWORK.hiddenSize();
    private static final short[] BIASES = NNUE.NETWORK.inputBiases();
    private static final VectorSpecies<Short> SPECIES = ShortVector.SPECIES_PREFERRED;
    private static final int LOOP_LENGTH = SPECIES.loopBound(HIDDEN_SIZE);

    public short[] whiteFeatures;
    public short[] blackFeatures;
    public final boolean[] needsRefresh;
    public final boolean[] computed;
    public AccumulatorUpdate update;

    public Accumulator(int featureCount) {
        this.whiteFeatures = new short[featureCount];
        this.blackFeatures = new short[featureCount];
        this.needsRefresh = new boolean[] {true, true};
        this.computed = new boolean[] {false, false};
    }

    public Accumulator(short[] whiteFeatures, short[] blackFeatures, boolean[] needsRefresh, boolean[] computed) {
        this.whiteFeatures = whiteFeatures;
        this.blackFeatures = blackFeatures;
        this.needsRefresh = needsRefresh;
        this.computed = computed;
    }

    public void reset(boolean whitePerspective) {
        // Reset the features of the accumulator to the initial bias values.
        short[] features = whitePerspective ? whiteFeatures : blackFeatures;
        System.arraycopy(BIASES, 0, features, 0, NNUE.NETWORK.hiddenSize());
    }

    public void add(short[] weights, Feature feature, boolean whitePerspective, boolean mirror) {
        // Add a single feature to the accumulator.
        final int offset = feature.index(whitePerspective, mirror) * HIDDEN_SIZE;
        final short[] features = whitePerspective ? whiteFeatures : blackFeatures;

        for (int i = 0; i < LOOP_LENGTH; i += SPECIES.length()) {

            ShortVector.fromArray(SPECIES, features, i)
                    .add(ShortVector.fromArray(SPECIES, weights, i + offset))
                    .intoArray(features, i);

        }
    }

    public void sub(short[] weights, Feature feature, boolean whitePerspective, boolean mirror) {
        // Subtract a single feature from the accumulator.
        final int offset = feature.index(whitePerspective, mirror) * HIDDEN_SIZE;
        final short[] features = whitePerspective ? whiteFeatures : blackFeatures;

        for (int i = 0; i < LOOP_LENGTH; i += SPECIES.length()) {

            ShortVector.fromArray(SPECIES, features, i)
                    .sub(ShortVector.fromArray(SPECIES, weights, i + offset))
                    .intoArray(features, i);

        }
    }

    public void apply(AccumulatorUpdate update, short[] weights, boolean white) {
        // Accumulator updates are 'fused' together, so that multiple feature updates can be applied in a single pass.
        switch (update.getUpdateType()) {
            case ADD -> add(update, weights, white);
            case ADD_SUB -> addSub(update, weights, white);
            case ADD_SUB_SUB -> addSubSub(update, weights, white);
            case ADD_ADD_SUB_SUB -> addAddSubSub(update, weights, white);
        }
    }

    public void add(AccumulatorUpdate update, short[] weights, boolean white) {

        final Feature add1 = update.adds[0];

        final boolean mirror = update.mirrored[Colour.index(white)];
        final int offset = add1.index(white, mirror) * HIDDEN_SIZE;
        final short[] features = white ? whiteFeatures : blackFeatures;

        for (int i = 0; i < LOOP_LENGTH; i += SPECIES.length()) {
            ShortVector.fromArray(SPECIES, features, i)
                    .add(ShortVector.fromArray(SPECIES, weights, i + offset))
                    .intoArray(features, i);
        }
    }

    public void addSub(AccumulatorUpdate update, short[] weights, boolean white) {

        final Feature add1 = update.adds[0];
        final Feature sub1 = update.subs[0];

        final boolean mirror = update.mirrored[Colour.index(white)];
        final int offset1 = add1.index(white, mirror) * HIDDEN_SIZE;
        final int offset2 = sub1.index(white, mirror) * HIDDEN_SIZE;
        final short[] features = white ? whiteFeatures : blackFeatures;

        for (int i = 0; i < LOOP_LENGTH; i += SPECIES.length()) {
            ShortVector.fromArray(SPECIES, features, i)
                    .add(ShortVector.fromArray(SPECIES, weights, i + offset1))
                    .sub(ShortVector.fromArray(SPECIES, weights, i + offset2))
                    .intoArray(features, i);
        }
    }

    public void addSubSub(AccumulatorUpdate update, short[] weights, boolean white) {

        final Feature add1 = update.adds[0];
        final Feature sub1 = update.subs[0];
        final Feature sub2 = update.subs[1];

        final boolean mirror = update.mirrored[Colour.index(white)];
        final int offset1 = add1.index(white, mirror) * HIDDEN_SIZE;
        final int offset2 = sub1.index(white, mirror) * HIDDEN_SIZE;
        final int offset3 = sub2.index(white, mirror) * HIDDEN_SIZE;
        final short[] features = white ? whiteFeatures : blackFeatures;

        for (int i = 0; i < LOOP_LENGTH; i += SPECIES.length()) {
            ShortVector.fromArray(SPECIES, features, i)
                    .add(ShortVector.fromArray(SPECIES, weights, i + offset1))
                    .sub(ShortVector.fromArray(SPECIES, weights, i + offset2))
                    .sub(ShortVector.fromArray(SPECIES, weights, i + offset3))
                    .intoArray(features, i);
        }
    }

    public void addAddSubSub(AccumulatorUpdate update, short[] weights, boolean white) {

        final Feature add1 = update.adds[0];
        final Feature add2 = update.adds[1];
        final Feature sub1 = update.subs[0];
        final Feature sub2 = update.subs[1];

        final boolean mirror = update.mirrored[Colour.index(white)];

        final int offset1 = add1.index(white, mirror) * HIDDEN_SIZE;
        final int offset2 = add2.index(white, mirror) * HIDDEN_SIZE;
        final int offset3 = sub1.index(white, mirror) * HIDDEN_SIZE;
        final int offset4 = sub2.index(white, mirror) * HIDDEN_SIZE;

        final short[] features = white ? whiteFeatures : blackFeatures;

        for (int i = 0; i < LOOP_LENGTH; i += SPECIES.length()) {
            ShortVector.fromArray(SPECIES, features, i)
                    .add(ShortVector.fromArray(SPECIES, weights, i + offset1))
                    .add(ShortVector.fromArray(SPECIES, weights, i + offset2))
                    .sub(ShortVector.fromArray(SPECIES, weights, i + offset3))
                    .sub(ShortVector.fromArray(SPECIES, weights, i + offset4))
                    .intoArray(features, i);
        }
    }

    public Accumulator copy() {
        return new Accumulator(
                Arrays.copyOf(whiteFeatures, whiteFeatures.length),
                Arrays.copyOf(blackFeatures, blackFeatures.length),
                Arrays.copyOf(needsRefresh, needsRefresh.length),
                Arrays.copyOf(computed, computed.length)
        );
    }

    public void copyFrom(short[] features, boolean whitePerspective) {
        if (whitePerspective) {
            whiteFeatures = Arrays.copyOf(features, features.length);
        } else {
            blackFeatures = Arrays.copyOf(features, features.length);
        }
    }

    public static class AccumulatorUpdate {

        public final Feature[] adds = new Feature[2];
        public final Feature[] subs = new Feature[2];
        public final boolean[] mirrored = new boolean[2];
        public final int[] kingBucket = new int[2];

        public int addCount = 0;
        public int subCount = 0;

        public void pushAdd(Feature add) {
            adds[addCount++] = add;
        }

        public void pushSub(Feature sub) {
            subs[subCount++] = sub;
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