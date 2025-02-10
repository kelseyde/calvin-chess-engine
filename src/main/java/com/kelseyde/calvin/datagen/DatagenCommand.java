package com.kelseyde.calvin.datagen;

import com.kelseyde.calvin.uci.UCICommand;

public record DatagenCommand(String file,
                             int positions,
                             int threads,
                             int batchSize,
                             int softNodes,
                             int hardNodes,
                             int minPlies,
                             int maxPlies,
                             int maxGameLength,
                             int winThreshold,
                             int winPliesThreshold,
                             int drawThreshold,
                             int drawPliesThreshold,
                             int initialScoreThreshold) {

    public static DatagenCommand parse(UCICommand command) {
        String file                 = command.getString("file", null, true);
        int positions               = command.getInt("positions", 100000000, false);
        int threads                 = command.getInt("threads", 1, false);
        int batchSize               = command.getInt("batchSize", 16384, false);
        int softNodes               = command.getInt("softNodes", 5000, false);
        int hardNodes               = command.getInt("hardNodes", 1000000, false);
        int minPlies                = command.getInt("minPlies", 8, false);
        int maxPlies                = command.getInt("maxPlies", 9, false);
        int maxGameLength           = command.getInt("maxGameLength", 100, false);
        int winThreshold            = command.getInt("winThreshold", 2500, false);
        int winPliesThreshold       = command.getInt("winPliesThreshold", 5, false);
        int drawThreshold           = command.getInt("drawThreshold", 2, false);
        int drawPliesThreshold      = command.getInt("drawPliesThreshold", 8, false);
        int initialScoreThreshold   = command.getInt("initialScoreThreshold", 300, false);
        return new DatagenCommand(
                file, positions, threads, batchSize, softNodes, hardNodes, minPlies, maxPlies, maxGameLength,
                winThreshold, winPliesThreshold, drawThreshold, drawPliesThreshold, initialScoreThreshold
        );
    }

}
