package com.kelseyde.calvin.datagen;

import com.kelseyde.calvin.uci.UCICommand;

public record DatagenCommand(String file,
                             int positions,
                             int threads,
                             int softNodes,
                             int hardNodes,
                             int minPlies,
                             int maxPlies,
                             int maxInitialScore,
                             int maxScore,
                             int adjudicateScore,
                             int adjudicateMoves) {

    public static DatagenCommand parse(UCICommand command) {
        String file                 = command.getString("file", null, true);
        int positions               = command.getInt("positions", 100000000, false);
        int threads                 = command.getInt("threads", 1, false);
        int softNodes               = command.getInt("softNodes", 5000, false);
        int hardNodes               = command.getInt("hardNodes", 100000, false);
        int minPlies                = command.getInt("minPlies", 8, false);
        int maxPlies                = command.getInt("maxPlies", 9, false);
        int maxInitialScore         = command.getInt("maxInitialScore", 1200, false);
        int maxScore                = command.getInt("maxScore", 6000, false);
        int adjudicateScore         = command.getInt("adjudicateScore", 2500, false);
        int adjudicateMoves         = command.getInt("adjudicateMoves", 5, false);

        return new DatagenCommand(
                file, positions, threads, softNodes, hardNodes, minPlies, maxPlies,
                maxInitialScore, maxScore, adjudicateScore, adjudicateMoves
        );
    }

}
