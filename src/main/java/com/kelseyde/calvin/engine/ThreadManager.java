package com.kelseyde.calvin.engine;

import java.util.Arrays;

public class ThreadManager {

    public int[] threadDepths;

    private final Object syncObject = new Object();

    public ThreadManager(int threadCount) {
        threadDepths = new int[threadCount];
        for (int i = 0; i < threadCount - 1; i++) {
            threadDepths[i] = 1;
        }
    }

    public int selectNewDepth(int threadId) {
            int currentDepth = threadDepths[threadId];
            System.out.printf("thread %s current depth %s%n", threadId, currentDepth);
            int newDepth = currentDepth + 1;
            int threadsAtSameDepth = countThreadsAtDepth(newDepth);
            boolean halfOfThreadsAtDepth = threadsAtSameDepth - 1 >= threadDepths.length / 2;
            if (halfOfThreadsAtDepth) {
                newDepth++;
            }

//            int newDepth = currentDepth;
//            int increment = 1;
//            boolean foundDepth = false;
//            while (!foundDepth) {
//                newDepth = currentDepth + increment;
//                int threadsAtSameDepth = countThreadsAtDepth(newDepth);
//                boolean halfOfThreadsAtDepth = threadsAtSameDepth - 1 >= threadDepths.length / 2;
//                //System.out.printf("thread %s depth %s others %s / %s%n", threadId, newDepth, threadsAtSameDepth, threadDepths.length);
//                if (halfOfThreadsAtDepth) {
//                    increment++;
//                } else {
//                    foundDepth = true;
//                }
//            }
            threadDepths[threadId] = newDepth;
            System.out.printf("thread %s new depth %s%n", threadId, newDepth);
            return newDepth;

    }

    private int countThreadsAtDepth(int depth) {
        int count = 0;
        for (int i = 0; i < threadDepths.length; i++) {
            if (threadDepths[depth] == depth) count++;
        }
        return count;
    }

}
