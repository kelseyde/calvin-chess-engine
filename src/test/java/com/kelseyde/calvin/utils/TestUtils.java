package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.board.Board;

public class TestUtils {

    public static Board emptyBoard() {
        Board board = new Board();
        board.setWhitePawns(0L);
        board.setWhiteKnights(0L);
        board.setWhiteBishops(0L);
        board.setWhiteRooks(0L);
        board.setWhiteQueens(0L);
        board.setWhiteKing(0L);

        board.setBlackPawns(0L);
        board.setBlackKnights(0L);
        board.setBlackBishops(0L);
        board.setBlackRooks(0L);
        board.setBlackQueens(0L);
        board.setBlackKing(0L);

        board.setEnPassantTarget(0L);

        board.setWhiteKingsideCastlingAllowed(false);
        board.setWhiteQueensideCastlingAllowed(false);
        board.setBlackKingsideCastlingAllowed(false);
        board.setBlackQueensideCastlingAllowed(false);

        board.recalculatePieces();

        return board;
    }

}
