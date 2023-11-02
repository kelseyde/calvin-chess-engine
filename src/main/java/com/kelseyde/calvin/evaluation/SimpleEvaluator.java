package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;

public class SimpleEvaluator implements Evaluator {

    private Board board;

    int eval;

    public SimpleEvaluator(Board board) {
        init(board);
    }

    @Override
    public void init(Board board) {

        this.board = board;
        int whiteMiddlegameScore = 0;
        int whiteEndgameScore = 0;
        int blackMiddlegameScore = 0;
        int blackEndgameScore = 0;

        Material whiteMaterial = Material.fromBoard(board, true);
        Material blackMaterial = Material.fromBoard(board, false);

        whiteMiddlegameScore += whiteMaterial.sum(PieceValues.MIDDLEGAME_VALUES);
        whiteEndgameScore += whiteMaterial.sum(PieceValues.ENDGAME_VALUES);
        blackMiddlegameScore += blackMaterial.sum(PieceValues.MIDDLEGAME_VALUES);
        blackEndgameScore += blackMaterial.sum(PieceValues.ENDGAME_VALUES);

        PiecePlacement whitePiecePlacement = PiecePlacement.fromBoard(board, true);
        PiecePlacement blackPiecePlacement = PiecePlacement.fromBoard(board, false);

        whiteMiddlegameScore += whitePiecePlacement.sum(PieceSquareTable.MIDDLEGAME_TABLES, true);
        whiteEndgameScore += whitePiecePlacement.sum(PieceSquareTable.ENDGAME_TABLES, true);
        blackMiddlegameScore += blackPiecePlacement.sum(PieceSquareTable.MIDDLEGAME_TABLES, false);
        blackEndgameScore += blackPiecePlacement.sum(PieceSquareTable.ENDGAME_TABLES, false);

        float phase = GamePhase.fromMaterial(whiteMaterial, blackMaterial);
        int whiteScore = GamePhase.taperedEval(whiteMiddlegameScore, whiteEndgameScore, phase);
        int blackScore = GamePhase.taperedEval(blackMiddlegameScore, blackEndgameScore, phase);

        whiteScore += PawnStructure.score(board.getPawns(true), board.getPawns(false), true);
        blackScore += PawnStructure.score(board.getPawns(false), board.getPawns(true), false);

        whiteScore += KingSafety.score(board, blackMaterial, phase, true);
        blackScore += KingSafety.score(board, whiteMaterial, phase, false);

        whiteScore += MopUp.score(board, whiteMaterial, blackMaterial, phase, true);
        blackScore += MopUp.score(board, blackMaterial, whiteMaterial, phase, true);

        int modifier = board.isWhiteToMove() ? 1 : -1;
        eval = modifier * (whiteScore - blackScore);

    }

    @Override
    public void makeMove(Move move) {

    }

    @Override
    public void unmakeMove() {

    }

    @Override
    public int get() {
        init(board);
        return eval;
    }

}
