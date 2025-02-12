package com.kelseyde.calvin.datagen;

import com.kelseyde.calvin.datagen.dataformat.DataFormat;
import com.kelseyde.calvin.datagen.dataformat.DataFormat.DataPoint;
import com.kelseyde.calvin.datagen.dataformat.PlainFormat;
import com.kelseyde.calvin.uci.UCI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Calvin generates data to train its neural network by playing many games itself, and recording the positions, scores,
 * and results of those games. This class is responsible for orchestrating the data generation process.
 */
public class Datagen {

    private final DataFormat<String> formatter;
    private final DatagenReporter reporter;
    private final ExecutorService executorService;

    private List<DatagenThread> datagenThreads;

    public Datagen() {
        this.formatter = new PlainFormat();
        this.reporter = new DatagenReporter();
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void generate(DatagenCommand command) {

        UCI.Options.output = false;

        reporter.reportDatagenInfo(command);
        datagenThreads = IntStream.range(0, command.threads())
                .mapToObj(i -> new DatagenThread(command))
                .toList();

        try {

            // Initialise the output file, creating a new file if it doesn't exist.
            Path outputPath = Paths.get(command.file());
            if (!Files.exists(outputPath)) {
                Files.createFile(outputPath);
            }

            Instant start = Instant.now();
            int positions = 0;

            while (positions < command.positions()) {

                // Call the datagen threads to generate a batch of data
                List<CompletableFuture<List<DataPoint>>> threads = datagenThreads.stream()
                        .map(thread -> CompletableFuture.supplyAsync(thread::run, executorService))
                        .toList();
                List<DataPoint> result = threads.stream().map(CompletableFuture::join).flatMap(List::stream).toList();
                positions += result.size();

                // Convert the data to the chosen data format and write it to the output file
                String formatted = formatter.serialize(result);
                Files.writeString(outputPath, formatted, java.nio.file.StandardOpenOption.APPEND);

                // Report the progress of the data generation.
                reporter.reportDatagenProgress(command, start, positions);

            }

        } catch (IOException e) {
            UCI.writeError("Error during datagen!", e);
        }

        UCI.Options.output = true;

    }


}
