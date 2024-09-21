package com.kelseyde.calvin.movegen;

import com.kelseyde.calvin.board.Bits.Ray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RayBetweenTest {
    
    @Test
    public void testInvalidRays() {

        Assertions.assertEquals(0L, Ray.between(0, 17));
        Assertions.assertEquals(0L, Ray.between(42, 15));
        Assertions.assertEquals(0L, Ray.between(31, 57));
        Assertions.assertEquals(0L, Ray.between(3, 18));
        Assertions.assertEquals(0L, Ray.between(16, 30));

    }

    @Test
    public void testSameSquare() {

        Assertions.assertEquals(0L, Ray.between(18, 18));

    }

    @Test
    public void testAdjacentSquare() {

        Assertions.assertEquals(0L, Ray.between(28, 36));
        Assertions.assertEquals(0L, Ray.between(28, 37));
        Assertions.assertEquals(0L, Ray.between(28, 29));
        Assertions.assertEquals(0L, Ray.between(28, 21));
        Assertions.assertEquals(0L, Ray.between(28, 20));
        Assertions.assertEquals(0L, Ray.between(28, 19));
        Assertions.assertEquals(0L, Ray.between(28, 27));
        Assertions.assertEquals(0L, Ray.between(28, 35));

    }

    @Test
    public void testPositiveRankOrthogonalRays() {

        long ray = Ray.between(0, 2);
        long expectedRay = 1L << 1;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(33, 38);
        expectedRay = 0L;
        expectedRay |= 1L << 34;
        expectedRay |= 1L << 35;
        expectedRay |= 1L << 36;
        expectedRay |= 1L << 37;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(12, 15);
        expectedRay = 0L;
        expectedRay |= 1L << 13;
        expectedRay |= 1L << 14;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(40, 47);
        expectedRay = 0L;
        expectedRay |= 1L << 41;
        expectedRay |= 1L << 42;
        expectedRay |= 1L << 43;
        expectedRay |= 1L << 44;
        expectedRay |= 1L << 45;
        expectedRay |= 1L << 46;
        Assertions.assertEquals(expectedRay, ray);

    }

    @Test
    public void testNegativeRankOrthogonalRays() {

        long ray = Ray.between(63, 61);
        long expectedRay = 0L;
        expectedRay |= 1L << 62;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(7, 0);
        expectedRay = 0L;
        expectedRay |= 1L << 6;
        expectedRay |= 1L << 5;
        expectedRay |= 1L << 4;
        expectedRay |= 1L << 3;
        expectedRay |= 1L << 2;
        expectedRay |= 1L << 1;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(30, 25);
        expectedRay = 0L;
        expectedRay |= 1L << 29;
        expectedRay |= 1L << 28;
        expectedRay |= 1L << 27;
        expectedRay |= 1L << 26;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(23, 18);
        expectedRay = 0L;
        expectedRay |= 1L << 22;
        expectedRay |= 1L << 21;
        expectedRay |= 1L << 20;
        expectedRay |= 1L << 19;
        Assertions.assertEquals(expectedRay, ray);

    }

    @Test
    public void testPositiveFileOrthogonalRays() {

        long ray = Ray.between(9, 25);
        long expectedRay = 0L;
        expectedRay |= 1L << 17;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(3, 59);
        expectedRay = 0L;
        expectedRay |= 1L << 11;
        expectedRay |= 1L << 19;
        expectedRay |= 1L << 27;
        expectedRay |= 1L << 35;
        expectedRay |= 1L << 43;
        expectedRay |= 1L << 51;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(15, 39);
        expectedRay = 0L;
        expectedRay |= 1L << 23;
        expectedRay |= 1L << 31;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(24, 56);
        expectedRay = 0L;
        expectedRay |= 1L << 32;
        expectedRay |= 1L << 40;
        expectedRay |= 1L << 48;
        Assertions.assertEquals(expectedRay, ray);

    }

    @Test
    public void testNegativeFileOrthogonalRays() {

        long ray = Ray.between(25, 9);
        long expectedRay = 0L;
        expectedRay |= 1L << 17;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(59, 3);
        expectedRay = 0L;
        expectedRay |= 1L << 11;
        expectedRay |= 1L << 19;
        expectedRay |= 1L << 27;
        expectedRay |= 1L << 35;
        expectedRay |= 1L << 43;
        expectedRay |= 1L << 51;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(39, 15);
        expectedRay = 0L;
        expectedRay |= 1L << 23;
        expectedRay |= 1L << 31;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(56, 24);
        expectedRay = 0L;
        expectedRay |= 1L << 32;
        expectedRay |= 1L << 40;
        expectedRay |= 1L << 48;
        Assertions.assertEquals(expectedRay, ray);

    }

    @Test
    public void testPositiveAntiDiagonalRays() {

        long ray = Ray.between(0, 63);
        long expectedRay = 0L;
        expectedRay |= 1L << 9;
        expectedRay |= 1L << 18;
        expectedRay |= 1L << 27;
        expectedRay |= 1L << 36;
        expectedRay |= 1L << 45;
        expectedRay |= 1L << 54;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(40, 58);
        expectedRay = 0L;
        expectedRay |= 1L << 49;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(12, 39);
        expectedRay = 0L;
        expectedRay |= 1L << 21;
        expectedRay |= 1L << 30;
        Assertions.assertEquals(expectedRay, ray);

    }

    @Test
    public void testNegativeAntiDiagonalRays() {

        long ray = Ray.between(63, 0);
        long expectedRay = 0L;
        expectedRay |= 1L << 9;
        expectedRay |= 1L << 18;
        expectedRay |= 1L << 27;
        expectedRay |= 1L << 36;
        expectedRay |= 1L << 45;
        expectedRay |= 1L << 54;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(58, 40);
        expectedRay = 0L;
        expectedRay |= 1L << 49;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(39, 12);
        expectedRay = 0L;
        expectedRay |= 1L << 21;
        expectedRay |= 1L << 30;
        Assertions.assertEquals(expectedRay, ray);

    }

    @Test
    public void testPositiveDiagonalRays() {

        long ray = Ray.between(56, 7);
        long expectedRay = 0L;
        expectedRay |= 1L << 49;
        expectedRay |= 1L << 42;
        expectedRay |= 1L << 35;
        expectedRay |= 1L << 28;
        expectedRay |= 1L << 21;
        expectedRay |= 1L << 14;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(53, 39);
        expectedRay = 0L;
        expectedRay |= 1L << 46;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(32, 4);
        expectedRay = 0L;
        expectedRay |= 1L << 25;
        expectedRay |= 1L << 18;
        expectedRay |= 1L << 11;
        Assertions.assertEquals(expectedRay, ray);

    }

    @Test
    public void testNegativeDiagonalRays() {

        long ray = Ray.between(7, 56);
        long expectedRay = 0L;
        expectedRay |= 1L << 49;
        expectedRay |= 1L << 42;
        expectedRay |= 1L << 35;
        expectedRay |= 1L << 28;
        expectedRay |= 1L << 21;
        expectedRay |= 1L << 14;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(39, 53);
        expectedRay = 0L;
        expectedRay |= 1L << 46;
        Assertions.assertEquals(expectedRay, ray);

        ray = Ray.between(4, 32);
        expectedRay = 0L;
        expectedRay |= 1L << 25;
        expectedRay |= 1L << 18;
        expectedRay |= 1L << 11;
        Assertions.assertEquals(expectedRay, ray);

    }

}