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
public class ComplexEvaluator implements Evaluator {

    private final MaterialEvaluator materialEvaluator = new MaterialEvaluator();

    private final PiecePlacementEvaluator piecePlacementEvaluator = new PiecePlacementEvaluator();

    private final PawnStructureEvaluator pawnStructureEvaluator = new PawnStructureEvaluator();

    private final KingPawnShieldEvaluator kingPawnShieldEvaluator = new KingPawnShieldEvaluator();

    private final MopUpEvaluator mopUpEvaluator = new MopUpEvaluator();

    private final Deque<Evaluation> whiteEvalHistory = new ArrayDeque<>();
    private final Deque<Evaluation> blackEvalHistory = new ArrayDeque<>();

    private @Getter Evaluation whiteEval;
    private @Getter Evaluation blackEval;

    private Board board;

    public ComplexEvaluator(Board board) {
        init(board);
    }

    @Override
    public void init(Board board) {
        this.board = board;
        this.whiteEval = new Evaluation();
        this.blackEval = new Evaluation();

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
        boolean updateWhiteCapturedPiece = false;
        boolean updateBlackCapturedPiece = false;
        boolean updateWhitePiecePlacement = false;
        boolean updateBlackPiecePlacement = false;
        boolean updateWhiteKingPawnShield = false;
        boolean updateBlackKingPawnShield = false;

        if (move.isPromotion()) {
            updatePawnStructure = true;
            if (board.isWhiteToMove()) {
                updateBlackMaterial = true;
                updateWhitePiecePlacement = true;
                updateWhiteKingPawnShield = true;
            } else {
                updateWhiteMaterial = true;
                updateBlackPiecePlacement = true;
                updateBlackKingPawnShield = true;
            }
        }

        PieceType capturedPiece = board.getGameState().getCapturedPiece();
        if (capturedPiece != null) {
            if (board.isWhiteToMove()) {
                updateWhiteMaterial = true;
                updateBlackKingPawnShield = true;
                updateWhitePiecePlacement = true;
                if (capturedPiece != PieceType.PAWN) {
                    updateBlackCapturedPiece = true;
                }
            } else {
                updateBlackMaterial = true;
                updateWhiteKingPawnShield = true;
                updateBlackPiecePlacement = true;
                if (capturedPiece != PieceType.PAWN) {
                    updateWhiteCapturedPiece = true;
                }
            }
            if (capturedPiece == PieceType.PAWN) {
                updatePawnStructure = true;
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

        if (updateWhitePiecePlacement) {
            whitePieceScore = piecePlacementEvaluator.evaluate(board, blackMaterial.phase(), true);
        }

        if (updateBlackPiecePlacement) {
            blackPieceScore = piecePlacementEvaluator.evaluate(board, whiteMaterial.phase(), false);
        }

        if (board.isWhiteToMove() && !updateBlackPiecePlacement) {
            blackPieceScore = piecePlacementEvaluator.handleMove(board, whiteMaterial.phase(), blackPieceScore, move);
        }
        else if (!board.isWhiteToMove() && !updateWhitePiecePlacement) {
            whitePieceScore = piecePlacementEvaluator.handleMove(board, blackMaterial.phase(), whitePieceScore, move);
        }

        if (updateWhiteCapturedPiece) {
            whitePieceScore = piecePlacementEvaluator.evaluate(board, blackMaterial.phase(), true);
        }
        if (updateBlackCapturedPiece) {
            blackPieceScore = piecePlacementEvaluator.evaluate(board, whiteMaterial.phase(), false);
        }
        if (updateWhiteKingPawnShield) {
            whiteKingPawnShieldScore = kingPawnShieldEvaluator.evaluate(board, blackMaterial, true);
        }
        if (updateBlackKingPawnShield) {
            blackKingPawnShieldScore = kingPawnShieldEvaluator.evaluate(board, whiteMaterial, false);
        }

        int whiteMopUpScore = mopUpEvaluator.evaluate(board, whiteMaterial, blackMaterial, true);
        int blackMopUpScore = mopUpEvaluator.evaluate(board, blackMaterial, whiteMaterial, false);

        this.whiteEval = new Evaluation(whiteMaterial, whitePieceScore, whitePawnStructureScore, whiteKingPawnShieldScore, whiteMopUpScore);
        this.blackEval = new Evaluation(blackMaterial, blackPieceScore, blackPawnStructureScore, blackKingPawnShieldScore, blackMopUpScore);

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
