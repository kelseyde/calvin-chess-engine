package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.utils.TestUtils;

public class MopUpTest {

    private final EngineConfig config = TestUtils.TST_CONFIG;

//    @Test
//    public void testBonusForMovingKingCloser() {
//
//        String fen = "8/8/8/4k3/8/8/8/3QK3 w - - 0 1";
//        Board board = FEN.toBoard(fen);
//
//        Material friendlyMaterial = Material.fromBoard(board, true);
//        Material opponentMaterial = Material.fromBoard(board, false);
//
//        int score = MopUp.score(config, board, friendlyMaterial., opponentMaterial, true);
//        System.out.println(score);
//
//        board.makeMove(TestUtils.getLegalMove(board, "e1", "e2"));
//
//        int score2 = MopUp.score(config, board, friendlyMaterial, opponentMaterial, true);
//        System.out.println(score2);
//
//        Assertions.assertTrue(score2 > score);
//    }
//
//    @Test
//    public void testEvalTaperedBasedOnOpponentMaterial() {
//
//        String fen = "8/8/8/4k3/8/8/8/3QK3 w - - 0 1";
//        Board board = FEN.toBoard(fen);
//
//        Material friendlyMaterial = Material.fromBoard(board, true);
//        Material opponentMaterial = Material.fromBoard(board, false);
//
//        int score = MopUp.score(config, board, friendlyMaterial, opponentMaterial, true);
//
//        // give black an extra couple of knights
//        fen = "8/8/4n3/3nk3/8/8/8/3QK3 w - - 0 1";
//        board = FEN.toBoard(fen);
//
//        friendlyMaterial = Material.fromBoard(board, true);
//        opponentMaterial = Material.fromBoard(board, false);
//
//        int score2 = MopUp.score(config, board, friendlyMaterial, opponentMaterial, true);
//        System.out.println(score);
//        System.out.println(score2);
//
//        Assertions.assertTrue(score2 < score);
//    }

}