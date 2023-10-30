package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.evaluation.kingsafety.KingPawnShieldEvaluator;
import com.kelseyde.calvin.evaluation.material.Material;
import com.kelseyde.calvin.evaluation.material.MaterialEvaluator;
import com.kelseyde.calvin.evaluation.mopup.MopUpEvaluator;
import com.kelseyde.calvin.evaluation.pawnstructure.PawnStructureEvaluator;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementEvaluator;
import com.kelseyde.calvin.evaluation.placement.PiecePlacementScore;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Calculates a numeric score evaluating the board. The evaluation is relative to the side to move, meaning a positive
 * score indicates the side to move has an advantage, and vice versa, regardless of the colour of the pieces.
 * <p>
 * The score consists of the material score + the piece placement score + pawn structure bonuses/penalties + mop up eval.
 * <p>
 * The evaluation is incrementally updated as moves are made on the board, rather than re-calculated every time.
 */
public class Evaluator implements Evaluation {

    private final MaterialEvaluator materialEvaluator = new MaterialEvaluator();

    private final PiecePlacementEvaluator piecePlacementEvaluator = new PiecePlacementEvaluator();

    private final PawnStructureEvaluator pawnStructureEvaluator = new PawnStructureEvaluator();

    private final KingPawnShieldEvaluator kingPawnShieldEvaluator = new KingPawnShieldEvaluator();

    private final MopUpEvaluator mopUpEvaluator = new MopUpEvaluator();

    private final Deque<EvaluationResult> whiteEvalHistory = new ArrayDeque<>();
    private final Deque<EvaluationResult> blackEvalHistory = new ArrayDeque<>();

    private @Getter EvaluationResult whiteEval;
    private @Getter EvaluationResult blackEval;

    private Board board;

    public Evaluator(Board board) {
        init(board);
    }

    @Override
    public void init(Board board) {
        this.board = board;
        this.whiteEval = new EvaluationResult();
        this.blackEval = new EvaluationResult();

        whiteEval.setMaterial(materialEvaluator.evaluate(board, true));
        blackEval.setMaterial(materialEvaluator.evaluate(board, false));

        whiteEval.setPiecePlacementScore(piecePlacementEvaluator.evaluate(board, blackEval.getMaterial().phase(), true));
        blackEval.setPiecePlacementScore(piecePlacementEvaluator.evaluate(board, whiteEval.getMaterial().phase(), false));

        whiteEval.setPawnStructureScore(pawnStructureEvaluator.evaluate(board, true));
        blackEval.setPawnStructureScore(pawnStructureEvaluator.evaluate(board, false));

        whiteEval.setKingPawnShieldScore(kingPawnShieldEvaluator.evaluate(board, blackEval.getMaterial(), true));
        blackEval.setKingPawnShieldScore(kingPawnShieldEvaluator.evaluate(board, whiteEval.getMaterial(), false));

        whiteEval.setMopUpEval(mopUpEvaluator.evaluate(board, whiteEval.getMaterial(), blackEval.getMaterial(), true));
        blackEval.setMopUpEval(mopUpEvaluator.evaluate(board, blackEval.getMaterial(), whiteEval.getMaterial(), false));
    }

    /**
     * Updates the evaluation based on the last move. Assumes that the move has already been 'made' on the board.
     */
    @Override
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
        boolean updateWhiteKingPawnShield = false;
        boolean updateBlackKingPawnShield = false;

        if (move.isPromotion()) {
            updatePawnStructure = true;
            if (board.isWhiteToMove()) {
                updateBlackMaterial = true;
                updateBlackWeightedPieces = true;
                updateWhiteKingPawnShield = true;
            } else {
                updateWhiteMaterial = true;
                updateWhiteWeightedPieces = true;
                updateBlackKingPawnShield = true;
            }
        }

