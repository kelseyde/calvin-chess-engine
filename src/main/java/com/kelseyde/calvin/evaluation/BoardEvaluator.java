package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.evaluation.material.MaterialEvaluator;
import com.kelseyde.calvin.evaluation.mopup.MopUpEvaluator;
import com.kelseyde.calvin.evaluation.pawnstructure.PawnStructureEvaluator;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementEvaluator;

import java.util.ArrayDeque;
import java.util.Deque;

public class BoardEvaluator {

    private final MaterialEvaluator materialEvaluator = new MaterialEvaluator();

    private final PiecePlacementEvaluator piecePlacementEvaluator = new PiecePlacementEvaluator();

    private final PawnStructureEvaluator pawnStructureEvaluator = new PawnStructureEvaluator();

    private final MopUpEvaluator mopUpEvaluator = new MopUpEvaluator();

    private final Deque<Evaluation> whiteEvalHistory = new ArrayDeque<>();
    private final Deque<Evaluation> blackEvalHistory = new ArrayDeque<>();

    private Evaluation whiteEval;
    private Evaluation blackEval;

    private Board board;

    public BoardEvaluator(Board board) {
        this.board = board;
        this.whiteEval = new Evaluation();
        this.blackEval = new Evaluation();

        whiteEval.setMaterial(materialEvaluator.calculate(board, true));
        blackEval.setMaterial(materialEvaluator.calculate(board, false));

        whiteEval.setPieceSquareScore(piecePlacementEvaluator.evaluate(board, whiteEval.getMaterial().phase(), true));
        blackEval.setPieceSquareScore(piecePlacementEvaluator.evaluate(board, blackEval.getMaterial().phase(), false));

        whiteEval.setPawnStructureScore(pawnStructureEvaluator.evaluate(board, true));
        blackEval.setPawnStructureScore(pawnStructureEvaluator.evaluate(board, false));

        whiteEval.setMopUpEval(mopUpEvaluator.evaluate(board, whiteEval.getMaterial(), blackEval.getMaterial(), true));
        blackEval.setMopUpEval(mopUpEvaluator.evaluate(board, blackEval.getMaterial(), whiteEval.getMaterial(), false));
    }

    public void makeMove(Move move, boolean isWhiteMove) {

        long opponents = isWhiteMove ? board.getBlackPieces() : board.getWhitePieces();

    }

    public void unapplyMove() {
        whiteEval = whiteEvalHistory.pop();
        blackEval = blackEvalHistory.pop();
    }

    public int get() {
        int whiteScore = whiteEval.sum();
        int blackScore = blackEval.sum();
        int modifier = board.isWhiteToMove() ? 1 : -1;
        return modifier * (whiteScore - blackScore);
    }

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
