package com.kelseyde.calvin.utils.train;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.Score;
import com.kelseyde.calvin.utils.notation.FEN;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

public class DataDebugger {

    private static final int BATCH_SIZE = 10000;

    private int count = 0;
    private int filtered = 0;
    private final List<String> batch = new ArrayList<>();

    public void score() throws IOException {

        Path inputPath = Paths.get("/Users/kelseyde/git/dan/calvin/data/calvindata_12.txt");
        Path outputPath = Paths.get("/Users/kelseyde/git/dan/calvin/data/calvindata_12_filtered.txt");
        if (!Files.exists(outputPath)) {
            Files.createFile(outputPath);
        }

        try (Stream<String> lines = Files.lines(inputPath)) {

            Iterator<String> iterator = lines.iterator();
            while (iterator.hasNext()) {

                count++;
                if (count % 1000000 == 0) {
                    System.out.printf("count: %d, filtered: %d\n", count, filtered);
                }

                String line = iterator.next();
                String[] parts = line.split("\\|");
                String fen = parts[0].trim();
                int score = Integer.parseInt(parts[1].trim());
                String result = parts[2].trim();

                boolean isTooHigh = Math.abs(score) >= 10000;
                boolean isBadResult = (score >= 1000 && result.equalsIgnoreCase("0.0"))
                        || (score <= -1000 && result.equalsIgnoreCase("1.0"));

                if (isTooHigh || isBadResult) {
                    filtered++;
                    continue;
                }

                batch.add(line);

                if (batch.size() == BATCH_SIZE) {
                    Files.write(outputPath, batch, StandardOpenOption.APPEND);
                    batch.clear();
                }


            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to read input file", e);
        }

        System.out.println("total positions: " + count);
        System.out.printf("filtered positions: %d\n", filtered);

    }

    public static void main(String[] args) throws IOException {
        DataDebugger dataDebugger = new DataDebugger();
        dataDebugger.score();
    }

}
