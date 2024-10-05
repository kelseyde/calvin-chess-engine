package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;

public class QuietHistoryTable extends AbstractHistoryTable {

    public static final int MAX_SCORE = 8192;
    private static final int MAX_BONUS = 1200;

    int[][][] table = new int[2][Square.COUNT][Square.COUNT];

    public void update(Move move, int depth, boolean white, boolean good) {
        int colourIndex = Colour.index(white);
        int from = move.from();
        int to = move.to();
        int current = table[colourIndex][from][to];
        int bonus = bonus(depth);
        if (!good) bonus = -bonus;
        int update = gravity(current, bonus);
        table[colourIndex][from][to] = update;
    }

    public int get(Move historyMove, boolean white) {
        int colourIndex = Colour.index(white);
        int from = historyMove.from();
        int to = historyMove.to();
        return table[colourIndex][from][to];
    }

    public void set(Move historyMove, boolean white, int update) {
        int colourIndex = Colour.index(white);
        int from = historyMove.from();
        int to = historyMove.to();
        table[colourIndex][from][to] = update;
    }

    public void ageScores(boolean white) {
        int colourIndex = Colour.index(white);
        for (int from = 0; from < Square.COUNT; from++) {
            for (int to = 0; to < Square.COUNT; to++) {
                table[colourIndex][from][to] /= 2;
            }
        }
    }

    public void clear() {
        table = new int[2][Square.COUNT][Square.COUNT];
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
