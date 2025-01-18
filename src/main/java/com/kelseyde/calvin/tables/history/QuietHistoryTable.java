package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;

public class QuietHistoryTable extends AbstractHistoryTable {

    int[][][][][] table = new int[2][Square.COUNT][Square.COUNT][2][2];

    public QuietHistoryTable(EngineConfig config) {
        super(config.quietHistBonusMax.value,
                config.quietHistBonusScale.value,
                config.quietHistMalusMax.value,
                config.quietHistMalusScale.value,
                config.quietHistMaxScore.value);
    }

    public void update(Move move, int depth, boolean white, long threats, boolean good) {
        int colourIndex = Colour.index(white);
        int fromThreatIndex = Bits.contains(threats, move.from()) ? 1 : 0;
        int toThreatIndex = Bits.contains(threats, move.to()) ? 1 : 0;
        int current = table[colourIndex][move.from()][move.to()][fromThreatIndex][toThreatIndex];
        int bonus = good ? bonus(depth) : malus(depth);
        int update = gravity(current, bonus);
        table[colourIndex][move.from()][move.to()][fromThreatIndex][toThreatIndex] = update;
    }

    public int get(Move move, Piece piece, boolean white, long threats) {
        int colourIndex = Colour.index(white);
        int fromThreatIndex = Bits.contains(threats, move.from()) ? 1 : 0;
        int toThreatIndex = Bits.contains(threats, move.to()) ? 1 : 0;
        return table[colourIndex][move.from()][move.to()][fromThreatIndex][toThreatIndex];
    }

    public void clear() {
        table = new int[2][Square.COUNT][Square.COUNT][2][2];
    }

}
