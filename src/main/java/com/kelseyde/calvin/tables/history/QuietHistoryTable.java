package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

public class QuietHistoryTable extends AbstractHistoryTable {

    public static final int MAX_SCORE = 8192;
    private static final int MAX_BONUS = 1200;

    int[][][] table = new int[2][Piece.COUNT][Square.COUNT];

    public void update(Move move, Piece piece, int depth, boolean white, boolean good) {
        int colourIndex = Colour.index(white);
        int current = table[colourIndex][piece.index()][move.to()];
        int bonus = bonus(depth);
        if (!good) bonus = -bonus;
        int update = gravity(current, bonus);
        table[colourIndex][piece.index()][move.to()] = update;
    }

    public int get(Move move, Piece piece, boolean white) {
        int colourIndex = Colour.index(white);
        return table[colourIndex][piece.index()][move.to()];
    }

    public void ageScores(boolean white) {
        int colourIndex = Colour.index(white);
        for (int from = 0; from < Piece.COUNT; from++) {
            for (int to = 0; to < Square.COUNT; to++) {
                table[colourIndex][from][to] /= 2;
            }
        }
    }

    public void clear() {
        table = new int[2][Piece.COUNT][Square.COUNT];
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
