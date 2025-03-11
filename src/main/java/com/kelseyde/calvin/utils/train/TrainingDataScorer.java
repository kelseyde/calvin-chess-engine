package com.kelseyde.calvin.utils.train;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.*;
import com.kelseyde.calvin.tables.tt.TranspositionTable;
import com.kelseyde.calvin.uci.Pretty;
import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.uci.UCICommand.ScoreDataCommand;
import com.kelseyde.calvin.utils.notation.FEN;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TrainingDataScorer {

    private static final int THREAD_COUNT = 20;
    private static final int THREAD_TIMEOUT_SECONDS = 15;
    private static final int BATCH_SIZE = THREAD_COUNT * 500;
    private static final int TT_SIZE = 64;
    private static final int TOTAL_POSITIONS_PER_FILE = 100000000;
    private static final Duration MAX_SEARCH_TIME = Duration.ofSeconds(30);
    private static final MoveGenerator MOVE_GENERATOR = new MoveGenerator();
    private static final EngineConfig ENGINE_CONFIG = new EngineConfig();

    private List<Searcher> searchers;

    public void score(ScoreDataCommand command) {

        logDatascoreInfo(command);

        Path inputPath = Paths.get(command.inputFile());
        Path outputPath = Paths.get(command.outputFile());
        UCI.setOutputEnabled(false);
        searchers = IntStream.range(0, THREAD_COUNT)
                .mapToObj(this::initSearcher)
                .toList();
        Instant start = Instant.now();

        try (Stream<String> lines = Files.lines(inputPath)) {
            if (!Files.exists(outputPath)) Files.createFile(outputPath);
            int count = 0;
            AtomicInteger scored = new AtomicInteger(0);
            AtomicInteger excluded = new AtomicInteger(0);
            List<String> batch = new ArrayList<>(BATCH_SIZE);
            Iterator<String> iterator = lines.iterator();
            while (iterator.hasNext()) {
                if (count++ < command.resumeOffset()) {
                    iterator.next();
                    continue;
                }
                if (scored.get() > 0 && batch.isEmpty()) {
                    logProgress(start, command, scored, excluded);
                }
                String line = iterator.next();
                batch.add(line);
                if (batch.size() == BATCH_SIZE) {
                    List<String> scoredBatch = processBatch(batch, command);
                    excluded.addAndGet(batch.size() - scoredBatch.size());
                    batch.clear();
                    scored.addAndGet(scoredBatch.size());
                    searchers.forEach(Searcher::clearHistory);
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

        UCI.setOutputEnabled(true);
    }

    private List<String> processBatch(List<String> positions, ScoreDataCommand command) {
        List<List<String>> partitions = partitionBatch(positions);
        List<Future<List<String>>> futures = new ArrayList<>(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            Searcher searcher = searchers.get(i);
            List<String> partition = partitions.get(i);
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    List<String> scoredPartition = new ArrayList<>(partition.size());
                    for (String line : partition) {
                        String scoredLine = scoreData(searcher, line, command);
                        if (!scoredLine.isEmpty()) {
                            scoredPartition.add(scoredLine);
                        }
                    }
                    return scoredPartition;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to score partition " + Arrays.toString(e.getStackTrace()) + e.getCause() + e.getMessage(), e);
                }
            }));
        }
        List<String> scoredBatch = new ArrayList<>(positions.size());
        for (Future<List<String>> future : futures) {
            try {
                scoredBatch.addAll(future.get(THREAD_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            } catch (Exception e) {
                throw new RuntimeException("Failed to score batch " + Arrays.toString(e.getStackTrace()) + e.getCause() + e.getMessage(), e);
            }
        }
        return scoredBatch;
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


    private String scoreData(Searcher searcher, String line, ScoreDataCommand command) {
        String[] parts = line.split("\\|");
        String fen = parts[0].trim();
        int score = Integer.parseInt(parts[1].trim());
        String result = parts[2].trim();
        boolean isTooHigh = Math.abs(score) >= 10000;
        boolean isBadResult = (score >= 1000 && result.equalsIgnoreCase("0.0"))
                || (score <= -1000 && result.equalsIgnoreCase("1.0"));

        if (isTooHigh || isBadResult) {
            return "";
        }

        Board board = FEN.toBoard(fen);
        if (MOVE_GENERATOR.isCheck(board, board.isWhite())) {
            // Filter out positions where the side to move is in check
            return "";
        }
        searcher.setPosition(board);
        TimeControl tc = new TimeControl(ENGINE_CONFIG, Instant.now(), MAX_SEARCH_TIME, MAX_SEARCH_TIME, command.softNodes(), command.hardNodes(), -1);
        SearchResult searchResult;
        try {
             searchResult = searcher.search(tc);
        } catch (Exception e) {
            System.out.println("info error scoring fen " + fen + " " + e);
            return "";
        }
        Move bestMove = searchResult.move();
        if (bestMove == null) {
            // Filter out positions where there is no best move
            return "";
        }
        boolean isCapture = board.pieceAt(bestMove.to()) != null;
        if (isCapture) {
            // Filter out positions where the best move is a capture
            return "";
        }
        score = searchResult.eval();
        if (Score.isMate(score)) {
            // Filter out positions where there is forced mate
            return "";
        }
        isTooHigh = Math.abs(score) >= 10000;
        isBadResult = (score >= 1000 && result.equalsIgnoreCase("0.0"))
                || (score <= -1000 && result.equalsIgnoreCase("1.0"));

        if (isTooHigh || isBadResult) {
            return "";
        }
        if (!board.isWhite()) score = -score;
        return String.format("%s | %s | %s", fen, score, result);
    }

    private Searcher initSearcher(int i) {
        EngineConfig config = new EngineConfig();
        TranspositionTable transpositionTable = new TranspositionTable(TT_SIZE);
        return new Searcher(config, transpositionTable, new ThreadData(i == 0));
    }

    private void logDatascoreInfo(ScoreDataCommand command) {
        if (UCI.Options.pretty) {
            UCI.write("");
            UCI.write(String.format("""
                    %sBeginning Data Scoring%s
                    Input File    : %s%s%s
                    Output File   : %s%s%s
                    Soft Limit    : %s%d%s
                    Hard Limit    : %s%d%s
                    Resume Offset : %s%d%s
                    """,
                    Pretty.BLUE, Pretty.RESET, Pretty.GREEN, command.inputFile(), Pretty.RESET, Pretty.GREEN, command.outputFile(), Pretty.RESET,
                    Pretty.RED, command.softNodes(), Pretty.RESET, Pretty.RED, command.hardNodes(), Pretty.RESET, Pretty.RED, command.resumeOffset(), Pretty.RESET)
                    );
        } else {
            UCI.write(String.format("Scoring training data from %s to %s, soft limit %d, hard limit %d, resume offset %d\n",
                    command.inputFile(), command.outputFile(), command.softNodes(), command.hardNodes(), command.resumeOffset()));
        }
    }

    private void logProgress(Instant start, ScoreDataCommand command, AtomicInteger scored, AtomicInteger excluded) {
        Duration duration = Duration.between(start, Instant.now()).truncatedTo(ChronoUnit.SECONDS);
        int total = scored.get() + excluded.get() + command.resumeOffset();
        int totalSinceResume = total - command.resumeOffset();
        int remaining = TOTAL_POSITIONS_PER_FILE - total;
        double rate = (double) totalSinceResume / duration.getSeconds();
        String rateFormatted = String.format("%.0f", rate);
        Duration estimate = Duration.ofSeconds((long) (remaining / rate)).truncatedTo(ChronoUnit.SECONDS);

        if (UCI.Options.pretty) {
            System.out.printf("total %s%d%s, since resume %s%d%s, scored %s%d%s, excluded %s%d%s, time %s%s%s, pos/s %s%s%s, remaining pos %s%s%s remaining time %s%s%s\n",
                    Pretty.CYAN, total, Pretty.RESET,
                    Pretty.CYAN, totalSinceResume, Pretty.RESET,
                    Pretty.CYAN, scored.get(), Pretty.RESET,
                    Pretty.CYAN, excluded.get(), Pretty.RESET,
                    Pretty.CYAN, duration, Pretty.RESET,
                    Pretty.CYAN, rateFormatted, Pretty.RESET,
                    Pretty.CYAN, remaining, Pretty.RESET,
                    Pretty.CYAN, estimate, Pretty.RESET);
        } else {
            System.out.printf("total %d, since resume %d, scored %d, excluded %d, time %s, pos/s %s, remaining pos %s remaining time %s\n",
                    total, totalSinceResume, scored.get(), excluded.get(), duration, rateFormatted, remaining, estimate);
        }
    }

}
