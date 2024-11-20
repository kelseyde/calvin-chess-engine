package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.search.SEE;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SEETest {

    private int passed = 0;

    @Test
    public void testSeeSuite() throws IOException {

        Path path = Path.of("src/test/resources/see_suite.epd");
        List<String> lines = Files.readAllLines(path);

        passed = 0;
        lines.forEach(this::runTest);
        if (passed != lines.size()) {
            Assertions.fail("Passed " + passed + "/" + lines.size());
        }

    }

    private void runTest(String line) {
        String[] parts = line.split("\\|");
        String fen = parts[0].trim();
        Board board = Board.from(fen);
        Move move = Move.fromUCI(parts[1].trim());
        int threshold = Integer.parseInt(parts[2].trim());
        if (SEE.see(board, move, threshold)
                && !SEE.see(board, move, threshold + 1)) {
            passed++;
        } else {
            System.out.println("Failed: " + line);
        }
    }

}