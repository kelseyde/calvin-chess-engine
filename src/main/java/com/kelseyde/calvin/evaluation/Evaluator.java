package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;

import java.util.ArrayDeque;
import java.util.Deque;

public class Evaluator implements Evaluation {

    private Board board;

    int whiteMaterialScore;
    int blackMaterialScore;

    int whitePiecePlacementScore;
    int blackPiecePlacementScore;

    int whitePawnStructureScore;
    int blackPawnStructureScore;

    int whiteKingSafetyScore;
    int blackKingSafetyScore;

    int whiteMopUpScore;
    int blackMopUpScore;

    float phase;

    int eval;

    private final Deque<Integer> evalHistory = new ArrayDeque<>();

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

        phase = GamePhase.fromMaterial(whiteMaterial, blackMaterial);
        whiteMaterialScore = GamePhase.taperedEval(whiteMaterialMiddlegameScore, whiteMaterialEndgameScore, phase);
        whitePiecePlacementScore = GamePhase.taperedEval(whitePiecePlacementMiddlegameScore, whitePiecePlacementEndgameScore, phase);
        blackMaterialScore = GamePhase.taperedEval(blackMaterialMiddlegameScore, blackMaterialEndgameScore, phase);
        blackPiecePlacementScore = GamePhase.taperedEval(blackPiecePlacementMiddlegameScore, blackPiecePlacementEndgameScore, phase);

        whitePawnStructureScore = PawnStructure.score(board.getPawns(true), board.getPawns(false), true);
        blackPawnStructureScore = PawnStructure.score(board.getPawns(false), board.getPawns(true), false);

        whiteKingSafetyScore = KingSafety.score(board, blackMaterial, phase, true);
        blackKingSafetyScore = KingSafety.score(board, whiteMaterial, phase, false);

        whiteMopUpScore = MopUp.score(board, whiteMaterial, blackMaterial, true);
        blackMopUpScore = MopUp.score(board, blackMaterial, whiteMaterial, false);

        int whiteScore = whiteMaterialScore + whitePiecePlacementScore + whitePawnStructureScore + whiteKingSafetyScore + whiteMopUpScore;
        int blackScore = blackMaterialScore + blackPiecePlacementScore + blackPawnStructureScore + blackKingSafetyScore + blackMopUpScore;

