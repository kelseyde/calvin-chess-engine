package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Move;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuietHistoryTableTest {

    private final QuietHistoryTable historyTable = new QuietHistoryTable();

    @Test
    public void testThreatHistory() {

        Move move = Move.fromUCI("e2e4");
        int from = move.from();
        int to = move.to();

        long fromBitboard = Bits.fromSquare(from);
        long toBitboard = Bits.fromSquare(to);

        historyTable.update(move, 10, fromBitboard, true, true);

        assertEquals(1200, historyTable.get(move, fromBitboard, true));
        assertEquals(0, historyTable.get(move, toBitboard, true));

        historyTable.update(move, 1, toBitboard, true, true);

        assertEquals(1200, historyTable.get(move, fromBitboard, true));
        assertEquals(64, historyTable.get(move, toBitboard, true));


    }

}