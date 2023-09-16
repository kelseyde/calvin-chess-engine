package com.kelseyde.calvin;

import com.kelseyde.calvin.model.Game;
import com.kelseyde.calvin.service.game.perft.MoveGenerationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

public class PerformanceTest {

    private final MoveGenerationService moveGenerator = new MoveGenerationService();

    @Test
    public void testPerftOneDepth() {
        perft(1, 20);
    }

    @Test
//    @Disabled
    public void testPerftTwoDepth() {
        perft(2, 400);
    }

    @Test
    @Disabled
    public void testPerftThreeDepth() {
        perft(3, 8902);
    }

    private void perft(int depth, int expectedTotalMoves) {
        Instant start = Instant.now();
        int totalMoveCount = moveGenerator.generateMoves(new Game(), depth);
        Instant end = Instant.now();
        System.out.printf("Move generator calculated %s possible positions at depth %s in %s.%n",
                totalMoveCount, depth, Duration.between(start, end));
        Assertions.assertEquals(expectedTotalMoves, totalMoveCount);
    }

}
