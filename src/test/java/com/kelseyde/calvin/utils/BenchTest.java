package com.kelseyde.calvin.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BenchTest {

    @Test
    public void testBench() {
        Bench.run(TestUtils.ENGINE);
    }

}