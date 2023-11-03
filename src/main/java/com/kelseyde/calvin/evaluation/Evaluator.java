package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;

import java.util.ArrayDeque;
import java.util.Deque;

public class Evaluator implements Evaluation {

    private Board board;

    private EvaluationScore score;

    private final Deque<EvaluationScore> evalHistory = new ArrayDeque<>();

    public Evaluator(Board board) {
        init(board);
    }

    @Override
    public void init(Board board) {

        this.board = board;

        Material whiteMaterial = Material.fromBoard(board, true);
        Material blackMaterial = Material.fromBoard(board, false);
        int whiteMaterialMiddlegameScore = whiteMaterial.sum(PieceValues.MIDDLEGAME_VALUES);
        int whiteMaterialEndgameScore = whiteMaterial.sum(PieceValues.ENDGAME_VALUES);
        int blackMaterialMiddlegameScore = blackMaterial.sum(PieceValues.MIDDLEGAME_VALUES);
        int blackMaterialEndgameScore = blackMaterial.sum(PieceValues.ENDGAME_VALUES);

        PiecePlacement whitePiecePlacement = PiecePlacement.fromBoard(board, true);
        PiecePlacement blackPiecePlacement = PiecePlacement.fromBoard(board, false);
        int whitePiecePlacementMiddlegameScore = whitePiecePlacement.sum(PieceSquareTable.MIDDLEGAME_TABLES, true);
        int whitePiecePlacementEndgameScore = whitePiecePlacement.sum(PieceSquareTable.ENDGAME_TABLES, true);
        int blackPiecePlacementMiddlegameScore = blackPiecePlacement.sum(PieceSquareTable.MIDDLEGAME_TABLES, false);
        int blackPiecePlacementEndgameScore = blackPiecePlacement.sum(PieceSquareTable.ENDGAME_TABLES, false);

        float phase = GamePhase.fromMaterial(whiteMaterial, blackMaterial);
        int whiteMaterialScore = GamePhase.taperedEval(whiteMaterialMiddlegameScore, whiteMaterialEndgameScore, phase);
        int whitePiecePlacementScore = GamePhase.taperedEval(whitePiecePlacementMiddlegameScore, whitePiecePlacementEndgameScore, phase);
        int blackMaterialScore = GamePhase.taperedEval(blackMaterialMiddlegameScore, blackMaterialEndgameScore, phase);
        int blackPiecePlacementScore = GamePhase.taperedEval(blackPiecePlacementMiddlegameScore, blackPiecePlacementEndgameScore, phase);

        int whitePawnStructureScore = PawnStructure.score(board.getPawns(true), board.getPawns(false), true);
        int blackPawnStructureScore = PawnStructure.score(board.getPawns(false), board.getPawns(true), false);

        int whiteKingSafetyScore = KingSafety.score(board, blackMaterial, phase, true);
        int blackKingSafetyScore = KingSafety.score(board, whiteMaterial, phase, false);

        int whiteMopUpScore = MopUp.score(board, whiteMaterial, blackMaterial, true);
        int blackMopUpScore = MopUp.score(board, blackMaterial, whiteMaterial, false);

        score = EvaluationScore.builder()
                .whiteMaterialScore(whiteMaterialScore)
                .whitePiecePlacementScore(whitePiecePlacementScore)
                .whitePawnStructureScore(whitePawnStructureScore)
                .whiteKingSafetyScore(whiteKingSafetyScore)
                .whiteMopUpScore(whiteMopUpScore)
                .blackMaterialScore(blackMaterialScore)
                .blackPiecePlacementScore(blackPiecePlacementScore)
                .blackPawnStructureScore(blackPawnStructureScore)
                .blackKingSafetyScore(blackKingSafetyScore)
                .blackMopUpScore(blackMopUpScore)
                .build();

    }

