package com.kelseyde.calvin.datagen;

import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.uci.command.DatagenCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DataGenerator {

    private List<Searcher> searchers;

    public void generate(DatagenCommand command) {

        String file = command.outputFile();
        int numPositions = command.numPositions();
        int numThreads = command.numThreads();
        int softNodeLimit = command.softNodeLimit();
        int hardNodeLimit = command.hardNodeLimit();

        System.out.printf(
                """
                Beginning data generation
                File Path        : %s
                Positions        : %d
                Threads          : %d
                Soft Nodes       : %d
                Hard Nodes       : %d
                """,
                file, numPositions, numThreads, softNodeLimit, hardNodeLimit);

        Path outputPath = Paths.get(file);
        if (!Files.exists(outputPath)) try {
            Files.createFile(outputPath);
        } catch (IOException e) {
            UCI.writeError("Encountered error during data generation: ", e);
        }

    }

}
