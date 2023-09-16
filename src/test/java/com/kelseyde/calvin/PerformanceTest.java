package com.kelseyde.calvin;

import com.kelseyde.calvin.model.Game;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.service.game.perft.MoveGenerationService;
import com.kelseyde.calvin.utils.MoveUtils;
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

public class PerformanceTest {

    private final MoveGenerationService moveGenerator = new MoveGenerationService();

    @Test
    public void testPerftOneDepth() {
        perft(new Game(), 1, 20);
    }

    @Test
    public void testPerftTwoDepth() {
        perft(new Game(), 2, 400);
    }

    @Test
    public void testPerftThreeDepth() {
        perft(new Game(), 3, 8902);
    }

    @Test
    public void testPerftFourDepth() {
        perft(new Game(), 4, 197281);
    }

    private void perft(Game game, int depth, int expectedTotalMoves) {
        Instant start = Instant.now();
        int totalMoveCount = moveGenerator.generateMoves(game, depth);
        Instant end = Instant.now();
        Duration performance = Duration.between(start, end);
        System.out.printf("Move generator calculated %s possible positions at depth %s in %s",
                totalMoveCount, depth, performance);
        writeResults(depth, performance);
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
        return MoveUtils.fromNotation(startSquare, endSquare);
    }


}