    @Override
    public void makeMove(Move move) {

        evalHistory.push(score);

        PieceType pieceType = board.pieceAt(move.getEndSquare());

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

        PieceType capturedPiece = board.getGameState().getCapturedPiece();
        if (capturedPiece != null) {
            updateMaterial = true;
            updateWhiteKingSafety = true;
            updateBlackKingSafety = true;
            if (board.isWhiteToMove()) {
                updateWhitePiecePlacement = true;
            } else {
                updateBlackPiecePlacement = true;
            }
            if (capturedPiece == PieceType.PAWN) {
                updatePawnStructure = true;
            }
        }

        if (move.isPromotion()) {
            updateMaterial = true;
            updateWhitePiecePlacement = true;
            updateBlackPiecePlacement = true;
            updatePawnStructure = true;
        }

        if (pieceType == PieceType.KING) {
            if (board.isWhiteToMove()) {
                updateBlackKingSafety = true;
            } else {
                updateWhiteKingSafety = true;
            }
        }

        if (pieceType == PieceType.PAWN) {
            // Any pawn move can create passed/backward/doubled pawns for either side.
            updatePawnStructure = true;
            updateWhiteKingSafety = true;
            updateBlackKingSafety = true;
        }

        int whiteMaterialScore = score.getWhiteMaterialScore();
        int whitePiecePlacementScore = score.getWhitePiecePlacementScore();
        int whitePawnStructureScore = score.getWhitePawnStructureScore();
        int whiteKingSafetyScore = score.getWhiteKingSafetyScore();
        int whiteMopUpScore = score.getWhiteMopUpScore();
        int blackMaterialScore = score.getBlackMaterialScore();
        int blackPiecePlacementScore = score.getBlackPiecePlacementScore();
        int blackPawnStructureScore = score.getBlackPawnStructureScore();
        int blackKingSafetyScore = score.getBlackKingSafetyScore();
        int blackMopUpScore = score.getBlackMopUpScore();

        Material whiteMaterial = Material.fromBoard(board, true);
        Material blackMaterial = Material.fromBoard(board, false);
        float phase = GamePhase.fromMaterial(whiteMaterial, blackMaterial);

        if (updateMaterial) {
            int whiteMaterialMiddlegameScore = whiteMaterial.sum(PieceValues.MIDDLEGAME_VALUES);
            int whiteMaterialEndgameScore = whiteMaterial.sum(PieceValues.ENDGAME_VALUES);
            whiteMaterialScore = GamePhase.taperedEval(whiteMaterialMiddlegameScore, whiteMaterialEndgameScore, phase);
            int blackMaterialMiddlegameScore = blackMaterial.sum(PieceValues.MIDDLEGAME_VALUES);
            int blackMaterialEndgameScore = blackMaterial.sum(PieceValues.ENDGAME_VALUES);
            blackMaterialScore = GamePhase.taperedEval(blackMaterialMiddlegameScore, blackMaterialEndgameScore, phase);
        }

        if (updateWhitePiecePlacement) {
            PiecePlacement whitePiecePlacement = PiecePlacement.fromBoard(board, true);
            int whitePiecePlacementMiddlegameScore = whitePiecePlacement.sum(PieceSquareTable.MIDDLEGAME_TABLES, true);
            int whitePiecePlacementEndgameScore = whitePiecePlacement.sum(PieceSquareTable.ENDGAME_TABLES, true);
            whitePiecePlacementScore = GamePhase.taperedEval(whitePiecePlacementMiddlegameScore, whitePiecePlacementEndgameScore, phase);
        }

        if (updateBlackPiecePlacement) {
            PiecePlacement blackPiecePlacement = PiecePlacement.fromBoard(board, false);
            int blackPiecePlacementMiddlegameScore = blackPiecePlacement.sum(PieceSquareTable.MIDDLEGAME_TABLES, false);
            int blackPiecePlacementEndgameScore = blackPiecePlacement.sum(PieceSquareTable.ENDGAME_TABLES, false);
            blackPiecePlacementScore = GamePhase.taperedEval(blackPiecePlacementMiddlegameScore, blackPiecePlacementEndgameScore, phase);
        }

        if (updatePawnStructure) {
            whitePawnStructureScore = PawnStructure.score(board.getPawns(true), board.getPawns(false), true);
            blackPawnStructureScore = PawnStructure.score(board.getPawns(false), board.getPawns(true), false);
        }

        if (updateWhiteKingSafety) {
            whiteKingSafetyScore = KingSafety.score(board, blackMaterial, phase, true);
        }
        if (updateBlackKingSafety) {
            blackKingSafetyScore = KingSafety.score(board, whiteMaterial, phase, false);
        }

        whiteMopUpScore = MopUp.score(board, whiteMaterial, blackMaterial, true);
        blackMopUpScore = MopUp.score(board, blackMaterial, whiteMaterial, false);

        score = EvaluationScore.builder()
                .whiteMaterialScore(whiteMaterialScore)
                .whitePiecePlacementScore(whitePiecePlacementScore)
                .whitePawnStructureScore(whitePawnStructureScore)
                .whiteKingSafetyScore(whiteKingSafetyScore)
                .whiteMopUpScore(whiteMopUpScore)
                .blackMaterialScore(blackMaterialScore)
                .blackPiecePlacementScore(blackPiecePlacementScore)
                .blackPawnStructureScore(blackPawnStructureScore)
                .blackKingSafetyScore(blackKingSafetyScore)
                .blackMopUpScore(blackMopUpScore)
                .build();

    }

    @Override
    public void unmakeMove() {
        score = evalHistory.pop();
    }

    @Override
    public int get() {
        return score.sum(board.isWhiteToMove());
    }

}
