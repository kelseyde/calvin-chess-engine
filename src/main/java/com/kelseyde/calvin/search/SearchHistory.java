package com.kelseyde.calvin.search;

import com.kelseyde.calvin.tables.history.CaptureHistoryTable;
import com.kelseyde.calvin.tables.history.HistoryTable;
import com.kelseyde.calvin.tables.history.KillerTable;
import lombok.Data;

@Data
public class SearchHistory {

    private KillerTable killerTable = new KillerTable();
    private HistoryTable historyTable = new HistoryTable();
    private CaptureHistoryTable captureHistoryTable = new CaptureHistoryTable();

    public void clear() {
        killerTable.clear();
        historyTable.clear();
        captureHistoryTable.clear();
    }

}
