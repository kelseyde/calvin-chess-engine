package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Piece;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.Arrays;

public class Accumulator {

    private static final VectorSpecies<Short> SPECIES = ShortVector.SPECIES_PREFERRED;
    private static final int HIDDEN_SIZE = NNUE.Network.HIDDEN_SIZE;
    private static final short[] WEIGHTS = NNUE.Network.NETWORK.inputWeights();

    /**
     * Two feature vectors, one from white's perspective, one from black's.
     */
    public final short[] whiteFeatures;
    public final short[] blackFeatures;
    private final int featureCount;
    private final int loopLength;

    public AccumulatorUpdate update;
    public boolean correct = true;

    public Accumulator(int featureCount) {
        this.whiteFeatures = new short[featureCount];
        this.blackFeatures = new short[featureCount];
        this.featureCount = featureCount;
        this.loopLength = SPECIES.loopBound(featureCount);
        this.update = new AccumulatorUpdate();
    }

    public Accumulator(short[] whiteFeatures, short[] blackFeatures) {
        this.whiteFeatures = whiteFeatures;
        this.blackFeatures = blackFeatures;
        this.featureCount = whiteFeatures.length;
        this.loopLength = SPECIES.loopBound(this.featureCount);
        this.update = new AccumulatorUpdate();
    }

    public void add(int wx1, int bx1) {
        add(whiteFeatures, blackFeatures, wx1, bx1);
    }

    public void addSub(int wx1, int bx1, int wx2, int bx2) {
        addSub(whiteFeatures, blackFeatures, wx1, bx1, wx2, bx2);
    }

    public void addSubSub(int wx1, int bx1, int wx2, int bx2, int wx3, int bx3) {
        addSubSub(whiteFeatures, blackFeatures, wx1, bx1, wx2, bx2, wx3, bx3);
    }

    public void addAddSubSub(int wx1, int bx1, int wx2, int bx2, int wx3, int bx3, int wx4, int bx4) {
        addAddSubSub(whiteFeatures, blackFeatures, wx1, bx1, wx2, bx2, wx3, bx3, wx4, bx4);
    }

    public void add(short[] white, short[] black, int wx1, int bx1) {
        int wOffset = wx1 * HIDDEN_SIZE;
        int bOffset = bx1 * HIDDEN_SIZE;

        for (int i = 0; i < loopLength; i += SPECIES.length()) {

            ShortVector.fromArray(SPECIES, white, i)
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset))
                    .intoArray(whiteFeatures, i);

            ShortVector.fromArray(SPECIES, black, i)
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset))
                    .intoArray(blackFeatures, i);

        }
    }

    public void addSub(short[] white, short[] black, int wx1, int bx1, int wx2, int bx2) {
        int wOffset1 = wx1 * HIDDEN_SIZE;
        int bOffset1 = bx1 * HIDDEN_SIZE;
        int wOffset2 = wx2 * HIDDEN_SIZE;
        int bOffset2 = bx2 * HIDDEN_SIZE;

        for (int i = 0; i < loopLength; i += SPECIES.length()) {

            ShortVector.fromArray(SPECIES, white, i)
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset1))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset2))
                    .intoArray(whiteFeatures, i);

            ShortVector.fromArray(SPECIES, black, i)
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset1))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset2))
                    .intoArray(blackFeatures, i);

        }
    }

    public void addSubSub(short[] white, short[] black, int wx1, int bx1, int wx2, int bx2, int wx3, int bx3) {
        int wOffset1 = wx1 * HIDDEN_SIZE;
        int bOffset1 = bx1 * HIDDEN_SIZE;
        int wOffset2 = wx2 * HIDDEN_SIZE;
        int bOffset2 = bx2 * HIDDEN_SIZE;
        int wOffset3 = wx3 * HIDDEN_SIZE;
        int bOffset3 = bx3 * HIDDEN_SIZE;

        for (int i = 0; i < loopLength; i += SPECIES.length()) {

            ShortVector.fromArray(SPECIES, white, i)
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset1))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset2))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + wOffset3))
                    .intoArray(whiteFeatures, i);

            ShortVector.fromArray(SPECIES, black, i)
                    .add(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset1))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset2))
                    .sub(ShortVector.fromArray(SPECIES, WEIGHTS, i + bOffset3))
                    .intoArray(blackFeatures, i);

        }
    }

    public void addAddSubSub(short[] white, short[] black, int wx1, int bx1, int wx2, int bx2, int wx3, int bx3, int wx4, int bx4) {
        short[] weights = NNUE.Network.NETWORK.inputWeights();
        int wOffset1 = wx1 * HIDDEN_SIZE;
        int bOffset1 = bx1 * HIDDEN_SIZE;
        int wOffset2 = wx2 * HIDDEN_SIZE;
        int bOffset2 = bx2 * HIDDEN_SIZE;
        int wOffset3 = wx3 * HIDDEN_SIZE;
        int bOffset3 = bx3 * HIDDEN_SIZE;
        int wOffset4 = wx4 * HIDDEN_SIZE;
        int bOffset4 = bx4 * HIDDEN_SIZE;

        for (int i = 0; i < loopLength; i += SPECIES.length()) {

            ShortVector.fromArray(SPECIES, white, i)
                    .add(ShortVector.fromArray(SPECIES, weights, i + wOffset1))
                    .add(ShortVector.fromArray(SPECIES, weights, i + wOffset2))
                    .sub(ShortVector.fromArray(SPECIES, weights, i + wOffset3))
                    .sub(ShortVector.fromArray(SPECIES, weights, i + wOffset4))
                    .intoArray(whiteFeatures, i);

            ShortVector.fromArray(SPECIES, black, i)
                    .add(ShortVector.fromArray(SPECIES, weights, i + bOffset1))
                    .add(ShortVector.fromArray(SPECIES, weights, i + bOffset2))
                    .sub(ShortVector.fromArray(SPECIES, weights, i + bOffset3))
                    .sub(ShortVector.fromArray(SPECIES, weights, i + bOffset4))
                    .intoArray(blackFeatures, i);

        }
    }

    public Accumulator copy() {
        return new Accumulator(
                Arrays.copyOf(whiteFeatures, whiteFeatures.length),
                Arrays.copyOf(blackFeatures, blackFeatures.length));
    }

    public record FeatureUpdate(int square, Piece piece, boolean white) {}

    /**
     * A collection of {@link FeatureUpdate} updates representing a single move. This is used to update the accumulator
     * with the changes in features that result from a move.
     */
    public static class AccumulatorUpdate {

        public FeatureUpdate[] adds = new FeatureUpdate[2];
        public int addCount = 0;

        public FeatureUpdate[] subs = new FeatureUpdate[2];
        public int subCount = 0;

        public void pushAdd(FeatureUpdate update) {
            adds[addCount++] = update;
        }

        public void pushSub(FeatureUpdate update) {
            subs[subCount++] = update;
        }

        public void pushAddSub(FeatureUpdate add, FeatureUpdate sub) {
            pushAdd(add);
            pushSub(sub);
        }

        public void pushAddSubSub(FeatureUpdate add, FeatureUpdate sub1, FeatureUpdate sub2) {
            pushAdd(add);
            pushSub(sub1);
            pushSub(sub2);
        }

        public void pushAddAddSubSub(FeatureUpdate add1, FeatureUpdate add2, FeatureUpdate sub1, FeatureUpdate sub2) {
            pushAdd(add1);
            pushAdd(add2);
            pushSub(sub1);
            pushSub(sub2);
        }

    }

}