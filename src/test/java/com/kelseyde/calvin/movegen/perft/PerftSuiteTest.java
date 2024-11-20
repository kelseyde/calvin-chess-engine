package com.kelseyde.calvin.movegen.perft;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class PerftSuiteTest {

    @Test
    @Disabled
    public void testPerftSuite() throws IOException {

        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/perft_suite.epd"));
        lines.forEach(line -> {

            String[] parts = line.split(";");
            String fen = parts[0];
            if (parts.length > 1) {
                long expectedTotalMoves = Long.parseLong(parts[1].split(" ")[1].trim());
                perftDepth(fen, 1, expectedTotalMoves);
            }
            if (parts.length > 2) {
                long expectedTotalMoves = Long.parseLong(parts[2].split(" ")[1].trim());
                perftDepth(fen, 2, expectedTotalMoves);
            }
            if (parts.length > 3) {
                long expectedTotalMoves = Long.parseLong(parts[3].split(" ")[1].trim());
                perftDepth(fen, 3, expectedTotalMoves);
            }
            if (parts.length > 4) {
                long expectedTotalMoves = Long.parseLong(parts[4].split(" ")[1].trim());
                perftDepth(fen, 4, expectedTotalMoves);
            }
            if (parts.length > 5) {
                long expectedTotalMoves = Long.parseLong(parts[5].split(" ")[1].trim());
                perftDepth(fen, 5, expectedTotalMoves);
            }
            if (parts.length > 6) {
                long expectedTotalMoves = Long.parseLong(parts[6].split(" ")[1].trim());
                perftDepth(fen, 6, expectedTotalMoves);
            }

        });

    }

    private void perftDepth(String fen, int depth, long expectedTotalMoves) {
        new PerftTest() {
            @Override
            protected String getFen() {
                return fen;
            }

            @Override
            protected String getSubFolder() {
                return null;
            }
        }.perft(depth, expectedTotalMoves);
    }

}
