package com.kelseyde.calvin.search.moveordering.tables;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.search.moveordering.MoveBonus;

public class HistoryTable {

    static final double AGE_FACTOR = 2.0;

    private final int[][][] table = new int[2][64][64];

    public void increment(int depth, Move historyMove, boolean white) {
        int colourIndex = Board.colourIndex(white);
        int startSquare = historyMove.getStartSquare();
        int endSquare = historyMove.getEndSquare();
        int score = depth * depth;
        table[colourIndex][startSquare][endSquare] += score;
    }

    public void decrement(int depth, Move historyMove, boolean white) {
        int colourIndex = Board.colourIndex(white);
        int startSquare = historyMove.getStartSquare();
        int endSquare = historyMove.getEndSquare();
        int score = depth * depth;
        table[colourIndex][startSquare][endSquare] -= score;
    }

    public void ageHistoryScores(boolean white) {
        int colourIndex = Board.colourIndex(white);
        for (int startSquare = 0; startSquare < 64; startSquare++) {
            for (int endSquare = 0; endSquare < 64; endSquare++) {
                table[colourIndex][startSquare][endSquare] /= AGE_FACTOR;
            }
        }
    }

    public int getScore(Board board, int from, int to, int killerScore) {
        int colourIndex = Board.colourIndex(board.isWhiteToMove());
        int historyScore = table[colourIndex][from][to];
        if (killerScore == 0 && historyScore > 0) {
            historyScore += MoveBonus.HISTORY_MOVE_BIAS;
        }
        return historyScore;
    }

}
