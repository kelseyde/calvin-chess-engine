package com.kelseyde.calvin.search;

import lombok.Data;

import java.time.Instant;

@Data
public class ThreadData {

    public boolean mainThread;
    public Instant start;
    public int nodes;
    public int depth;

    public ThreadData(boolean mainThread) {
        this.mainThread = mainThread;
        this.nodes = 0;
        this.depth = 0;
    }

    public void reset() {
        this.start = Instant.now();
        this.nodes = 0;
        this.depth = 0;
    }

}
