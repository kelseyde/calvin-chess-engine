package com.kelseyde.calvin.utils.train;

import com.kelseyde.calvin.Application;
import com.kelseyde.calvin.evaluation.Score;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class TrainingDataFilterer {

    private static final int THREAD_COUNT = 20;
    private static final int BATCH_SIZE = THREAD_COUNT * 1000;

    public void filter(String inputFile, String outputFile) {

        System.out.printf("Filtering training data from %s to %s\n", inputFile, outputFile);
        Path inputPath = Paths.get(inputFile);
        Path outputPath = Paths.get(outputFile);
        Application.outputEnabled = false;
        Instant start = Instant.now();

        try (Stream<String> lines = Files.lines(inputPath)) {
            if (!Files.exists(outputPath)) Files.createFile(outputPath);
            AtomicInteger scored = new AtomicInteger(0);
            List<String> batch = new ArrayList<>(BATCH_SIZE);
            Iterator<String> iterator = lines.iterator();
            while (iterator.hasNext()) {
                if (scored.get() > 0 && batch.isEmpty()) {
                    Duration duration = Duration.between(start, Instant.now());
                    System.out.println("info string progress: scored " + scored + " positions, total time " + duration);
                }
                String line = iterator.next();
                batch.add(line);
                if (batch.size() == BATCH_SIZE) {
                    List<String> scoredBatch = processBatch(batch);
                    batch.clear();
                    scored.addAndGet(scoredBatch.size());
                    try {
                        Files.write(outputPath, scoredBatch, StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to write output file", e);
                    }
                }

            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read input file", e);
        }

    }

    private List<String> processBatch(List<String> positions) {
        List<List<String>> partitions = partitionBatch(positions);
        List<Future<List<String>>> futures = new ArrayList<>(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            List<String> partition = partitions.get(i);
            futures.add(CompletableFuture.supplyAsync(() -> {
                List<String> filteredPartitions = new ArrayList<>(partition.size());
                for (String line : partition) {
                    filterData(line).ifPresent(filteredPartitions::add);
                }
                return filteredPartitions;
            }));
        }
        List<String> filteredBatch = new ArrayList<>(positions.size());
        for (Future<List<String>> future : futures) {
            try {
                filteredBatch.addAll(future.get());
            } catch (Exception e) {
                throw new RuntimeException("Failed to score batch " + Arrays.toString(e.getStackTrace()) + e.getCause() + e.getMessage(), e);
            }
        }
        return filteredBatch;
    }

    private List<List<String>> partitionBatch(List<String> batch) {
        List<List<String>> partitions = new ArrayList<>(THREAD_COUNT);
        int partitionSize = batch.size() / THREAD_COUNT;
        for (int i = 0; i < THREAD_COUNT; i++) {
            int start = i * partitionSize;
            int end = (i + 1) * partitionSize;
            if (i == THREAD_COUNT - 1) {
                end = batch.size();
            }
            partitions.add(batch.subList(start, end));
        }
        return partitions;
    }


    private Optional<String> filterData(String line) {
        String[] parts = line.split("\\|");
        int score = Integer.parseInt(parts[1].trim());
        return Score.isMateScore(score) ? Optional.empty() : Optional.of(line);
    }

}
