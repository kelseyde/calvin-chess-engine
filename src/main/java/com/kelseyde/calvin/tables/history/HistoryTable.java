package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;

public class HistoryTable extends AbstractHistoryTable {

    private static final int MAX_BONUS = 1200;
    private static final int MAX_SCORE = 8192;

    int[][][] table = new int[2][64][64];

    public void update(Move move, int depth, boolean white, boolean good) {
        int colourIndex = Colour.index(white);
        int startSquare = move.getFrom();
        int endSquare = move.getTo();
        int current = table[colourIndex][startSquare][endSquare];
        int bonus = bonus(depth);
        if (!good) bonus = -bonus;
        int update = gravity(current, bonus);
        table[colourIndex][startSquare][endSquare] = update;
    }

    public int get(Move historyMove, boolean white) {
        int colourIndex = Colour.index(white);
        int startSquare = historyMove.getFrom();
        int endSquare = historyMove.getTo();
        return table[colourIndex][startSquare][endSquare];
    }

    public void set(Move historyMove, boolean white, int update) {
        int colourIndex = Colour.index(white);
        int startSquare = historyMove.getFrom();
        int endSquare = historyMove.getTo();
        table[colourIndex][startSquare][endSquare] = update;
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
        for (int startSquare = 0; startSquare < 64; startSquare++) {
            for (int endSquare = 0; endSquare < 64; endSquare++) {
                table[colourIndex][startSquare][endSquare] /= 2;
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