        int modifier = board.isWhiteToMove() ? 1 : -1;
        eval = modifier * (whiteScore - blackScore);

    }

    @Override
    public void makeMove(Move move) {

//        evalHistory.push(eval);
//
//        PieceType pieceType = board.pieceAt(move.getEndSquare());
//
//        boolean updatePawnStructure = false;
//        boolean updateMaterial = false;
//        boolean updateWhitePiecePlacement = false;
//        boolean updateBlackPiecePlacement = false;
//        boolean updateWhiteKingSafety = false;
//        boolean updateBlackKingSafety = false;
//
//        if (board.isWhiteToMove()) {
//            updateBlackPiecePlacement = true;
//        } else {
//            updateWhitePiecePlacement = true;
//        }
//
//        PieceType capturedPiece = board.getGameState().getCapturedPiece();
//        if (capturedPiece != null) {
//            updateMaterial = true;
//            updateWhiteKingSafety = true;
//            updateBlackKingSafety = true;
//            if (board.isWhiteToMove()) {
//                updateWhitePiecePlacement = true;
//            } else {
//                updateBlackPiecePlacement = true;
//            }
//            if (capturedPiece == PieceType.PAWN) {
//                updatePawnStructure = true;
//            }
//        }
//
//        if (move.isPromotion()) {
//            updateMaterial = true;
//            updateWhitePiecePlacement = true;
//            updateBlackPiecePlacement = true;
//            updatePawnStructure = true;
//        }
//
//        if (pieceType == PieceType.KING) {
//            if (board.isWhiteToMove()) {
//                updateBlackKingSafety = true;
//            } else {
//                updateWhiteKingSafety = true;
//            }
//        }
//
//        if (pieceType == PieceType.PAWN) {
//            // Any pawn move can create passed/backward/doubled pawns for either side.
//            updatePawnStructure = true;
//            updateWhiteKingSafety = true;
//            updateBlackKingSafety = true;
//        }
//
//        Material whiteMaterial = Material.fromBoard(board, true);
//        Material blackMaterial = Material.fromBoard(board, false);
//        phase = GamePhase.fromMaterial(whiteMaterial, blackMaterial);
//
//        if (updateMaterial) {
//            int whiteMaterialMiddlegameScore = whiteMaterial.sum(PieceValues.MIDDLEGAME_VALUES);
//            int whiteMaterialEndgameScore = whiteMaterial.sum(PieceValues.ENDGAME_VALUES);
//            whiteMaterialScore = GamePhase.taperedEval(whiteMaterialMiddlegameScore, whiteMaterialEndgameScore, phase);
//            int blackMaterialMiddlegameScore = blackMaterial.sum(PieceValues.MIDDLEGAME_VALUES);
//            int blackMaterialEndgameScore = blackMaterial.sum(PieceValues.ENDGAME_VALUES);
//            blackMaterialScore = GamePhase.taperedEval(blackMaterialMiddlegameScore, blackMaterialEndgameScore, phase);
//        }
//
//        if (updateWhitePiecePlacement) {
//            PiecePlacement whitePiecePlacement = PiecePlacement.fromBoard(board, true);
//            int whitePiecePlacementMiddlegameScore = whitePiecePlacement.sum(PieceSquareTable.MIDDLEGAME_TABLES, true);
//            int whitePiecePlacementEndgameScore = whitePiecePlacement.sum(PieceSquareTable.ENDGAME_TABLES, true);
//            whitePiecePlacementScore = GamePhase.taperedEval(whitePiecePlacementMiddlegameScore, whitePiecePlacementEndgameScore, phase);
//        }
//
//        if (updateBlackPiecePlacement) {
//            PiecePlacement blackPiecePlacement = PiecePlacement.fromBoard(board, false);
//            int blackPiecePlacementMiddlegameScore = blackPiecePlacement.sum(PieceSquareTable.MIDDLEGAME_TABLES, false);
//            int blackPiecePlacementEndgameScore = blackPiecePlacement.sum(PieceSquareTable.ENDGAME_TABLES, false);
//            blackPiecePlacementScore = GamePhase.taperedEval(blackPiecePlacementMiddlegameScore, blackPiecePlacementEndgameScore, phase);
//        }
//
//        if (updatePawnStructure) {
//            whitePawnStructureScore = PawnStructure.score(board.getPawns(true), board.getPawns(false), true);
//            blackPawnStructureScore = PawnStructure.score(board.getPawns(false), board.getPawns(true), false);
//        }
//
//        if (updateWhiteKingSafety) {
//            whiteKingSafetyScore = KingSafety.score(board, blackMaterial, phase, true);
//        }
//        if (updateBlackKingSafety) {
//            blackKingSafetyScore = KingSafety.score(board, whiteMaterial, phase, false);
//        }
//
//        whiteMopUpScore = MopUp.score(board, whiteMaterial, blackMaterial, true);
//        blackMopUpScore = MopUp.score(board, blackMaterial, whiteMaterial, false);
//
//        int whiteScore = whiteMaterialScore + whitePiecePlacementScore + whitePawnStructureScore + whiteKingSafetyScore + whiteMopUpScore;
//        int blackScore = blackMaterialScore + blackPiecePlacementScore + blackPawnStructureScore + blackKingSafetyScore + blackMopUpScore;
//
//        int modifier = board.isWhiteToMove() ? 1 : -1;
//        eval = modifier * (whiteScore - blackScore);

    }

    @Override
    public void unmakeMove() {
//        eval = evalHistory.pop();
    }

    @Override
    public int get() {
        init(board);
        return eval;
    }

}
