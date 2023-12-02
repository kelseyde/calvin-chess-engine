package com.kelseyde.calvin.evaluation.score;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RookEvaluationTest {

    private final EngineConfig config = TestUtils.TST_CONFIG;

    @Test
    public void testBothPawnsThenNoBonus() {

        String fen = "k1r5/2p1p3/8/8/8/8/2P1P3/4R2K w - - 0 1";
        Board board = FEN.toBoard(fen);

        Assertions.assertEquals(0, RookEvaluation.score(config, board, 1, true));
        Assertions.assertEquals(0, RookEvaluation.score(config, board, 1, false));

    }

    @Test
    public void testOnlyFriendlyPawnThenNoBonus() {

        String fen = "k1r5/2p5/8/8/8/8/4P3/4R2K w - - 0 1";
        Board board = FEN.toBoard(fen);

        Assertions.assertEquals(0, RookEvaluation.score(config, board, 1, true));
        Assertions.assertEquals(0, RookEvaluation.score(config, board, 1, false));

    }

    @Test
    public void testSemiOpenFile() {

        String fen = "k1r5/4p3/8/8/8/8/2P5/4R2K w - - 0 1";
        Board board = FEN.toBoard(fen);

        Assertions.assertEquals(18, RookEvaluation.score(config, board, 1, true));
        Assertions.assertEquals(18, RookEvaluation.score(config, board, 1, false));

    }

    @Test
    public void testOpenFile() {

        String fen = "k1r5/8/8/8/8/8/8/4R2K w - - 0 1";
        Board board = FEN.toBoard(fen);

        Assertions.assertEquals(42, RookEvaluation.score(config, board, 1, true));
        Assertions.assertEquals(42, RookEvaluation.score(config, board, 1, false));

    }

    @Test
    public void testOpenFileNotAffectedByAdjacentPawns() {

        String fen = "k1r5/3p2p1/8/1p1P4/8/8/1p1P1PPP/4R2K w - - 0 1";
        Board board = FEN.toBoard(fen);

        Assertions.assertEquals(42, RookEvaluation.score(config, board, 1, true));
        Assertions.assertEquals(42, RookEvaluation.score(config, board, 1, false));

    }

    @Test
    public void testSemiOpenFileNotAffectedByAdjacentPawns() {

        String fen = "k1r5/3pp1p1/8/1p1P4/8/8/1pPP1PPP/4R2K w - - 0 1";
        Board board = FEN.toBoard(fen);

        Assertions.assertEquals(18, RookEvaluation.score(config, board, 1, true));
        Assertions.assertEquals(18, RookEvaluation.score(config, board, 1, false));

    }

}