package com.kelseyde.calvin;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.movegeneration.perft.PerftService;
import com.kelseyde.calvin.utils.NotationUtils;
import com.kelseyde.calvin.utils.fen.FEN;
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

    private static final String STARTING_POSITION = "starting_position";
    private static final String KIWIPETE_FOLDER = "kiwipete";
    private static final String KIWIPETE_FEN = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ";

    private final PerftService perftService = new PerftService();

    @Test
    public void testPerftOneDepth() {
        perft(STARTING_POSITION, new Board(), 1, 20);
    }

    @Test
    public void testPerftTwoDepth() {
        perft(STARTING_POSITION, new Board(), 2, 400);
    }

    @Test
    public void testPerftThreeDepth() {
        perft(STARTING_POSITION, new Board(), 3, 8902);
    }

    @Test
    public void testPerftFourDepth() {
        perft(STARTING_POSITION, new Board(), 4, 197281);
    }

    @Test
    public void testPerftFiveDepth() {
        perft(STARTING_POSITION, new Board(), 5, 4865609);
    }

    @Test
    public void testPerftSixDepth() {
        perft(STARTING_POSITION, new Board(), 6, 119060324);
    }

    @Test
    public void testKiwipeteOneDepth() {
        perft(KIWIPETE_FOLDER, FEN.fromFEN(KIWIPETE_FEN), 1, 48);
    }

    @Test
    public void testKiwipeteTwoDepth() {
        perft(KIWIPETE_FOLDER, FEN.fromFEN(KIWIPETE_FEN), 2, 2039);
    }

    @Test
    public void testKiwipeteThreeDepth() {
        perft(KIWIPETE_FOLDER, FEN.fromFEN(KIWIPETE_FEN), 3, 97862);
    }

    @Test
    public void testKiwipeteFourDepth() {
        perft(KIWIPETE_FOLDER, FEN.fromFEN(KIWIPETE_FEN), 4, 4085603);
    }

    @Test
    public void testKiwipeteFiveDepth() {
        perft(KIWIPETE_FOLDER, FEN.fromFEN(KIWIPETE_FEN), 5, 193690690);
    }

    private void perft(String subfolder, Board board, int depth, int expectedTotalMoves) {
        Instant start = Instant.now();
        int totalMoveCount = perftService.perft(board, depth);
        Instant end = Instant.now();
        Duration performance = Duration.between(start, end);
        if (expectedTotalMoves == totalMoveCount) {
            writeResults(subfolder, depth, performance);
        }
        Assertions.assertEquals(expectedTotalMoves, totalMoveCount);
    }

    private void writeResults(String subfolder, int depth, Duration performance) {
        Instant timestamp = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        String line = String.format("%s,%s\n", timestamp, performance);
        String fileName = String.format("src/test/resources/perft/%s/perft_depth_%s.csv", subfolder, depth);
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
