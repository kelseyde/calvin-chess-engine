package com.kelseyde.calvin.generation.mperft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.perft.MPerftService;
import com.kelseyde.calvin.utils.FEN;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public abstract class MPerftTest {

    private static final MPerftService mPerftService = new MPerftService();

    protected abstract String getFen();

    protected abstract String getSubFolder();

    protected void perft(int depth, int expectedTotalMoves) {
        Board board = FEN.toBoard(getFen());
        Instant start = Instant.now();
        int totalMoveCount = mPerftService.perft(board, depth);
        Instant end = Instant.now();
        Duration performance = Duration.between(start, end);
        if (expectedTotalMoves == totalMoveCount) {
            writeResults(depth, performance);
        }
        Assertions.assertEquals(expectedTotalMoves, totalMoveCount);
    }

    private void writeResults(int depth, Duration performance) {
        Instant timestamp = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        String line = String.format("%s,%s\n", timestamp, performance);
        String fileName = String.format("src/test/resources/mperft/%s/mperft_depth_%s.csv", getSubFolder(), depth);
        Path path = Paths.get(fileName);
        try {
            Files.writeString(path, line, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.printf("Error writing to %s! %s", fileName, e);
        }
    }

}
