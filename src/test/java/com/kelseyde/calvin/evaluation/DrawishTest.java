package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.hce.Material;
import com.kelseyde.calvin.evaluation.hce.Result;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Disabled
public class DrawishTest {

    @Test
    public void extractDrawishPositions() throws IOException {

        int count = 0;
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        for (String fen : loadFens()) {
            Board board = FEN.toBoard(fen);
            Material whiteMaterial = Material.fromBoard(board, true);
            Material blackMaterial = Material.fromBoard(board, false);
            if (Result.isDrawish(whiteMaterial, blackMaterial)) {
                count++;
                String newFen = FEN.toFEN(board);
                int eval = TestUtils.EVALUATOR.evaluate(board);
                System.out.println(eval + " --- " + newFen);
                if (eval < minValue) minValue = eval;
                if (eval > maxValue) maxValue = eval;
                if (count > 5000) break;
            }
        }

        System.out.println("Finished");
        System.out.println("-----");
        System.out.println(count);
        System.out.println(minValue);
        System.out.println(maxValue);

    }


    private List<String> loadFens() throws IOException {
        Path path = Paths.get("src/test/resources/texel/quiet_positions.epd");
        return Files.readAllLines(path);
    }

}
