package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;

public class QuietHistoryTable extends HistoryTable {

    private static final int MAX_BONUS = 1200;
    private static final int MAX_SCORE = 8192;

    int[][][][] table = new int[2][Square.COUNT][Square.COUNT][4];

    public void update(Move move, int depth, long threats, boolean white, boolean good) {
        int colourIndex = Colour.index(white);
        int from = move.from();
        int to = move.to();
        int threatIndex = threatIndex(from, to, threats);
        int current = table[colourIndex][from][to][threatIndex];
        int bonus = bonus(depth);
        if (!good) bonus = -bonus;
        int update = gravity(current, bonus);
        table[colourIndex][from][to][threatIndex] = update;
        for (int i = 0; i < 4; i++) {
            if (i != threatIndex) {
                current = table[colourIndex][from][to][i];
                update = gravity(current, bonus / 2);
                table[colourIndex][from][to][i] = update;
            }
        }
    }

    public int get(Move historyMove, long threats, boolean white) {
        int colourIndex = Colour.index(white);
        int from = historyMove.from();
        int to = historyMove.to();
        int threatIndex = threatIndex(from, to, threats);
        return table[colourIndex][from][to][threatIndex];
    }

    public void ageScores(boolean white) {
        int colourIndex = Colour.index(white);
        for (int from = 0; from < Square.COUNT; from++) {
            for (int to = 0; to < Square.COUNT; to++) {
                table[colourIndex][from][to][0] /= 2;
                table[colourIndex][from][to][1] /= 2;
            }
        }
    }

    private int threatIndex(int from, int to, long threats) {
        int fromThreatened = Bits.contains(threats, from) ? 1 : 0;
        int toThreatened = Bits.contains(threats, to) ? 1 : 0;
        return fromThreatened << 1 | toThreatened;
    }

    public void clear() {
        table = new int[2][Square.COUNT][Square.COUNT][2];
    }

    @Override
    protected int getMaxScore() {
        return MAX_SCORE;
    }

    @Override
    protected int getMaxBonus() {
        return MAX_BONUS;
    }

}
