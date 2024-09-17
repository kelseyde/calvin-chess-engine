package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;

public class HistoryTable extends AbstractHistoryTable {

    private static final int MAX_BONUS = 1200;
    private static final int MAX_SCORE = 8192;

    int[][][] table = new int[2][64][64];

    public void update(Move move, int depth, boolean white, boolean good) {
        int colourIndex = Colour.index(white);
        int from = move.getFrom();
        int to = move.getTo();
        int current = table[colourIndex][from][to];
        int bonus = bonus(depth);
        if (!good) bonus = -bonus;
        int update = gravity(current, bonus);
        table[colourIndex][from][to] = update;
    }

    public int get(Move historyMove, boolean white) {
        int colourIndex = Colour.index(white);
        int from = historyMove.getFrom();
        int to = historyMove.getTo();
        return table[colourIndex][from][to];
    }

    public void set(Move historyMove, boolean white, int update) {
        int colourIndex = Colour.index(white);
        int from = historyMove.getFrom();
        int to = historyMove.getTo();
        table[colourIndex][from][to] = update;
    }

    public void add(int depth, Move historyMove, boolean white) {
        int current = get(historyMove, white);
        int bonus = bonus(depth);
        int update = gravity(current, bonus);
        set(historyMove, white, update);
    }

    public void sub(int depth, Move historyMove, boolean white) {
        int current = get(historyMove, white);
        int bonus = bonus(depth);
        int update = gravity(current, -bonus);
        set(historyMove, white, update);
    }

    public void ageScores(boolean white) {
        int colourIndex = Colour.index(white);
        for (int from = 0; from < 64; from++) {
            for (int to = 0; to < 64; to++) {
                table[colourIndex][from][to] /= 2;
            }
        }
    }

    public void clear() {
        table = new int[2][64][64];
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
