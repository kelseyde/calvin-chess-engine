package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.search.SearchStack.PlayedMove;
import com.kelseyde.calvin.tables.history.*;
import lombok.Data;

import java.util.List;

@Data
public class SearchHistory {

    private KillerTable killerTable = new KillerTable();
    private HistoryTable historyTable = new HistoryTable();
    private ContinuationHistoryTable contHistTable = new ContinuationHistoryTable();
    private CaptureHistoryTable captureHistoryTable = new CaptureHistoryTable();
    private CorrectionHistoryTable pawnCorrHistTable = new CorrectionHistoryTable();
    private CorrectionHistoryTable nonPawnCorrHistTable = new CorrectionHistoryTable();

    private int bestMoveStability = 0;
    private int bestScoreStability = 0;

    public void updateQuietHistory(
            PlayedMove bestMove, boolean white, int depth, int ply, SearchStack ss, List<PlayedMove> quietsSearched, List<PlayedMove> capturesSearched) {

        killerTable.add(ply, bestMove.getMove());

        for (PlayedMove quietMove : quietsSearched) {
            boolean good = bestMove.getMove().equals(quietMove.getMove());
            historyTable.update(quietMove.getMove(), depth, white, good);
//
//            Move prevMove = ss.getMove(ply - 1);
//            Piece prevPiece = ss.getMovedPiece(ply - 1);
//            contHistTable.update(prevMove, prevPiece, quietMove.getMove(), quietMove.getPiece(), depth, white, good);
        }

        for (PlayedMove captureMove : capturesSearched) {
            boolean good = bestMove.equals(captureMove);
            Piece piece = captureMove.getPiece();
            int to = captureMove.getMove().getTo();
            Piece captured = captureMove.getCaptured();
            captureHistoryTable.update(piece, to, captured, depth, white, good);
        }

    }

    public void updateCaptureHistory(PlayedMove bestMove, boolean white, int depth, List<PlayedMove> capturesSearched) {
        for (PlayedMove capture : capturesSearched) {
            boolean good = bestMove.equals(capture);
            Piece piece = capture.getPiece();
            int to = capture.getMove().getTo();
            Piece captured = capture.getCaptured();
            captureHistoryTable.update(piece, to, captured, depth, white, good);
        }
    }

    public void updateBestMoveStability(Move bestMovePrevious, Move bestMoveCurrent) {
        if (bestMovePrevious == null || bestMoveCurrent == null) {
            return;
        }
        bestMoveStability = bestMovePrevious.equals(bestMoveCurrent) ? bestMoveStability + 1 : 0;
    }

    public void updateBestScoreStability(int scorePrevious, int scoreCurrent) {
        bestScoreStability = scoreCurrent >= scorePrevious - 10 && scoreCurrent <= scorePrevious + 10 ? bestScoreStability + 1 : 0;
    }

    public int correctEvaluation(Board board, int staticEval) {
        boolean white = board.isWhiteToMove();

        int pawnCorrection = pawnCorrHistTable.getCorrection(board.pawnKey(), white);
        int whiteNonPawnCorrection = nonPawnCorrHistTable.getCorrection(board.nonPawnKey(true), white);
        int blackNonPawnCorrection = nonPawnCorrHistTable.getCorrection(board.nonPawnKey(false), white);

        return staticEval + pawnCorrection + (whiteNonPawnCorrection + blackNonPawnCorrection) / 2;
    }

    public void updateCorrectionHistory(Board board, int depth, int score, int staticEval) {
        boolean white = board.isWhiteToMove();
        pawnCorrHistTable.update(board.pawnKey(), white, depth, score, staticEval);
        nonPawnCorrHistTable.update(board.nonPawnKey(true), white, depth, score, staticEval);
        nonPawnCorrHistTable.update(board.nonPawnKey(false), white, depth, score, staticEval);
    }

    public void reset() {
        bestMoveStability = 0;
        bestScoreStability = 0;
        historyTable.ageScores(true);
        historyTable.ageScores(false);
    }

    public void clear() {
        killerTable.clear();
        historyTable.clear();
        captureHistoryTable.clear();
        pawnCorrHistTable.clear();
        nonPawnCorrHistTable.clear();
    }

}
