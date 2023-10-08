package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.evaluation.material.MaterialCalculator;
import com.kelseyde.calvin.evaluation.mopup.MopUpEvaluator;
import com.kelseyde.calvin.evaluation.pawnstructure.PawnStructureEvaluator;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementEvaluator;

public class BoardEvaluator {

    private final MaterialCalculator materialEvaluator = new MaterialCalculator();

    private final PiecePlacementEvaluator piecePlacementEvaluator = new PiecePlacementEvaluator();

    private final PawnStructureEvaluator pawnStructureEvaluator = new PawnStructureEvaluator();

    private final MopUpEvaluator mopUpEvaluator = new MopUpEvaluator();

    public int evaluate(Board board) {

        Evaluation whiteEval = new Evaluation();
        Evaluation blackEval = new Evaluation();

        whiteEval.setMaterial(materialEvaluator.calculate(board, true));
        blackEval.setMaterial(materialEvaluator.calculate(board, false));

        whiteEval.setPieceSquareScore(piecePlacementEvaluator.evaluate(board, whiteEval.getMaterial().phase(), true));
        blackEval.setPieceSquareScore(piecePlacementEvaluator.evaluate(board, blackEval.getMaterial().phase(), false));

        whiteEval.setPawnStructureScore(pawnStructureEvaluator.evaluate(board, true));
        blackEval.setPawnStructureScore(pawnStructureEvaluator.evaluate(board, false));

        whiteEval.setMopUpEval(mopUpEvaluator.evaluate(board, whiteEval.getMaterial(), blackEval.getMaterial(), true));
        blackEval.setMopUpEval(mopUpEvaluator.evaluate(board, blackEval.getMaterial(), whiteEval.getMaterial(), false));

        int whiteScore = whiteEval.sum();
        int blackScore = blackEval.sum();
        int modifier = board.isWhiteToMove() ? 1 : -1;
        return modifier * (whiteScore - blackScore);

    }

}
