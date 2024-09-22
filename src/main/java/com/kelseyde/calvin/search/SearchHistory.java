package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.search.SearchStack.PlayedMove;
import com.kelseyde.calvin.tables.history.*;

import java.util.List;

public class SearchHistory {

    private static final int[] CONT_HIST_PLIES = { 1, 2 };

    private final KillerTable killerTable = new KillerTable();
    private final HistoryTable historyTable = new HistoryTable();
    private final ContinuationHistoryTable contHistTable = new ContinuationHistoryTable();
    private final CounterMoveTable counterMoveTable = new CounterMoveTable();
    private final CaptureHistoryTable captureHistoryTable = new CaptureHistoryTable();

    private int bestMoveStability = 0;
    private int bestScoreStability = 0;

    public void updateHistory(
            PlayedMove bestMove, boolean white, int depth, int ply, SearchStack ss, List<PlayedMove> quiets, List<PlayedMove> captures) {

        if (bestMove.isQuiet()) {

            Move prevMove = ss.getMove(ply - 1);
            Piece prevPiece = ss.getMovedPiece(ply - 1);

            killerTable.add(ply, bestMove.move());
            counterMoveTable.add(prevPiece, prevMove, white, bestMove.move());
            for (PlayedMove quiet : quiets) {
                boolean good = bestMove.move().equals(quiet.move());
                historyTable.update(quiet.move(), depth, white, good);

                for (int prevPly : CONT_HIST_PLIES) {
                    prevMove = ss.getMove(ply - prevPly);
                    prevPiece = ss.getMovedPiece(ply - prevPly);
                    contHistTable.update(prevMove, prevPiece, quiet.move(), quiet.piece(), depth, white, good);
                }
            }

        }

        for (PlayedMove capture : captures) {
            boolean good = bestMove.equals(capture);
            Piece piece = capture.piece();
            int to = capture.move().to();
            Piece captured = capture.captured();
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

    public int getBestMoveStability() {
        return bestMoveStability;
    }

    public int getBestScoreStability() {
        return bestScoreStability;
    }

    public KillerTable getKillerTable() {
        return killerTable;
    }

    public HistoryTable getHistoryTable() {
        return historyTable;
    }

    public ContinuationHistoryTable getContHistTable() {
        return contHistTable;
    }

    public CaptureHistoryTable getCaptureHistoryTable() {
        return captureHistoryTable;
    }

    public CounterMoveTable getCounterMoveTable() {
        return counterMoveTable;
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
