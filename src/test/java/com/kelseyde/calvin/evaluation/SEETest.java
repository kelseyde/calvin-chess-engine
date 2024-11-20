package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.movegen.MoveGenerator;
import com.kelseyde.calvin.search.SEE;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SEETest {

    private static final MoveGenerator MOVEGEN = new MoveGenerator();

    private int passed = 0;

    @Test
    @Disabled
    public void testSeeSuite() throws IOException {

        int[] initialValues = SEE.SEE_PIECE_VALUES;
        SEE.SEE_PIECE_VALUES = new int[] {100, 300, 300, 500, 900, 0};

        Path path = Path.of("src/test/resources/see_suite.epd");
        List<String> lines = Files.readAllLines(path);

        passed = 0;
        lines.forEach(this::runTest);
        if (passed != lines.size()) {
            Assertions.fail("Passed " + passed + "/" + lines.size());
        }

        SEE.SEE_PIECE_VALUES = initialValues;

    }

    @Test
    @Disabled
    public void debugSingle() {

        int[] initialValues = SEE.SEE_PIECE_VALUES;
        SEE.SEE_PIECE_VALUES = new int[] {100, 300, 300, 500, 900, 0};

        String line = "rn2k2r/1bq2ppp/p2bpn2/1p1p4/3N4/1BN1P3/PPP2PPP/R1BQR1K1 b kq - | d6h2 | 100 | P";
        runTest(line);

        SEE.SEE_PIECE_VALUES = initialValues;

    }

    private void runTest(String line) {
        String[] parts = line.split("\\|");
        String fen = parts[0].trim();
        Board board = Board.from(fen);
        Move move = legalMove(board, Move.fromUCI(parts[1].trim()));
        int threshold = Integer.parseInt(parts[2].trim());
        if (SEE.see(board, move, threshold)
                && !SEE.see(board, move, threshold + 1)) {
            passed++;
        } else {
            int actualThreshold = findThreshold(board, move);
            System.out.println("Failed: " + line + ", target: " + threshold + ", actual: " + actualThreshold);
        }
    }

    private Move legalMove(Board board, Move uciMove) {
        return MOVEGEN.generateMoves(board).stream()
                .filter(move -> move.matches(uciMove))
                .findAny()
                .orElseThrow();
    }

    private int findThreshold(Board board, Move move) {
        for (int i = 5000; i > -5000; i-= 10) {
            if (SEE.see(board, move, i)) {
                return i;
            }
        }
        return Integer.MIN_VALUE;
    }

}