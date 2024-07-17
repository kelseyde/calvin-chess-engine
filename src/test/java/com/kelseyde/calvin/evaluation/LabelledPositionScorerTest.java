package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.search.Searcher;
import com.kelseyde.calvin.utils.FEN;
import com.kelseyde.calvin.utils.TestUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Disabled
public class LabelledPositionScorerTest {

    private static final String INPUT_FILE = "src/test/resources/texel/training_data.epd";
    private static final String OUTPUT_FILE = "src/test/resources/texel/training_data_scored.epd";
    private static final Searcher searcher = TestUtils.SEARCHER;
    private static final int CHECKPOINT = 10000;
    private static final int DEPTH = 6;

    @Test
    public void testScorePositions() throws IOException {

        List<String> labelledPositions = TestUtils.loadFens(INPUT_FILE).stream().distinct().toList();
        System.out.println("Loaded " + labelledPositions.size() + " positions");
        int count = 0;
        for (String labelledPosition : labelledPositions) {
            String[] parts = labelledPosition.split("\"");
            String fen = parts[0];
            Board board = FEN.toBoard(fen);
            searcher.clearHistory();
            searcher.setPosition(board);
            int eval = searcher.searchToDepth(DEPTH).eval();
            double result = Double.parseDouble(parts[1]);
            if (count % CHECKPOINT == 0) {
                System.out.printf("Processed %d positions\n", count);
            }
            String line = String.format("%s,%s,%s\n", fen, result, eval);
            Files.writeString(Paths.get(OUTPUT_FILE), line, StandardOpenOption.APPEND);
            count++;
        }

    }

}
