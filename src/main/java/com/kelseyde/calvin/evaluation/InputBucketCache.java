package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Piece;

import java.util.Objects;

public class InputBucketCache {

    public static final class BucketCacheEntry {

        // Each piece bitboard (0-5) and the two sides' bitboards (6-7)
        public final long[] bitboards = new long[Piece.COUNT + 2];

        // The cached accumulator last used for this bucket
        public short[] features;

    }

    private final BucketCacheEntry[][][] cache;

    public InputBucketCache(int bucketCount) {
        cache = new BucketCacheEntry[2][2][bucketCount];
        for (int whitePerspective = 0; whitePerspective < 2; whitePerspective++) {
            for (int mirror = 0; mirror < 2; mirror++) {
                for (int i = 0; i < bucketCount; i++) {
                    cache[whitePerspective][mirror][i] = new BucketCacheEntry();
                }
            }
        }
    }

    public BucketCacheEntry get(boolean whitePerspective, boolean mirror, int bucket) {
        return cache[whitePerspective ? 0 : 1][mirror ? 0 : 1][bucket];
    }

}
