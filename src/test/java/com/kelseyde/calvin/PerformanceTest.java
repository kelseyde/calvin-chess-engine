package com.kelseyde.calvin;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.movegeneration.perft.PerftService;
import com.kelseyde.calvin.utils.NotationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Disabled
public class PerformanceTest {

    private final PerftService perftService = new PerftService();

    @Test
    public void testPerftOneDepth() {
        perft(new Board(), 1, 20);
    }

    @Test
    public void testPerftTwoDepth() {
        perft(new Board(), 2, 400);
    }

    @Test
    public void testPerftThreeDepth() {
        perft(new Board(), 3, 8902);
    }

    @Test
    public void testPerftFourDepth() {
        perft(new Board(), 4, 197281);
    }

    @Test
    public void testPerftFiveDepth() {
        perft(new Board(), 5, 4865609);
    }

    private void perft(Board board, int depth, int expectedTotalMoves) {
        Instant start = Instant.now();
        int totalMoveCount = perftService.perft(board, depth);
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
        String fileName = String.format("src/test/resources/perft/perft_depth_%s.csv", depth);
        Path path = Paths.get(fileName);
        try {
            Files.writeString(path, line, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.printf("Error writing to %s! %s", fileName, e);
        }
    }

    private Move move(String startSquare, String endSquare) {
        return NotationUtils.fromNotation(startSquare, endSquare);
    }


}
