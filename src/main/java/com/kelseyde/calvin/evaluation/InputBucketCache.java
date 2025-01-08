package com.kelseyde.calvin.evaluation;

import java.util.Objects;

public class InputBucketCache {

    public static final class BucketCacheEntry {
        public long[] bitboards;
        public Accumulator accumulator;
    }

    private final BucketCacheEntry[][] cache;

    public InputBucketCache(int bucketCount) {
        cache = new BucketCacheEntry[2][bucketCount];
        for (int mirror = 0; mirror < 2; mirror++) {
            for (int i = 0; i < bucketCount; i++) {
                cache[mirror][i] = new BucketCacheEntry();
            }
        }
    }

    public BucketCacheEntry get(int bucket, boolean mirror) {
        return cache[mirror ? 1 : 0][bucket];
    }

}
