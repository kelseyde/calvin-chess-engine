package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.evaluation.score.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Evaluates the current board position. Uses various heuristics to calculate a numeric value, in centipawns, estimating
 * how good the current position is for the side to move. This means that a positive score indicates the position is better
 * for the side to move, regardless of whether they are white or black.
 * <p>
 * Also includes logic for incrementally updating the evaluation during make/unmake move, which saves some time during the
 * search procedure.
 * @see <a href="https://www.chessprogramming.org/Evaluation">Chess Programming Wiki</a>
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Evaluator implements Evaluation {

    final EngineConfig config;
    final Deque<Score> scoreHistory = new ArrayDeque<>();
    Score score;
    Board board;

    public Evaluator(EngineConfig config, Board board) {
        this.config = config;
        init(board);
    }

    @Override
    public void init(Board board) {

        this.board = board;

        Material whiteMaterial = Material.fromBoard(board, true);
        Material blackMaterial = Material.fromBoard(board, false);
        int whiteMaterialMiddlegameScore = whiteMaterial.sum(config.getPieceValues()[0]);
        int whiteMaterialEndgameScore = whiteMaterial.sum(config.getPieceValues()[1]);
        int blackMaterialMiddlegameScore = blackMaterial.sum(config.getPieceValues()[0]);
        int blackMaterialEndgameScore = blackMaterial.sum(config.getPieceValues()[1]);

        PiecePlacement whitePiecePlacement = PiecePlacement.fromBoard(board, true);
        PiecePlacement blackPiecePlacement = PiecePlacement.fromBoard(board, false);
        int whitePiecePlacementMiddlegameScore = whitePiecePlacement.sum(config.getMiddlegameTables(), true);
        int whitePiecePlacementEndgameScore = whitePiecePlacement.sum(config.getEndgameTables(), true);
        int blackPiecePlacementMiddlegameScore = blackPiecePlacement.sum(config.getMiddlegameTables(), false);
        int blackPiecePlacementEndgameScore = blackPiecePlacement.sum(config.getEndgameTables(), false);

        float phase = Phase.fromMaterial(whiteMaterial, blackMaterial);
        int whiteMaterialScore = Phase.taperedEval(whiteMaterialMiddlegameScore, whiteMaterialEndgameScore, phase);
        int whitePiecePlacementScore = Phase.taperedEval(whitePiecePlacementMiddlegameScore, whitePiecePlacementEndgameScore, phase);
        int blackMaterialScore = Phase.taperedEval(blackMaterialMiddlegameScore, blackMaterialEndgameScore, phase);
        int blackPiecePlacementScore = Phase.taperedEval(blackPiecePlacementMiddlegameScore, blackPiecePlacementEndgameScore, phase);

        int whiteMobilityScore = Mobility.score(config, board, true, phase);
        int blackMobilityScore = Mobility.score(config, board, false, phase);

        int whitePawnStructureScore = PawnEvaluation.score(config, board.getPawns(true), board.getPawns(false), phase, true);
        int blackPawnStructureScore = PawnEvaluation.score(config, board.getPawns(false), board.getPawns(true), phase, false);

        int whiteKingSafetyScore = KingSafety.score(config, board, blackMaterial, phase, true);
        int blackKingSafetyScore = KingSafety.score(config, board, whiteMaterial, phase, false);

        int whiteRookScore = RookEvaluation.score(config, board, phase, true);
        int blackRookScore = RookEvaluation.score(config, board, phase, false);

        int whiteMopUpScore = MopUp.score(config, board, whiteMaterial, blackMaterial, true);
        int blackMopUpScore = MopUp.score(config, board, blackMaterial, whiteMaterial, false);

        int whiteTempoBonus = board.isWhiteToMove() ? config.getTempoBonus() : 0;
        int blackTempoBonus = board.isWhiteToMove() ? 0 : config.getTempoBonus();

        score = Score.builder()
                .whiteMaterial(whiteMaterial)
                .whiteMaterialScore(whiteMaterialScore)
                .whitePiecePlacementScore(whitePiecePlacementScore)
                .whiteMobilityScore(whiteMobilityScore)
                .whitePawnStructureScore(whitePawnStructureScore)
                .whiteKingSafetyScore(whiteKingSafetyScore)
                .whiteRookScore(whiteRookScore)
                .whiteMopUpScore(whiteMopUpScore)
                .blackMaterial(blackMaterial)
                .blackMaterialScore(blackMaterialScore)
                .blackPiecePlacementScore(blackPiecePlacementScore)
                .blackMobilityScore(blackMobilityScore)
                .blackPawnStructureScore(blackPawnStructureScore)
                .blackKingSafetyScore(blackKingSafetyScore)
                .blackRookScore(blackRookScore)
                .blackMopUpScore(blackMopUpScore)
                .whiteTempoBonus(whiteTempoBonus)
                .blackTempoBonus(blackTempoBonus)
                .build();

    }

    @Override
    public void makeMove(Move move) {

        scoreHistory.push(score);

        Piece pieceType = board.pieceAt(move.getEndSquare());

        boolean updatePawnStructure = false;
        boolean updateMaterial = false;
        boolean updateWhitePiecePlacement = false;
        boolean updateBlackPiecePlacement = false;
        boolean updateWhiteKingSafety = false;
        boolean updateBlackKingSafety = false;

        if (board.isWhiteToMove()) {
            updateBlackPiecePlacement = true;
        } else {
            updateWhitePiecePlacement = true;
        }

        Piece capturedPiece = board.getGameState().getCapturedPiece();
        if (capturedPiece != null) {
            updateMaterial = true;
            updateWhiteKingSafety = true;
            updateBlackKingSafety = true;
            updatePawnStructure = true;
            if (board.isWhiteToMove()) {
                updateWhitePiecePlacement = true;
            } else {
                updateBlackPiecePlacement = true;
            }
        }

        if (move.isPromotion()) {
            updateMaterial = true;
            updateWhitePiecePlacement = true;
            updateBlackPiecePlacement = true;
            updatePawnStructure = true;
        }

        if (pieceType == Piece.KING) {
            if (board.isWhiteToMove()) {
                updateBlackKingSafety = true;
            } else {
                updateWhiteKingSafety = true;
            }
        }

        if (pieceType == Piece.PAWN) {
            // Any pawn move can create passed/backward/doubled pawns for either side.
            updatePawnStructure = true;
            updateWhiteKingSafety = true;
            updateBlackKingSafety = true;
        }

        int whiteMaterialScore = score.getWhiteMaterialScore();
        int whitePiecePlacementScore = score.getWhitePiecePlacementScore();
        int whitePawnStructureScore = score.getWhitePawnStructureScore();
        int whiteKingSafetyScore = score.getWhiteKingSafetyScore();
        int blackMaterialScore = score.getBlackMaterialScore();
        int blackPiecePlacementScore = score.getBlackPiecePlacementScore();
        int blackPawnStructureScore = score.getBlackPawnStructureScore();
        int blackKingSafetyScore = score.getBlackKingSafetyScore();

        Material whiteMaterial = Material.fromBoard(board, true);
        Material blackMaterial = Material.fromBoard(board, false);
        float phase = Phase.fromMaterial(whiteMaterial, blackMaterial);

        if (updateMaterial) {
            int whiteMaterialMiddlegameScore = whiteMaterial.sum(config.getPieceValues()[0]);
            int whiteMaterialEndgameScore = whiteMaterial.sum(config.getPieceValues()[1]);
            whiteMaterialScore = Phase.taperedEval(whiteMaterialMiddlegameScore, whiteMaterialEndgameScore, phase);
            int blackMaterialMiddlegameScore = blackMaterial.sum(config.getPieceValues()[0]);
            int blackMaterialEndgameScore = blackMaterial.sum(config.getPieceValues()[1]);
            blackMaterialScore = Phase.taperedEval(blackMaterialMiddlegameScore, blackMaterialEndgameScore, phase);
        }

        if (updateWhitePiecePlacement) {
            PiecePlacement whitePiecePlacement = PiecePlacement.fromBoard(board, true);
            int whitePiecePlacementMiddlegameScore = whitePiecePlacement.sum(config.getMiddlegameTables(), true);
            int whitePiecePlacementEndgameScore = whitePiecePlacement.sum(config.getEndgameTables(), true);
            whitePiecePlacementScore = Phase.taperedEval(whitePiecePlacementMiddlegameScore, whitePiecePlacementEndgameScore, phase);
        }
        if (updateBlackPiecePlacement) {
            PiecePlacement blackPiecePlacement = PiecePlacement.fromBoard(board, false);
            int blackPiecePlacementMiddlegameScore = blackPiecePlacement.sum(config.getMiddlegameTables(), false);
            int blackPiecePlacementEndgameScore = blackPiecePlacement.sum(config.getEndgameTables(), false);
            blackPiecePlacementScore = Phase.taperedEval(blackPiecePlacementMiddlegameScore, blackPiecePlacementEndgameScore, phase);
        }

        if (updatePawnStructure) {
            whitePawnStructureScore = PawnEvaluation.score(config, board.getPawns(true), board.getPawns(false), phase, true);
            blackPawnStructureScore = PawnEvaluation.score(config, board.getPawns(false), board.getPawns(true), phase, false);
        }

        if (updateWhiteKingSafety) {
            whiteKingSafetyScore = KingSafety.score(config, board, blackMaterial, phase, true);
        }
        if (updateBlackKingSafety) {
            blackKingSafetyScore = KingSafety.score(config, board, whiteMaterial, phase, false);
        }

        int whiteMobilityScore = Mobility.score(config, board, true, phase);
        int blackMobilityScore = Mobility.score(config, board, false, phase);

        int whiteRookScore = RookEvaluation.score(config, board, phase, true);
        int blackRookScore = RookEvaluation.score(config, board, phase, false);

        int whiteMopUpScore = MopUp.score(config, board, whiteMaterial, blackMaterial, true);
        int blackMopUpScore = MopUp.score(config, board, blackMaterial, whiteMaterial, false);

        int whiteTempoBonus = board.isWhiteToMove() ? config.getTempoBonus() : 0;
        int blackTempoBonus = board.isWhiteToMove() ? 0 : config.getTempoBonus();

        score = Score.builder()
                .whiteMaterial(whiteMaterial)
                .whiteMaterialScore(whiteMaterialScore)
                .whitePiecePlacementScore(whitePiecePlacementScore)
                .whiteMobilityScore(whiteMobilityScore)
                .whitePawnStructureScore(whitePawnStructureScore)
                .whiteKingSafetyScore(whiteKingSafetyScore)
                .whiteRookScore(whiteRookScore)
                .whiteMopUpScore(whiteMopUpScore)
                .blackMaterial(blackMaterial)
                .blackMaterialScore(blackMaterialScore)
                .blackPiecePlacementScore(blackPiecePlacementScore)
                .blackMobilityScore(blackMobilityScore)
                .blackPawnStructureScore(blackPawnStructureScore)
                .blackKingSafetyScore(blackKingSafetyScore)
                .blackRookScore(blackRookScore)
                .blackMopUpScore(blackMopUpScore)
                .whiteTempoBonus(whiteTempoBonus)
                .blackTempoBonus(blackTempoBonus)
                .build();

    }

    @Override
    public void unmakeMove() {
        score = scoreHistory.pop();
    }

    @Override
    public int get() {
        return score.sum(board.isWhiteToMove());
    }

    public Material getMaterial(boolean isWhite) {
        return isWhite ? score.getWhiteMaterial() : score.getBlackMaterial();
    }

}
