package com.kelseyde.calvin.uci.command;

public record DatagenCommand(String outputFile,
                             OutputFormat outputFormat,
                             int numPositions,
                             int numThreads,
                             int softNodeLimit,
                             int hardNodeLimit) {

    private static final int DEFAULT_SOFT_NODES = 5000;
    private static final int DEFAULT_HARD_NODES = 1000000;
    private static final int DEFAULT_THREADS = 20;

    public enum OutputFormat {
        MARLIN_FORMAT
    }

}
