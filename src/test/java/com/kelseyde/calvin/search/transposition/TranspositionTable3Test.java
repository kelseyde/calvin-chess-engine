package com.kelseyde.calvin.search.transposition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TranspositionTable3Test {

    @Test
    public void testPlayground() {
//
//        System.out.println(log2(-1000000));
//        System.out.println(log2(1000000));
//        System.out.println(log2(2500));
//        System.out.println(log2(-2500));
//        System.out.println(log2(256));
//        System.out.println(log2(0xff)); //8
//        System.out.println(log2(32767)); //8
//        System.out.println(log2(32767)); //8

        System.out.println(Long.toHexString(0b0000000000000000000000000000000011111111111111111111111111111111L));

    }

    int log2(int value) {
        return Integer.SIZE - Integer.numberOfLeadingZeros(value);
    }

}