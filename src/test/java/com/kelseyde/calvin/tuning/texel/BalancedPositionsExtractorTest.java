package com.kelseyde.calvin.tuning.texel;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

@Disabled
public class BalancedPositionsExtractorTest {

    private static final Evaluation evaluator = TestUtils.EVALUATOR;

    @Test
    public void testExtractBalancedPositions() throws IOException {

        List<String> fens = TestUtils.loadFens();
        System.out.println(fens.size());

        List<String> balancedPositions = fens.stream()
                .filter(fen -> {
                    Board board = FEN.toBoard(fen);
                    int eval = evaluator.evaluate(board);
                    return eval >= -300 && eval <= 300;
                })
                .toList();
        System.out.println(balancedPositions.size());

    }


}
