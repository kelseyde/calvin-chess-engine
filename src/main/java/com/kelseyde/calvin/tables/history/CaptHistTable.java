package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Piece;

public class CaptHistTable extends AbstractHistoryTable {

    private static final int MAX_BONUS = 1200;
    private static final int MAX_SCORE = 8192;

    private static final int COLOUR_STRIDE = 6;

    private int[][][] table = new int[12][64][12];

    public int get(Piece piece, Piece capturedPiece, int square, boolean white) {
        return table[pieceIndex(piece, white)][square][pieceIndex(capturedPiece, !white)];
    }

    public void set(Piece piece, Piece capturedPiece, int square, int update, boolean white) {
        table[pieceIndex(piece, white)][square][pieceIndex(capturedPiece, !white)] = update;
    }

    public void add(Piece piece, Piece capturedPiece, int square, int depth, boolean white) {
        int current = get(piece, capturedPiece, square, white);
        int bonus = bonus(depth);
        int update = gravity(current, bonus);
        set(piece, capturedPiece, square, update, white);
    }

    public void sub(Piece piece, Piece capturedPiece, int square, int depth, boolean white) {
        int current = get(piece, capturedPiece, square, white);
        int bonus = bonus(depth);
        int update = gravity(current, -bonus);
        set(piece, capturedPiece, square, update, white);
    }

    public void ageScores() {
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 64; j++) {
                for (int k = 0; k < 12; k++) {
                    table[i][j][k] /= 2;
                }
            }
        }
    }

    public void clear() {
        table = new int[12][64][12];
    }

    private int pieceIndex(Piece piece, boolean white) {
        return piece.getIndex() + (white ? 0 : COLOUR_STRIDE);
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
