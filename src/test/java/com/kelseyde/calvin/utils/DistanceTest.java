package com.kelseyde.calvin.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DistanceTest {

    @Test
    public void testChebyshev() {

        Assertions.assertEquals(7, Distance.chebyshev(0, 63));
        Assertions.assertEquals(4, Distance.chebyshev(18, 52));
        Assertions.assertEquals(7, Distance.chebyshev(57, 6));
        Assertions.assertEquals(0, Distance.chebyshev(27, 27));
        Assertions.assertEquals(7, Distance.chebyshev(40, 47));
        Assertions.assertEquals(1, Distance.chebyshev(22, 15));

    }

    @Test
    public void testManhattan() {

        Assertions.assertEquals(14, Distance.manhattan(0, 63));
        Assertions.assertEquals(6, Distance.manhattan(18, 52));
        Assertions.assertEquals(12, Distance.manhattan(57, 6));
        Assertions.assertEquals(0, Distance.manhattan(27, 27));
        Assertions.assertEquals(7, Distance.manhattan(40, 47));
        Assertions.assertEquals(2, Distance.manhattan(22, 15));

    }

    @Test
    public void testCenterManhattan() {

        Assertions.assertEquals(0, Distance.centerManhattan(27));
        Assertions.assertEquals(0, Distance.centerManhattan(28));
        Assertions.assertEquals(0, Distance.centerManhattan(35));
        Assertions.assertEquals(0, Distance.centerManhattan(36));
        Assertions.assertEquals(1, Distance.centerManhattan(34));
        Assertions.assertEquals(6, Distance.centerManhattan(0));
        Assertions.assertEquals(4, Distance.centerManhattan(23));
        Assertions.assertEquals(3, Distance.centerManhattan(3));

    }

}