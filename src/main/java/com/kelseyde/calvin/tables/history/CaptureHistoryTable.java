package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;

public class CaptureHistoryTable extends AbstractHistoryTable {

    private static final int MAX_BONUS = 1200;
    private static final int MAX_SCORE = 8192;

    int[][][][] table = new int[2][6][64][6];

    public void update(Piece piece, int to, Piece captured, boolean white, boolean good) {
        int colourIndex = Board.colourIndex(white);
        int pieceIndex = piece.getIndex();
        int capturedIndex = captured.getIndex();
        int current = table[colourIndex][pieceIndex][to][capturedIndex];
        int bonus = good ? MAX_BONUS : -MAX_BONUS;
        int update = gravity(current, bonus);
        table[colourIndex][pieceIndex][to][capturedIndex] = update;
    }

    public int get(Piece piece, int to, Piece captured, boolean white) {
        int colourIndex = Board.colourIndex(white);
        int pieceIndex = piece.getIndex();
        int capturedIndex = captured.getIndex();
        return table[colourIndex][pieceIndex][to][capturedIndex];
    }

    public void clear() {
        table = new int[2][6][64][6];
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
