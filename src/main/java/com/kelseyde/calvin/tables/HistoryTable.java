package com.kelseyde.calvin.tables;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;

public class HistoryTable {

    int[][][] table = new int[2][64][64];

    public int get(int startSquare, int endSquare, boolean white) {
        int colourIndex = Board.colourIndex(white);
        return table[colourIndex][startSquare][endSquare];
    }

    public void add(int depth, Move historyMove, boolean white) {
        update(historyMove, white, depth * depth);
    }

    public void sub(int depth, Move historyMove, boolean white) {
        update(historyMove, white, -depth * depth);
    }

    public int score(Move move, boolean white, int base, boolean applyBase) {
        int colourIndex = Board.colourIndex(white);
        int score = table[colourIndex][move.getFrom()][move.getTo()];
        base = applyBase ? base : 0;
        return score > 0 ? base + score : 0;
    }

    public void ageScores(boolean white) {
        int colourIndex = Board.colourIndex(white);
        for (int startSquare = 0; startSquare < 64; startSquare++) {
            for (int endSquare = 0; endSquare < 64; endSquare++) {
                table[colourIndex][startSquare][endSquare] /= 2;
            }
        }
    }

    private void update(Move historyMove, boolean white, int update) {
        int colourIndex = Board.colourIndex(white);
        int startSquare = historyMove.getFrom();
        int endSquare = historyMove.getTo();
        table[colourIndex][startSquare][endSquare] += update;
    }

    public void clear() {
        table = new int[2][64][64];
    }

}
