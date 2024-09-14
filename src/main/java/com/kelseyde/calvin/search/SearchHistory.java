package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.tables.history.CaptureHistoryTable;
import com.kelseyde.calvin.tables.history.HistoryTable;
import com.kelseyde.calvin.tables.history.KillerTable;
import lombok.Data;

@Data
public class SearchHistory {

    private KillerTable killerTable = new KillerTable();
    private HistoryTable historyTable = new HistoryTable();
    private CaptureHistoryTable captureHistoryTable = new CaptureHistoryTable();

    private int bestMoveStability = 0;
    private int bestScoreStability = 0;

    public void updateBestMoveStability(Move bestMovePrevious, Move bestMoveCurrent) {
        if (bestMovePrevious == null || bestMoveCurrent == null) {
            return;
        }
        bestMoveStability = bestMovePrevious.equals(bestMoveCurrent) ? bestMoveStability + 1 : 0;
    }

    public void updateBestScoreStability(int scorePrevious, int scoreCurrent) {
        bestScoreStability = scoreCurrent >= scorePrevious - 10 && scoreCurrent <= scorePrevious + 10 ? bestScoreStability + 1 : 0;
    }

    public void resetStability() {
        bestMoveStability = 0;
        bestScoreStability = 0;
    }

    public void clear() {
        killerTable.clear();
        historyTable.clear();
        captureHistoryTable.clear();
    }

}
