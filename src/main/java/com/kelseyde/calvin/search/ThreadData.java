package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;

import java.time.Instant;

public class ThreadData {

    public boolean mainThread;
    public Instant start;
    public int nodes;
    public int[][] nodesPerMove;
    public int depth;

    public ThreadData(boolean mainThread) {
        this.mainThread = mainThread;
        this.nodes = 0;
        this.nodesPerMove = new int[64][64];
        this.depth = 1;
    }

    public void addNodes(Move move, int nodes) {
        nodesPerMove[move.from()][move.to()] += nodes;
    }

    public int getNodes(Move move) {
        if (move == null) return 0;
        return nodesPerMove[move.from()][move.to()];
    }

    public boolean isMainThread() {
        return mainThread;
    }

    public void reset() {
        this.start = Instant.now();
        this.nodes = 0;
        this.nodesPerMove = new int[64][64];
        this.depth = 1;
    }

}
