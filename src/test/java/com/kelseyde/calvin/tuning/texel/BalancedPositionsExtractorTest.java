package com.kelseyde.calvin.tuning.texel;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.generation.MoveGenerator;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Disabled
public class BalancedPositionsExtractorTest {

    private static final Evaluation evaluator = TestUtils.EVALUATOR;
    private static final MoveGenerator moveGenerator = TestUtils.MOVE_GENERATOR;

    @Test
    public void testExtractBalancedPositions() throws IOException {

        List<String> fens = TestUtils.loadAllFens();
        System.out.println(fens.size());

        List<String> notInCheck = fens.stream()
                .filter(fen -> {
                    Board board = FEN.toBoard(fen);
                    boolean white = board.isWhiteToMove();
                    return !moveGenerator.isCheck(board, white) && !moveGenerator.isCheck(board, !white);
                })
                .distinct()
                .toList();
        System.out.println(notInCheck.size());

        Path path = Paths.get(TestUtils.QUIET_POSITIONS_EXTENDED_FILE);
        Files.deleteIfExists(path);
        Files.createFile(path);
        notInCheck.forEach(fen -> {
            try {
                Files.writeString(path, fen + "\n", StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }


}
