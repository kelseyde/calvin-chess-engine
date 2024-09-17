package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.search.SearchStack.PlayedMove;
import com.kelseyde.calvin.tables.history.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchHistory {

    final KillerTable killerTable = new KillerTable();
    final HistoryTable historyTable = new HistoryTable();
    final CounterMoveTable counterMoveTable = new CounterMoveTable();
    final CaptureHistoryTable captureHistoryTable = new CaptureHistoryTable();
    final ContinuationHistoryTable contHistTable = new ContinuationHistoryTable();

    int bestMoveStability = 0;
    int bestScoreStability = 0;

    public void updateQuietHistory(
            PlayedMove bestMove, boolean white, int depth, int ply, SearchStack ss, List<PlayedMove> quietsSearched, List<PlayedMove> capturesSearched, boolean failHigh) {

        Move prevMove = ss.getMove(ply - 1);
        Piece prevPiece = ss.getMovedPiece(ply - 1);

        if (failHigh) {
            killerTable.add(ply, bestMove.getMove());
            counterMoveTable.add(prevPiece, prevMove, white, bestMove.getMove());
        }

        for (PlayedMove quiet : quietsSearched) {
            boolean good = bestMove.getMove().equals(quiet.getMove());
            historyTable.update(quiet.getMove(), depth, white, good);
            contHistTable.update(prevMove, prevPiece, quiet.getMove(), quiet.getPiece(), depth, white, good);
        }

        for (PlayedMove capture : capturesSearched) {
            boolean good = bestMove.equals(capture);
            Piece piece = capture.getPiece();
            int to = capture.getMove().to();
            Piece captured = capture.getCaptured();
            captureHistoryTable.update(piece, to, captured, depth, white, good);
        }

    }

    public void updateCaptureHistory(PlayedMove bestMove, boolean white, int depth, List<PlayedMove> capturesSearched) {
        for (PlayedMove capture : capturesSearched) {
            boolean good = bestMove.equals(capture);
            Piece piece = capture.getPiece();
            int to = capture.getMove().to();
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

    public void reset() {
        bestMoveStability = 0;
        bestScoreStability = 0;
        historyTable.ageScores(true);
        historyTable.ageScores(false);
    }

    public void clear() {
        killerTable.clear();
        historyTable.clear();
        contHistTable.clear();
        counterMoveTable.clear();
        captureHistoryTable.clear();
    }

}
