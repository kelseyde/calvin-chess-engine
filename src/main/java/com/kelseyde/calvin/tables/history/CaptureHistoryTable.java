package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

public class CaptureHistoryTable extends AbstractHistoryTable {

    public static record CaptureMove(Move move, Piece piece , Piece capturedPiece) {}

    private static final int MAX_BONUS = 1200;
    private static final int MAX_SCORE = 8192;

    int[][][][] table = new int[2][6][64][6];

    public int get(Piece piece, int to, Piece captured, boolean white) {
        int colourIndex = Board.colourIndex(white);
        int pieceIndex = piece.getIndex();
        int capturedIndex = captured.getIndex();
        return table[colourIndex][pieceIndex][to][capturedIndex];
    }

    public void set(Piece piece, int to, Piece captured, boolean white, int update) {
        int colourIndex = Board.colourIndex(white);
        int pieceIndex = piece.getIndex();
        int capturedIndex = captured.getIndex();
        table[colourIndex][pieceIndex][to][capturedIndex] = update;
    }

    public void add(Piece piece, int to, Piece captured, boolean white, int depth) {
        int current = get(piece, to, captured, white);
        int bonus = bonus(depth);
        int update = gravity(current, bonus);
        set(piece, to, captured, white, update);
    }

    public void sub(Piece piece, int to, Piece captured, boolean white, int depth) {
        int current = get(piece, to, captured, white);
        int bonus = bonus(depth);
        int update = gravity(current, -bonus);
        set(piece, to, captured, white, update);
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
