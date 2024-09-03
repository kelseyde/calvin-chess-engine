package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

public class ContHistTable extends AbstractHistoryTable {

    private static final int MAX_BONUS = 1200;
    private static final int MAX_SCORE = 8192;
    private static final int COLOUR_STRIDE = 6;

    // [prevPiece][prevTo][currPiece][currTo]
    int[][][][] table = new int[12][64][12][64];

    public int get(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, boolean white) {
        if (prevMove == null || prevPiece == null || currMove == null || currPiece == null) {
            return 0;
        }
        int currPieceIndex = pieceIndex(currPiece, white);
        int prevPieceIndex = pieceIndex(prevPiece, !white);
        return table[prevPieceIndex][prevMove.getTo()][currPieceIndex][currMove.getTo()];
    }

    public void set(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, int update, boolean white) {
        if (prevMove == null || prevPiece == null || currMove == null || currPiece == null) {
            return;
        }
        int currPieceIndex = pieceIndex(currPiece, white);
        int prevPieceIndex = pieceIndex(prevPiece, !white);
        table[prevPieceIndex][prevMove.getTo()][currPieceIndex][currMove.getTo()] = update;
    }

    public void add(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, int depth, boolean white) {
        if (prevMove == null || prevPiece == null || currMove == null || currPiece == null) {
            return;
        }
        int current = get(prevMove, prevPiece, currMove, currPiece, white);
        int bonus = bonus(depth);
        int update = gravity(current, bonus);
        set(prevMove, prevPiece, currMove, currPiece, update, white);
    }

    public void sub(Move prevMove, Piece prevPiece, Move currMove, Piece currPiece, int depth, boolean white) {
        if (prevMove == null || prevPiece == null || currMove == null || currPiece == null) {
            return;
        }
        int current = get(prevMove, prevPiece, currMove, currPiece, white);
        int bonus = bonus(depth);
        int update = gravity(current, -bonus);
        set(prevMove, prevPiece, currMove, currPiece, update, white);
    }

    public void clear() {
        table = new int[12][64][12][64];
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
