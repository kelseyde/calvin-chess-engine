package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Piece;

/**
 * Whenever the king changes bucket, a costly full refresh of the accumulator is required. This service implements a
 * technique to improve the performance of this refresh known as 'Finny tables'.
 * </p>
 * We keep a cache of the last accumulator and board state used for each bucket. When refreshing, instead of starting
 * from an empty board, we start from the last board state used for the bucket. We therefore only need to apply the diff
 * between the last board state and the current board state to the accumulator.
 */
public class InputBucketCache {

    public static final class BucketCacheEntry {

        // Each piece bitboard (0-5) and the two sides' bitboards (6-7)
        public long[] bitboards = new long[Piece.COUNT + 2];

        // The cached accumulator last used for this bucket
        public short[] features;

    }

    private final BucketCacheEntry[][][] cache;

    public InputBucketCache(int bucketCount) {
        cache = new BucketCacheEntry[2][2][bucketCount];

        for (int whitePerspective = 0; whitePerspective < 2; whitePerspective++) {
            for (int mirror = 0; mirror < 2; mirror++) {
                for (int i = 0; i < cache[0][0].length; i++) {
                    cache[whitePerspective][mirror][i] = new BucketCacheEntry();
                }
            }
        }
    }

    public BucketCacheEntry get(boolean whitePerspective, boolean mirror, int bucket) {
        return cache[whitePerspective ? 0 : 1][mirror ? 0 : 1][bucket];
    }

}
