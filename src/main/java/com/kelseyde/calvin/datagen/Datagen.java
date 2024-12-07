package com.kelseyde.calvin.datagen;

import com.kelseyde.calvin.datagen.dataformat.DataFormat;
import com.kelseyde.calvin.datagen.dataformat.DataFormat.DataPoint;
import com.kelseyde.calvin.datagen.dataformat.MarlinFormat;
import com.kelseyde.calvin.uci.UCICommand.DatagenCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class Datagen {

    private final DataFormat<String> formatter;

    public Datagen() {
        this.formatter = new MarlinFormat();
    }

    public void generate(DatagenCommand command) throws IOException {

        Path outputPath = Paths.get(command.file());
        if (!Files.exists(outputPath)) {
            Files.createFile(outputPath);
        }

        Instant start = Instant.now();
        int positions = 0;

        List<CompletableFuture<List<DataPoint>>> threads = initThreads(command);

        while (positions < command.positions()) {

            // Call the datagen threads to generate a batch of data
            List<DataPoint> result = threads.stream().map(CompletableFuture::join).flatMap(List::stream).toList();
            positions += result.size();

            // Convert the data to the chosen data format and write it to the output file
            String formatted = formatter.serialize(result);
            Files.writeString(outputPath, formatted, java.nio.file.StandardOpenOption.APPEND);

        }

    }

    private List<CompletableFuture<List<DataPoint>>> initThreads(DatagenCommand command) {
        return IntStream.range(0, command.threads())
                .mapToObj(i -> new DatagenThread(command))
                .map(thread -> CompletableFuture.supplyAsync(thread::run))
                .toList();
    }

}
