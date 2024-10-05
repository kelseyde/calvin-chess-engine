package com.kelseyde.calvin.uci.command;

import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.uci.UCICommand;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public record ScoreDataCommand(String inputFile, String outputFile, int softNodes, int hardNodes, int resumeOffset) {

    private static final int DEFAULT_SOFT_NODES = 5000;
    private static final int DEFAULT_HARD_NODES = 1000000;
    private static final int DEFAULT_RESUME_OFFSET = 0;

    public static Optional<ScoreDataCommand> parse(UCICommand command) {
        String inputFile = command.getString("input", null, true);
        String outputFile = command.getString("output", null, true);

        if (!Files.exists(Path.of(inputFile))) {
            UCI.write("info error input file " + inputFile + " does not exist");
            return Optional.empty();
        }

        int softNodes = command.getInt("soft", DEFAULT_SOFT_NODES, false);
        int hardNodes = command.getInt("hard", DEFAULT_HARD_NODES, false);
        int resume = command.getInt("resume", DEFAULT_RESUME_OFFSET, false);

        return Optional.of(new ScoreDataCommand(inputFile, outputFile, softNodes, hardNodes, resume));
    }

}
