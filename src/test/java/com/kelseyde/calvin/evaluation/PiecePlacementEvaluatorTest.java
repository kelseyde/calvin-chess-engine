package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.evaluation.score.Material;
import com.kelseyde.calvin.evaluation.score.Phase;
import com.kelseyde.calvin.evaluation.score.PiecePlacement;
import com.kelseyde.calvin.utils.TestUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PiecePlacementEvaluatorTest {

    EngineConfig config = TestUtils.PRD_CONFIG;

    @Test
    public void testStartingPosition() {
        Board board = new Board();
        Assertions.assertEquals(0, score(board));
    }

    @Test
    public void bigPawnCentreGivesAdvantageToBlack() {

        Board board = FEN.toBoard("r1b2rk1/pp4pp/1qnb1n2/2pppp2/8/3PPN2/PPP1BPPP/RNBQ1R1K b - - 7 10");
        Assertions.assertEquals(76, score(board));

        board.setWhiteToMove(true);
        Assertions.assertEquals(-76, score(board));

    }

    @Test
    public void testKingActivityInEndgame() {

        String fen = "8/p7/8/8/8/3k4/P7/7K w - - 0 1";
        Board board = FEN.toBoard(fen);

        Assertions.assertEquals(-58, score(board));

    }

    @Test
    public void testPawnActivityInEndgame() {

        String fen = "7k/8/8/8/1p6/p6P/6P1/7K w - - 0 1";

        Board board = FEN.toBoard(fen);

        Assertions.assertEquals(-124, score(board));

    }

    @Test
    public void testEvaluationIsSymmetrical() {

        String ruyLopezFen = "r1bqkb1r/1ppp1ppp/p1n2n2/4p3/B3P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 1";
        String ruyLopezReverseFen = "rnbqk2r/pppp1ppp/5n2/b3p3/4P3/P1N2N2/1PPP1PPP/R1BQKB1R b KQkq - 0 1";

        Board board1 = FEN.toBoard(ruyLopezFen);
        int score1 = score(board1);

        Board board2 = FEN.toBoard(ruyLopezFen);
        int score2 = score(board2);

        Assertions.assertEquals(score1, score2);

    }

    private int score(Board board) {
        int whiteMiddlegameScore = 0;
        int whiteEndgameScore = 0;
        int blackMiddlegameScore = 0;
        int blackEndgameScore = 0;
        Material whiteMaterial = Material.fromBoard(board, true);
        Material blackMaterial = Material.fromBoard(board, false);

        PiecePlacement whitePiecePlacement = PiecePlacement.fromBoard(board, true);
        PiecePlacement blackPiecePlacement = PiecePlacement.fromBoard(board, false);

        whiteMiddlegameScore += whitePiecePlacement.sum(config.getMiddlegameTables(), true);
        whiteEndgameScore += whitePiecePlacement.sum(config.getEndgameTables(), true);
        blackMiddlegameScore += blackPiecePlacement.sum(config.getMiddlegameTables(), false);
        blackEndgameScore += blackPiecePlacement.sum(config.getEndgameTables(), false);

        float phase = Phase.fromMaterial(whiteMaterial, blackMaterial);
        int whiteScore = Phase.taperedEval(whiteMiddlegameScore, whiteEndgameScore, phase);
        int blackScore = Phase.taperedEval(blackMiddlegameScore, blackEndgameScore, phase);
        int modifier = board.isWhiteToMove() ? 1 : -1;
        return modifier * (whiteScore - blackScore);
    }

}
