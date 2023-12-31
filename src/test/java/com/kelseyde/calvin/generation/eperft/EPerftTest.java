package com.kelseyde.calvin.generation.eperft;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.tuning.perft.EPerftService;
import com.kelseyde.calvin.utils.notation.FEN;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public abstract class EPerftTest {

    protected abstract String getFen();
    protected abstract String getSubFolder();

    protected void ePerft(int depth) {
        Board board = FEN.toBoard(getFen());
        EPerftService ePerftService = new EPerftService(board);
        Instant start = Instant.now();
        ePerftService.ePerft(board, depth);
        Instant end = Instant.now();
        Duration performance = Duration.between(start, end);
        writeResults(depth, performance);
        System.out.println("Duration: " + performance);
    }

    private void writeResults(int depth, Duration performance) {
        Instant timestamp = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        String line = String.format("%s,%s\n", timestamp, performance);
        String fileName = String.format("src/test/resources/eperft/%s/eperft_depth_%s.csv", getSubFolder(), depth);
        Path path = Paths.get(fileName);
        try {
            Files.writeString(path, line, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.printf("Error writing to %s! %s", fileName, e);
        }
    }

}
