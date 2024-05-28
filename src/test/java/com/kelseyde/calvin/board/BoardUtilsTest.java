package com.kelseyde.calvin.board;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.stream.IntStream;

@Disabled
public class BoardUtilsTest {

    @Test
    public void benchMarkGetFileBitboard() {

        Random random = new Random();

        Instant start = Instant.now();
        IntStream.range(0, 100000000)
                .forEach(i -> getFileBitboard(random.nextInt(0, 8)));
        System.out.println("time 1 = " + Duration.between(start, Instant.now()));

        start = Instant.now();
        IntStream.range(0, 100000000)
                .forEach(i -> getFileBitboard2(random.nextInt(0, 8)));
        System.out.println("time 2 = " + Duration.between(start, Instant.now()));

        start = Instant.now();
        IntStream.range(0, 100000000)
                .forEach(i -> getFileBitboard(random.nextInt(0, 8)));
        System.out.println("time 3 = " + Duration.between(start, Instant.now()));

        start = Instant.now();
        IntStream.range(0, 100000000)
                .forEach(i -> getFileBitboard2(random.nextInt(0, 8)));
        System.out.println("time 4 = " + Duration.between(start, Instant.now()));

        // conclusion: bit shift is faster

    }

    public static long getFileBitboard(int file) {
        return switch (file) {
            case -1 -> 0L;
            case 0 -> Bits.FILE_A;
            case 1 -> Bits.FILE_B;
            case 2 -> Bits.FILE_C;
            case 3 -> Bits.FILE_D;
            case 4 -> Bits.FILE_E;
            case 5 -> Bits.FILE_F;
            case 6 -> Bits.FILE_G;
            case 7 -> Bits.FILE_H;
            default -> throw new IllegalArgumentException("Invalid file " + file);
        };
    }

    public static long getFileBitboard2(int file) {
        return 0x0101010101010101L << file;
    }



}
