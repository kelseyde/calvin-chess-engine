package com.kelseyde.calvin.search.thread;

import lombok.Getter;

public class ThreadManager {

    private static final int MAX_DEPTH = 256;

    @Getter
    private final int threadCount;

    private final boolean skipEnabled;

    private int[] threadDepths;

    private int maxDepthReached;

    public ThreadManager(int threadCount, boolean skipEnabled) {
        this.threadCount = threadCount;
        this.skipEnabled = skipEnabled;
        this.threadDepths = new int[threadCount];
        this.maxDepthReached = 0;
    }

    public synchronized void clear() {
        this.threadDepths = new int[threadDepths.length];
        this.maxDepthReached = 0;
    }

    public synchronized int selectDepth(int threadId) {

        int currentDepth = threadDepths[threadId];
        int newDepth = Math.max(maxDepthReached - 1, currentDepth + 1);
        int numberOfThreads = threadDepths.length;
        boolean isMainThread = threadId == 0;

        if (!isMainThread && skipEnabled) {
            for (int depth = newDepth; depth < MAX_DEPTH; depth++) {
                int otherThreadsAtDepth = countOtherThreadsAtDepth(threadId, depth);
                boolean shouldSkip = otherThreadsAtDepth > (numberOfThreads / 2);
                if (!shouldSkip) {
                    newDepth = depth;
                    break;
                }
            }
        }
        threadDepths[threadId] = newDepth;
        if (newDepth > maxDepthReached) maxDepthReached = newDepth;
        return newDepth;

    }

    private int countOtherThreadsAtDepth(int threadId, int depth) {
        int count = 0;
        for (int i = 0; i < threadDepths.length; i++) {
            if (i != threadId && threadDepths[i] >= depth) {
                count++;
            }
        }
        return count;
    }

}
