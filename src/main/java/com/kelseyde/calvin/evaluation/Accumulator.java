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
    public final boolean[] mirrored;

    public Accumulator(int featureCount) {
        this.whiteFeatures = new short[featureCount];
        this.blackFeatures = new short[featureCount];
        this.mirrored = new boolean[2];
    }

    public Accumulator(short[] whiteFeatures, short[] blackFeatures, boolean[] mirrored) {
        this.whiteFeatures = whiteFeatures;
        this.blackFeatures = blackFeatures;
        this.mirrored = mirrored;
    }

    public void reset(boolean whitePerspective) {
        // Reset the features of the accumulator to the initial bias values.
        short[] features = whitePerspective ? whiteFeatures : blackFeatures;
        System.arraycopy(BIASES, 0, features, 0, NNUE.NETWORK.hiddenSize());
    }

    public void add(short[] weights, Feature feature, boolean whitePerspective) {
        // Add a single feature to the accumulator.
        final boolean mirror = mirrored[Colour.index(whitePerspective)];
        final int offset = feature.index(whitePerspective, mirror) * HIDDEN_SIZE;
        final short[] features = whitePerspective ? whiteFeatures : blackFeatures;

        for (int i = 0; i < LOOP_LENGTH; i += SPECIES.length()) {

            ShortVector.fromArray(SPECIES, features, i)
                    .add(ShortVector.fromArray(SPECIES, weights, i + offset))
                    .intoArray(features, i);

        }
    }

    public void sub(short[] weights, Feature feature, boolean whitePerspective) {
        // Subtract a single feature from the accumulator.
        final boolean mirror = mirrored[Colour.index(whitePerspective)];
        final int offset = feature.index(whitePerspective, mirror) * HIDDEN_SIZE;
        final short[] features = whitePerspective ? whiteFeatures : blackFeatures;

        for (int i = 0; i < LOOP_LENGTH; i += SPECIES.length()) {

            ShortVector.fromArray(SPECIES, features, i)
                    .sub(ShortVector.fromArray(SPECIES, weights, i + offset))
                    .intoArray(features, i);

        }
    }

    public void apply(AccumulatorUpdate update, short[] whiteWeights, short[] blackWeights) {
        // Accumulator updates are 'fused' together, so that multiple feature updates can be applied in a single pass.
        switch (update.getUpdateType()) {
            case ADD -> add(update, whiteWeights, blackWeights);
            case ADD_SUB -> addSub(update, whiteWeights, blackWeights);
            case ADD_SUB_SUB -> addSubSub(update, whiteWeights, blackWeights);
            case ADD_ADD_SUB_SUB -> addAddSubSub(update, whiteWeights, blackWeights);
        }
    }

    public void add(AccumulatorUpdate update, short[] whiteWeights, short[] blackWeights) {

        final Feature add1 = update.adds[0];

        final boolean whiteMirror = mirrored[Colour.WHITE];
        final boolean blackMirror = mirrored[Colour.BLACK];

        final int wOffset = add1.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset = add1.index(false, blackMirror) * HIDDEN_SIZE;

        for (int i = 0; i < LOOP_LENGTH; i += SPECIES.length() * 4) {
            // Unroll 1
            ShortVector.fromArray(SPECIES, whiteFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + wOffset))
                    .intoArray(whiteFeatures, i);

            ShortVector.fromArray(SPECIES, blackFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + bOffset))
                    .intoArray(blackFeatures, i);

            // Unroll 2
            ShortVector.fromArray(SPECIES, whiteFeatures, i + SPECIES.length())
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() + wOffset))
                    .intoArray(whiteFeatures, i + SPECIES.length());

            ShortVector.fromArray(SPECIES, blackFeatures, i + SPECIES.length())
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() + bOffset))
                    .intoArray(blackFeatures, i + SPECIES.length());

            // Unroll 3
            ShortVector.fromArray(SPECIES, whiteFeatures, i + SPECIES.length() * 2)
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 2 + wOffset))
                    .intoArray(whiteFeatures, i + SPECIES.length() * 2);

            ShortVector.fromArray(SPECIES, blackFeatures, i + SPECIES.length() * 2)
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 2 + bOffset))
                    .intoArray(blackFeatures, i + SPECIES.length() * 2);

            // Unroll 4
            ShortVector.fromArray(SPECIES, whiteFeatures, i + SPECIES.length() * 3)
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 3 + wOffset))
                    .intoArray(whiteFeatures, i + SPECIES.length() * 3);

            ShortVector.fromArray(SPECIES, blackFeatures, i + SPECIES.length() * 3)
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 3 + bOffset))
                    .intoArray(blackFeatures, i + SPECIES.length() * 3);
        }
    }

    public void addSub(AccumulatorUpdate update, short[] whiteWeights, short[] blackWeights) {

        final Feature add1 = update.adds[0];
        final Feature sub1 = update.subs[0];

        final boolean whiteMirror = mirrored[Colour.WHITE];
        final boolean blackMirror = mirrored[Colour.BLACK];

        final int wOffset1 = add1.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset1 = add1.index(false, blackMirror) * HIDDEN_SIZE;
        final int wOffset2 = sub1.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset2 = sub1.index(false, blackMirror) * HIDDEN_SIZE;

        for (int i = 0; i < LOOP_LENGTH; i += SPECIES.length() * 4) {
            // Unroll 1
            ShortVector.fromArray(SPECIES, whiteFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + wOffset1))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + wOffset2))
                    .intoArray(whiteFeatures, i);

            ShortVector.fromArray(SPECIES, blackFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + bOffset1))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + bOffset2))
                    .intoArray(blackFeatures, i);

            // Unroll 2
            ShortVector.fromArray(SPECIES, whiteFeatures, i + SPECIES.length())
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() + wOffset1))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() + wOffset2))
                    .intoArray(whiteFeatures, i + SPECIES.length());

            ShortVector.fromArray(SPECIES, blackFeatures, i + SPECIES.length())
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() + bOffset1))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() + bOffset2))
                    .intoArray(blackFeatures, i + SPECIES.length());

            // Unroll 3
            ShortVector.fromArray(SPECIES, whiteFeatures, i + SPECIES.length() * 2)
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 2 + wOffset1))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 2 + wOffset2))
                    .intoArray(whiteFeatures, i + SPECIES.length() * 2);

            ShortVector.fromArray(SPECIES, blackFeatures, i + SPECIES.length() * 2)
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 2 + bOffset1))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 2 + bOffset2))
                    .intoArray(blackFeatures, i + SPECIES.length() * 2);

            // Unroll 4
            ShortVector.fromArray(SPECIES, whiteFeatures, i + SPECIES.length() * 3)
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 3 + wOffset1))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 3 + wOffset2))
                    .intoArray(whiteFeatures, i + SPECIES.length() * 3);

            ShortVector.fromArray(SPECIES, blackFeatures, i + SPECIES.length() * 3)
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 3 + bOffset1))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 3 + bOffset2))
                    .intoArray(blackFeatures, i + SPECIES.length() * 3);
        }
    }

    public void addSubSub(AccumulatorUpdate update, short[] whiteWeights, short[] blackWeights) {

        final Feature add1 = update.adds[0];
        final Feature sub1 = update.subs[0];
        final Feature sub2 = update.subs[1];

        final boolean whiteMirror = mirrored[Colour.WHITE];
        final boolean blackMirror = mirrored[Colour.BLACK];

        final int wOffset1 = add1.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset1 = add1.index(false, blackMirror) * HIDDEN_SIZE;
        final int wOffset2 = sub1.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset2 = sub1.index(false, blackMirror) * HIDDEN_SIZE;
        final int wOffset3 = sub2.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset3 = sub2.index(false, blackMirror) * HIDDEN_SIZE;

        for (int i = 0; i < LOOP_LENGTH; i += SPECIES.length() * 4) {
            // Unroll 1
            ShortVector.fromArray(SPECIES, whiteFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + wOffset1))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + wOffset2))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + wOffset3))
                    .intoArray(whiteFeatures, i);

            ShortVector.fromArray(SPECIES, blackFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + bOffset1))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + bOffset2))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + bOffset3))
                    .intoArray(blackFeatures, i);

            // Unroll 2
            ShortVector.fromArray(SPECIES, whiteFeatures, i + SPECIES.length())
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() + wOffset1))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() + wOffset2))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() + wOffset3))
                    .intoArray(whiteFeatures, i + SPECIES.length());

            ShortVector.fromArray(SPECIES, blackFeatures, i + SPECIES.length())
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() + bOffset1))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() + bOffset2))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() + bOffset3))
                    .intoArray(blackFeatures, i + SPECIES.length());

            // Unroll 3
            ShortVector.fromArray(SPECIES, whiteFeatures, i + SPECIES.length() * 2)
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 2 + wOffset1))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 2 + wOffset2))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 2 + wOffset3))
                    .intoArray(whiteFeatures, i + SPECIES.length() * 2);

            ShortVector.fromArray(SPECIES, blackFeatures, i + SPECIES.length() * 2)
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 2 + bOffset1))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 2 + bOffset2))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 2 + bOffset3))
                    .intoArray(blackFeatures, i + SPECIES.length() * 2);

            // Unroll 4
            ShortVector.fromArray(SPECIES, whiteFeatures, i + SPECIES.length() * 3)
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 3 + wOffset1))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 3 + wOffset2))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 3 + wOffset3))
                    .intoArray(whiteFeatures, i + SPECIES.length() * 3);

            ShortVector.fromArray(SPECIES, blackFeatures, i + SPECIES.length() * 3)
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 3 + bOffset1))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 3 + bOffset2))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 3 + bOffset3))
                    .intoArray(blackFeatures, i + SPECIES.length() * 3);
        }
    }

    public void addAddSubSub(AccumulatorUpdate update, short[] whiteWeights, short[] blackWeights) {

        final Feature add1 = update.adds[0];
        final Feature add2 = update.adds[1];
        final Feature sub1 = update.subs[0];
        final Feature sub2 = update.subs[1];

        final boolean whiteMirror = mirrored[Colour.WHITE];
        final boolean blackMirror = mirrored[Colour.BLACK];

        final int wOffset1 = add1.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset1 = add1.index(false, blackMirror) * HIDDEN_SIZE;
        final int wOffset2 = add2.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset2 = add2.index(false, blackMirror) * HIDDEN_SIZE;
        final int wOffset3 = sub1.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset3 = sub1.index(false, blackMirror) * HIDDEN_SIZE;
        final int wOffset4 = sub2.index(true, whiteMirror) * HIDDEN_SIZE;
        final int bOffset4 = sub2.index(false, blackMirror) * HIDDEN_SIZE;

        for (int i = 0; i < LOOP_LENGTH; i += SPECIES.length() * 4) {
            // Unroll 1
            ShortVector.fromArray(SPECIES, whiteFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + wOffset1))
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + wOffset2))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + wOffset3))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + wOffset4))
                    .intoArray(whiteFeatures, i);

            ShortVector.fromArray(SPECIES, blackFeatures, i)
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + bOffset1))
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + bOffset2))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + bOffset3))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + bOffset4))
                    .intoArray(blackFeatures, i);

            // Unroll 2
            ShortVector.fromArray(SPECIES, whiteFeatures, i + SPECIES.length())
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() + wOffset1))
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() + wOffset2))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() + wOffset3))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() + wOffset4))
                    .intoArray(whiteFeatures, i + SPECIES.length());

            ShortVector.fromArray(SPECIES, blackFeatures, i + SPECIES.length())
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() + bOffset1))
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() + bOffset2))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() + bOffset3))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() + bOffset4))
                    .intoArray(blackFeatures, i + SPECIES.length());

            // Unroll 3
            ShortVector.fromArray(SPECIES, whiteFeatures, i + SPECIES.length() * 2)
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 2 + wOffset1))
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 2 + wOffset2))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 2 + wOffset3))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 2 + wOffset4))
                    .intoArray(whiteFeatures, i + SPECIES.length() * 2);

            ShortVector.fromArray(SPECIES, blackFeatures, i + SPECIES.length() * 2)
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 2 + bOffset1))
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 2 + bOffset2))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 2 + bOffset3))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 2 + bOffset4))
                    .intoArray(blackFeatures, i + SPECIES.length() * 2);

            // Unroll 4
            ShortVector.fromArray(SPECIES, whiteFeatures, i + SPECIES.length() * 3)
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 3 + wOffset1))
                    .add(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 3 + wOffset2))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 3 + wOffset3))
                    .sub(ShortVector.fromArray(SPECIES, whiteWeights, i + SPECIES.length() * 3 + wOffset4))
                    .intoArray(whiteFeatures, i + SPECIES.length() * 3);

            ShortVector.fromArray(SPECIES, blackFeatures, i + SPECIES.length() * 3)
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 3 + bOffset1))
                    .add(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 3 + bOffset2))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 3 + bOffset3))
                    .sub(ShortVector.fromArray(SPECIES, blackWeights, i + SPECIES.length() * 3 + bOffset4))
                    .intoArray(blackFeatures, i + SPECIES.length() * 3);
        }
    }

    public Accumulator copy() {
        return new Accumulator(
                Arrays.copyOf(whiteFeatures, whiteFeatures.length),
                Arrays.copyOf(blackFeatures, blackFeatures.length),
                Arrays.copyOf(mirrored, mirrored.length));
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