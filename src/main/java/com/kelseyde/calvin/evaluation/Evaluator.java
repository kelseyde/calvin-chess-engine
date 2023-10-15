package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.evaluation.material.Material;
import com.kelseyde.calvin.evaluation.material.MaterialEvaluator;
import com.kelseyde.calvin.evaluation.mopup.MopUpEvaluator;
import com.kelseyde.calvin.evaluation.pawnstructure.PawnStructureEvaluator;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementEvaluator;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementScore;
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

    }

    /**
     * Updates the evaluation based on the last move. Assumes that the move has already been 'made' on the board.
     */
    public void makeMove(Move move) {

        whiteEvalHistory.push(whiteEval);
        blackEvalHistory.push(blackEval);

        PieceType pieceType = board.pieceAt(move.getEndSquare());

        boolean updatePawnStructure = false;
        boolean updateWhiteMaterial = false;
        boolean updateBlackMaterial = false;
        boolean updateWhiteCapture = false;
        boolean updateBlackCapture = false;
        boolean updateWhiteWeightedPieces = false;
        boolean updateBlackWeightedPieces = false;

        if (move.isPromotion()) {
            updatePawnStructure = true;
            if (board.isWhiteToMove()) {
                updateBlackMaterial = true;
                updateBlackWeightedPieces = true;
            } else {
                updateWhiteMaterial = true;
                updateWhiteWeightedPieces = true;
            }
        }

        PieceType capturedPiece = board.getGameState().getCapturedPiece();
        if (capturedPiece != null) {
            if (board.isWhiteToMove()) {
                updateWhiteMaterial = true;
                updateWhiteCapture = true;
                updateWhiteWeightedPieces = true;
                if (capturedPiece == PieceType.PAWN) {
                    updatePawnStructure = true;
                }
            } else {
                updateBlackMaterial = true;
                updateBlackCapture = true;
                updateBlackWeightedPieces = true;
                if (capturedPiece == PieceType.PAWN) {
                    updatePawnStructure = true;
                }
            }
        }

        if (pieceType == PieceType.PAWN) {
            // Any pawn move can create passed/backward/doubled pawns for either side.
            updatePawnStructure = true;
        }

        Material whiteMaterial = whiteEval.getMaterial();
        Material blackMaterial = blackEval.getMaterial();
        PiecePlacementScore whitePieceScore = whiteEval.getPiecePlacementScore();
        PiecePlacementScore blackPieceScore = blackEval.getPiecePlacementScore();
        int whitePawnStructureScore = whiteEval.getPawnStructureScore();
        int blackPawnStructureScore = blackEval.getPawnStructureScore();

        if (updatePawnStructure) {
            whitePawnStructureScore = pawnStructureEvaluator.evaluate(board, true);
            blackPawnStructureScore = pawnStructureEvaluator.evaluate(board, false);
        }

        if (updateWhiteMaterial) {
            whiteMaterial = materialEvaluator.evaluate(board, true);
        }
        if (updateBlackMaterial) {
            blackMaterial = materialEvaluator.evaluate(board, false);
        }
        if (updateWhiteWeightedPieces) {
            whitePieceScore = piecePlacementEvaluator.updateWeightedPieces(board, whiteMaterial.phase(), whitePieceScore, true);
        }
        if (updateBlackWeightedPieces) {
            blackPieceScore = piecePlacementEvaluator.updateWeightedPieces(board, blackMaterial.phase(), blackPieceScore, false);
        }

        if (board.isWhiteToMove()) {
            blackPieceScore = piecePlacementEvaluator.handleMove(board, blackMaterial.phase(), blackPieceScore, move);
        } else {
            whitePieceScore = piecePlacementEvaluator.handleMove(board, whiteMaterial.phase(), whitePieceScore, move);
        }

        if (updateWhiteCapture) {
            whitePieceScore = piecePlacementEvaluator.handleCapture(board, whiteMaterial.phase(), whitePieceScore, capturedPiece);
        }
        if (updateBlackCapture) {
            blackPieceScore = piecePlacementEvaluator.handleCapture(board, blackMaterial.phase(), blackPieceScore, capturedPiece);
        }

        int whiteMopUpScore = mopUpEvaluator.evaluate(board, whiteMaterial, blackMaterial, true);
        int blackMopUpScore = mopUpEvaluator.evaluate(board, blackMaterial, whiteMaterial, false);

        this.whiteEval = new Evaluation(whiteMaterial, whitePieceScore, whitePawnStructureScore, whiteMopUpScore);
        this.blackEval = new Evaluation(blackMaterial, blackPieceScore, blackPawnStructureScore, blackMopUpScore);

    }

    public void unmakeMove() {
        whiteEval = whiteEvalHistory.pop();
        blackEval = blackEvalHistory.pop();
    }

    public int get() {
        int whiteScore = whiteEval.sum();
        int blackScore = blackEval.sum();
        int modifier = board.isWhiteToMove() ? 1 : -1;
        return modifier * (whiteScore - blackScore);
    }

}