        PieceType capturedPiece = board.getGameState().getCapturedPiece();
        if (capturedPiece != null) {
            if (board.isWhiteToMove()) {
                updateWhiteMaterial = true;
                updateWhiteCapture = true;
                updateBlackWeightedPieces = true;
                updateBlackKingPawnShield = true;
            } else {
                updateBlackMaterial = true;
                updateBlackCapture = true;
                updateWhiteWeightedPieces = true;
                updateWhiteKingPawnShield = true;
            }
            if (capturedPiece == PieceType.PAWN) {
                updatePawnStructure = true;
            }
            if (capturedPiece == PieceType.PAWN) {
                updateWhiteKingPawnShield = true;
                updateBlackKingPawnShield = true;
            }
        }

        if (pieceType == PieceType.KING) {
            if (board.isWhiteToMove()) {
                updateBlackKingPawnShield = true;
            } else {
                updateWhiteKingPawnShield = true;
            }
        }

        if (pieceType == PieceType.PAWN) {
            // Any pawn move can create passed/backward/doubled pawns for either side.
            updatePawnStructure = true;
            updateWhiteKingPawnShield = true;
            updateBlackKingPawnShield = true;
        }

        Material whiteMaterial = whiteEval.getMaterial();
        Material blackMaterial = blackEval.getMaterial();
        PiecePlacementScore whitePieceScore = whiteEval.getPiecePlacementScore();
        PiecePlacementScore blackPieceScore = blackEval.getPiecePlacementScore();
        int whitePawnStructureScore = whiteEval.getPawnStructureScore();
        int blackPawnStructureScore = blackEval.getPawnStructureScore();
        int whiteKingPawnShieldScore = whiteEval.getKingPawnShieldScore();
        int blackKingPawnShieldScore = blackEval.getKingPawnShieldScore();

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
            whitePieceScore = piecePlacementEvaluator.updateWeightedPieces(board, blackMaterial.phase(), whitePieceScore, true);
        }
        if (updateBlackWeightedPieces) {
            blackPieceScore = piecePlacementEvaluator.updateWeightedPieces(board, whiteMaterial.phase(), blackPieceScore, false);
        }

        if (board.isWhiteToMove()) {
            blackPieceScore = piecePlacementEvaluator.handleMove(board, whiteMaterial.phase(), blackPieceScore, move);
        } else {
            whitePieceScore = piecePlacementEvaluator.handleMove(board, blackMaterial.phase(), whitePieceScore, move);
        }

        if (updateWhiteCapture) {
            whitePieceScore = piecePlacementEvaluator.handleCapture(board, blackMaterial.phase(), whitePieceScore, capturedPiece);
        }
        if (updateBlackCapture) {
            blackPieceScore = piecePlacementEvaluator.handleCapture(board, whiteMaterial.phase(), blackPieceScore, capturedPiece);
        }
        if (updateWhiteKingPawnShield) {
            whiteKingPawnShieldScore = kingPawnShieldEvaluator.evaluate(board, blackMaterial, true);
        }
        if (updateBlackKingPawnShield) {
            blackKingPawnShieldScore = kingPawnShieldEvaluator.evaluate(board, whiteMaterial, false);
        }

        int whiteMopUpScore = mopUpEvaluator.evaluate(board, whiteMaterial, blackMaterial, true);
        int blackMopUpScore = mopUpEvaluator.evaluate(board, blackMaterial, whiteMaterial, false);

        this.whiteEval = new EvaluationResult(whiteMaterial, whitePieceScore, whitePawnStructureScore, whiteKingPawnShieldScore, whiteMopUpScore);
        this.blackEval = new EvaluationResult(blackMaterial, blackPieceScore, blackPawnStructureScore, blackKingPawnShieldScore, blackMopUpScore);

    }

    @Override
    public void unmakeMove() {
        whiteEval = whiteEvalHistory.pop();
        blackEval = blackEvalHistory.pop();
    }

    @Override
    public int get() {
        int whiteScore = whiteEval.sum();
        int blackScore = blackEval.sum();
        int modifier = board.isWhiteToMove() ? 1 : -1;
        return modifier * (whiteScore - blackScore);
    }

}
