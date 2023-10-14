package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
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

    private int score;

    private final Board board;

    public BoardEvaluator(Board board) {
        this.board = board;
        this.whiteEval = new Evaluation();
        this.blackEval = new Evaluation();

        whiteEval.setMaterial(materialEvaluator.evaluate(board, true));
        blackEval.setMaterial(materialEvaluator.evaluate(board, false));

        whiteEval.setPiecePlacementScore(piecePlacementEvaluator.evaluate(board, whiteEval.getMaterial().phase(), true));
        blackEval.setPiecePlacementScore(piecePlacementEvaluator.evaluate(board, blackEval.getMaterial().phase(), false));

        whiteEval.setPawnStructureScore(pawnStructureEvaluator.evaluate(board, true));
        blackEval.setPawnStructureScore(pawnStructureEvaluator.evaluate(board, false));

        whiteEval.setMopUpEval(mopUpEvaluator.evaluate(board, whiteEval.getMaterial(), blackEval.getMaterial(), true));
        blackEval.setMopUpEval(mopUpEvaluator.evaluate(board, blackEval.getMaterial(), whiteEval.getMaterial(), false));

        int whiteScore = whiteEval.sum();
        int blackScore = blackEval.sum();
        int modifier = board.isWhiteToMove() ? 1 : -1;
        score = modifier * (whiteScore - blackScore);
    }

    /**
     * Updates the evaluation based on the last move. Assumes that the move has already been 'made' on the board.
     */
    public void makeMove(Move move) {

        whiteEvalHistory.push(whiteEval);
        blackEvalHistory.push(blackEval);

        PieceType pieceType = board.pieceAt(move.getEndSquare());

        if (move.isPromotion()) {
            if (board.isWhiteToMove()) {
                blackEval.setMaterial(materialEvaluator.updatePromotion(blackEval.getMaterial(), move.getPromotionPieceType()));
            } else {
                whiteEval.setMaterial(materialEvaluator.updatePromotion(whiteEval.getMaterial(), move.getPromotionPieceType()));
            }
        }

        PieceType capturedPiece = board.getGameState().getCapturedPiece();
        if (capturedPiece != null) {
            if (board.isWhiteToMove()) {
                whiteEval.setMaterial(materialEvaluator.updateCapture(whiteEval.getMaterial(), capturedPiece));
                whiteEval.setPiecePlacementScore(piecePlacementEvaluator.handleCapture(board, whiteEval.getGamePhase(), whiteEval.getPiecePlacementScore(), capturedPiece));
                if (capturedPiece == PieceType.PAWN) {
                    whiteEval.setPawnStructureScore(pawnStructureEvaluator.evaluate(board, true));
                }
            } else {
                blackEval.setMaterial(materialEvaluator.updateCapture(blackEval.getMaterial(), capturedPiece));
                blackEval.setPiecePlacementScore(piecePlacementEvaluator.handleCapture(board, blackEval.getGamePhase(), blackEval.getPiecePlacementScore(), capturedPiece));
                if (capturedPiece == PieceType.PAWN) {
                    blackEval.setPawnStructureScore(pawnStructureEvaluator.evaluate(board, false));
                }
            }
        }

        if (board.isWhiteToMove()) {
            blackEval.setPiecePlacementScore(piecePlacementEvaluator.handleMove(board, blackEval.getGamePhase(), blackEval.getPiecePlacementScore(), move));
        } else {
            whiteEval.setPiecePlacementScore(piecePlacementEvaluator.handleMove(board, whiteEval.getGamePhase(), whiteEval.getPiecePlacementScore(), move));
        }

        if (pieceType == PieceType.PAWN) {
            if (board.isWhiteToMove()) {
                blackEval.setPawnStructureScore(pawnStructureEvaluator.evaluate(board, false));
            } else {
                whiteEval.setPawnStructureScore(pawnStructureEvaluator.evaluate(board, true));
            }
        }

        whiteEval.setMopUpEval(mopUpEvaluator.evaluate(board, whiteEval.getMaterial(), blackEval.getMaterial(), true));
        blackEval.setMopUpEval(mopUpEvaluator.evaluate(board, blackEval.getMaterial(), whiteEval.getMaterial(), false));

        int whiteScore = whiteEval.sum();
        int blackScore = blackEval.sum();
        int modifier = board.isWhiteToMove() ? 1 : -1;
        score = modifier * (whiteScore - blackScore);

    }

    public void unmakeMove() {
        whiteEval = whiteEvalHistory.pop();
        blackEval = blackEvalHistory.pop();
        int whiteScore = whiteEval.sum();
        int blackScore = blackEval.sum();
        int modifier = board.isWhiteToMove() ? 1 : -1;
        score = modifier * (whiteScore - blackScore);
    }

    public int get() {
        return score;
    }

    public int evaluate(Board board) {

        Evaluation whiteEval = new Evaluation();
        Evaluation blackEval = new Evaluation();

        whiteEval.setMaterial(materialEvaluator.evaluate(board, true));
        blackEval.setMaterial(materialEvaluator.evaluate(board, false));

        whiteEval.setPiecePlacementScore(piecePlacementEvaluator.evaluate(board, whiteEval.getMaterial().phase(), true));
        blackEval.setPiecePlacementScore(piecePlacementEvaluator.evaluate(board, blackEval.getMaterial().phase(), false));

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
