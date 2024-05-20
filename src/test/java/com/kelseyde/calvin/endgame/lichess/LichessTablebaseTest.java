package com.kelseyde.calvin.endgame.lichess;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.endgame.Tablebase;
import com.kelseyde.calvin.endgame.TablebaseException;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;

@Disabled
public class LichessTablebaseTest {

    @Test
    public void testAverageResponseTime() throws IOException {

        Tablebase tablebase = TestUtils.TABLEBASE;

        int requestLimit = 500;
        int requestCount = 0;
        long totalTime = 0;
        long maxTime = Long.MIN_VALUE;
        long minTime = Long.MAX_VALUE;
        int successes = 0;
        int failures = 0;

        List<Board> endgamePositions = loadFens().stream()
                .map(FEN::toBoard)
                .filter(board -> board.countPieces() <= 7)
                .toList()
                .subList(0, requestLimit);

        System.out.println(endgamePositions.size());

        for (Board position : endgamePositions) {
            try {
                requestCount++;
                System.out.println("Sending request " + requestCount);
                Instant start = Instant.now();
                tablebase.getTablebaseMove(position);
                successes++;
                long millis = Duration.between(start, Instant.now()).toMillis();
                totalTime += millis;
                if (millis > maxTime) maxTime = millis;
                if (millis < minTime) minTime = millis;
                Thread.sleep(new Random().nextLong(100, 400));
            } catch (TablebaseException | InterruptedException e) {
                failures++;
            }
        }

        long averageTime = totalTime / successes;

        System.out.println("Report:");
        System.out.println("Total requests: " + requestLimit);
        System.out.println("Successful requests: " + successes);
        System.out.println("Failed requests: " + failures);
        System.out.println("Average request time: " + averageTime);
        System.out.println("Maximum request time: " + maxTime);
        System.out.println("Minimum request time: " + minTime);

    }

    private List<String> loadFens() throws IOException {
        String fileName = "src/test/resources/texel/quiet_positions.epd";
        Path path = Paths.get(fileName);
        return Files.readAllLines(path);
    }

}