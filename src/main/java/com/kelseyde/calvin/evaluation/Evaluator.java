package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.evaluation.material.MaterialEvaluator;
import com.kelseyde.calvin.evaluation.mopup.MopUpEvaluator;
import com.kelseyde.calvin.evaluation.pawnstructure.PawnStructureEvaluator;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementEvaluator;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;

public class Evaluator {

    private final MaterialEvaluator materialEvaluator = new MaterialEvaluator();

    private final PiecePlacementEvaluator piecePlacementEvaluator = new PiecePlacementEvaluator();

    private final PawnStructureEvaluator pawnStructureEvaluator = new PawnStructureEvaluator();

    private final MopUpEvaluator mopUpEvaluator = new MopUpEvaluator();

    private final Deque<Evaluation> whiteEvalHistory = new ArrayDeque<>();
    private final Deque<Evaluation> blackEvalHistory = new ArrayDeque<>();

    private @Getter Evaluation whiteEval;
    private @Getter Evaluation blackEval;

    private @Getter int score;

    private final Board board;

    public Evaluator(Board board) {
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

        Evaluation newWhiteEval = whiteEval.copy();
        Evaluation newBlackEval = blackEval.copy();

        whiteEvalHistory.push(whiteEval);
        blackEvalHistory.push(blackEval);

        PieceType pieceType = board.pieceAt(move.getEndSquare());

        boolean updatePawnStructure = false;
        boolean updateWhiteMaterial = false;
        boolean updateBlackMaterial = false;
        boolean updateWhitePiecePlacement = false;
        boolean updateBlackPiecePlacement = false;

        if (move.isPromotion()) {
            updatePawnStructure = true;
            if (board.isWhiteToMove()) {
                updateBlackMaterial = true;
            } else {
                updateWhiteMaterial = true;
            }
        }

        PieceType capturedPiece = board.getGameState().getCapturedPiece();
        if (capturedPiece != null) {
            if (board.isWhiteToMove()) {
                updateWhiteMaterial = true;
                updateWhitePiecePlacement = true;
                if (capturedPiece == PieceType.PAWN) {
                    updatePawnStructure = true;
                }
            } else {
                updateBlackMaterial = true;
                updateBlackPiecePlacement = true;
                if (capturedPiece == PieceType.PAWN) {
                    updatePawnStructure = true;
                }
            }
        }

        if (board.isWhiteToMove()) {
            updateBlackPiecePlacement = true;
        } else {
            updateWhitePiecePlacement = true;
        }

        if (pieceType == PieceType.PAWN) {
            // Any pawn move can create passed/backward/doubled pawns for either side.
            updatePawnStructure = true;
        }

        if (updatePawnStructure) {
            newBlackEval.setPawnStructureScore(pawnStructureEvaluator.evaluate(board, false));
            newWhiteEval.setPawnStructureScore(pawnStructureEvaluator.evaluate(board, true));
        }

        if (updateWhiteMaterial) {
            newWhiteEval.setMaterial(materialEvaluator.evaluate(board, true));
        }
        if (updateBlackMaterial) {
            newBlackEval.setMaterial(materialEvaluator.evaluate(board, false));
        }

        if (updateWhitePiecePlacement) {
            newWhiteEval.setPiecePlacementScore(piecePlacementEvaluator.evaluate(board, newWhiteEval.getMaterial().phase(), true));
        }
        if (updateBlackPiecePlacement) {
            newBlackEval.setPiecePlacementScore(piecePlacementEvaluator.evaluate(board, newBlackEval.getMaterial().phase(), false));
        }

        newWhiteEval.setMopUpEval(mopUpEvaluator.evaluate(board, newWhiteEval.getMaterial(), newBlackEval.getMaterial(), true));
        newBlackEval.setMopUpEval(mopUpEvaluator.evaluate(board, newBlackEval.getMaterial(), newWhiteEval.getMaterial(), false));

        this.whiteEval = newWhiteEval;
        this.blackEval = newBlackEval;

        int whiteScore = newWhiteEval.sum();
        int blackScore = newBlackEval.sum();
        int modifier = board.isWhiteToMove() ? 1 : -1;
        this.score = modifier * (whiteScore - blackScore);

    }

    public void unmakeMove() {
        whiteEval = whiteEvalHistory.pop();
        blackEval = blackEvalHistory.pop();
        int whiteScore = whiteEval.sum();
        int blackScore = blackEval.sum();
        int modifier = board.isWhiteToMove() ? 1 : -1;
        score = modifier * (whiteScore - blackScore);
    }

}
