package com.kelseyde.calvin.utils.train;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.evaluation.Score;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.search.ThreadData;
import com.kelseyde.calvin.search.TimeControl;
import com.kelseyde.calvin.tables.tt.TranspositionTable;
import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.uci.UCICommand;
import com.kelseyde.calvin.utils.notation.FEN;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DataDebugger {

    private static final int THREAD_COUNT = 20;
    private static final int THREAD_TIMEOUT_SECONDS = 15;
    private static final int BATCH_SIZE = THREAD_COUNT * 1000;
    private static final int TT_SIZE = 64;
    private static final int TOTAL_POSITIONS_PER_FILE = 100000000;
    private static final Duration MAX_SEARCH_TIME = Duration.ofSeconds(30);
    private static final MoveGenerator MOVE_GENERATOR = new MoveGenerator();

    private int count = 0;
    private int maxScore = Integer.MIN_VALUE;
    private int minScore = Integer.MAX_VALUE;
    private long totalScore = 0;

    //private final Set<Long> keys = new HashSet<>();

    private int wonScores = 0;

    public void score() {

        Path inputPath = Paths.get("/Users/kelseyde/git/dan/calvin/data/calvindata_6.txt");

        try (Stream<String> lines = Files.lines(inputPath)) {

            Iterator<String> iterator = lines.iterator();
            while (iterator.hasNext()) {

                count++;
                if (count % 50000 == 0) {
                    System.out.printf("count: %d\n", count);
                }

                String line = iterator.next();
                String[] parts = line.split("\\|");
                int score = Integer.parseInt(parts[1].trim());
                totalScore += score;
                if (score > maxScore) {
                    maxScore = score;
                }
                if (score < minScore) {
                    minScore = score;
                }
                if (Math.abs(score) > 7500) {
                    wonScores++;
                }
            }

            //System.out.printf("duplicate keys: %d\n", count - keys.size());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read input file", e);
        }

        System.out.println("total positions: " + count);

    }

    public static void main(String[] args) {
        DataDebugger dataDebugger = new DataDebugger();
        dataDebugger.score();
    }

}
