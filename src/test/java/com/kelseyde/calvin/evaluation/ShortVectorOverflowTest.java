package com.kelseyde.calvin.evaluation;

import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorOperators;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class ShortVectorOverflowTest {

    @Test
    public void shortVectorOverflowTest() {
        // Test that the ShortVector class does not overflow when adding two vectors
        // of shorts.
        // This test is a placeholder and should be replaced with a real test.
        short[] shorts = new short[ShortVector.SPECIES_PREFERRED.length()];
        Arrays.fill(shorts, Short.MAX_VALUE);
        ShortVector vector1 = ShortVector.fromArray(ShortVector.SPECIES_PREFERRED, shorts, 0);
        short i = vector1.reduceLanes(VectorOperators.ADD);
        System.out.println(i);

    }

}
