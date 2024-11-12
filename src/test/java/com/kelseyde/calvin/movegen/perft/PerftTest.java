package com.kelseyde.calvin.movegen.perft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.uci.UCI;
import com.kelseyde.calvin.utils.notation.FEN;
import com.kelseyde.calvin.utils.perft.PerftService;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public abstract class PerftTest {

    private static final PerftService perftService = new PerftService();

    protected abstract String getFen();
    protected abstract String getSubFolder();

    protected void perft(int depth, long expectedTotalMoves) {
        UCI.Options.chess960 = true;
        Board board = FEN.toBoard(getFen());
        Instant start = Instant.now();
        long totalMoveCount = perftService.perft(board, depth);
        long totalNodeCount = perftService.nodesSearched;
        System.out.println("totalMoveCount: " + totalNodeCount);
        Instant end = Instant.now();
        Duration performance = Duration.between(start, end);

        float nps = (float) totalNodeCount / ((float) performance.toNanos() / 1000000);
        System.out.println("nps: " + nps);
        if (expectedTotalMoves == totalMoveCount) {
            writeResults(depth, performance);
        }
        Assertions.assertEquals(expectedTotalMoves, totalMoveCount);
    }

    private void writeResults(int depth, Duration performance) {
        Instant timestamp = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        String line = String.format("%s,%s\n", timestamp, performance);
        String fileName = String.format("src/test/resources/perft/%s/perft_depth_%s.csv", getSubFolder(), depth);
        Path path = Paths.get(fileName);
        try {
            Files.writeString(path, line, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.printf("Error writing to %s! %s", fileName, e);
        }
    }

}
