package com.kelseyde.calvin.search.moveordering;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

public class MvvLva {

    public static final int[][] MVV_LVA_TABLE = new int[][] {
            new int[] {15, 14, 13, 12, 11, 10},  // victim P, attacker P, N, B, R, Q, K
            new int[] {25, 24, 23, 22, 21, 20},  // victim N, attacker P, N, B, R, Q, K
            new int[] {35, 34, 33, 32, 31, 30},  // victim B, attacker P, N, B, R, Q, K
            new int[] {45, 44, 43, 42, 41, 40},  // victim R, attacker P, N, B, R, Q, K
            new int[] {55, 54, 53, 52, 51, 50},  // victim Q, attacker P, N, B, R, Q, K
    };

    public static int score(Board board, Move move, Move ttMove) {
        if (move.equals(ttMove)) return MoveBonus.TT_MOVE_BIAS;
        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();
        Piece capturedPiece = board.pieceAt(endSquare);
        if (capturedPiece == null) return 0;
        Piece piece = board.pieceAt(startSquare);
        return MVV_LVA_TABLE[capturedPiece.getIndex()][piece.getIndex()];
    }

}
