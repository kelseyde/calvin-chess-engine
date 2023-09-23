package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitBoardConstants;

import java.util.Optional;

public class BoardUtils {

    public static int getFile(int square) {
        long squareMask = 1L << square;
        if ((squareMask & BitBoardConstants.FILE_A) != 0) {
            return 0;
        }
        if ((squareMask & BitBoardConstants.FILE_B) != 0) {
            return 1;
        }
        if ((squareMask & BitBoardConstants.FILE_C) != 0) {
            return 2;
        }
        if ((squareMask & BitBoardConstants.FILE_D) != 0) {
            return 3;
        }
        if ((squareMask & BitBoardConstants.FILE_E) != 0) {
            return 4;
        }
        if ((squareMask & BitBoardConstants.FILE_F) != 0) {
            return 5;
        }
        if ((squareMask & BitBoardConstants.FILE_G) != 0) {
            return 6;
        }
        if ((squareMask & BitBoardConstants.FILE_H) != 0) {
            return 7;
        }
        throw new IllegalArgumentException("Illegal square coordinate " + square);
    }

    // TODO make less horrible
    public static Optional<String> pieceCodeAt(Board board, int square) {
        long squareBB = 1L << square;
        if ((squareBB & board.getWhitePawns()) != 0) {
            return Optional.of("wP");
        }
        if ((squareBB & board.getWhiteKnights()) != 0) {
            return Optional.of("wN");
        }
        if ((squareBB & board.getWhiteBishops()) != 0) {
            return Optional.of("wB");
        }
        if ((squareBB & board.getWhiteRooks()) != 0) {
            return Optional.of("wR");
        }
        if ((squareBB & board.getWhiteQueens()) != 0) {
            return Optional.of("wQ");
        }
        if ((squareBB & board.getWhiteKing()) != 0) {
            return Optional.of("wK");
        }
        if ((squareBB & board.getBlackPawns()) != 0) {
            return Optional.of("bP");
        }
        if ((squareBB & board.getBlackKnights()) != 0) {
            return Optional.of("bN");
        }
        if ((squareBB & board.getBlackBishops()) != 0) {
            return Optional.of("bB");
        }
        if ((squareBB & board.getBlackRooks()) != 0) {
            return Optional.of("bR");
        }
        if ((squareBB & board.getBlackQueens()) != 0) {
            return Optional.of("bQ");
        }
        if ((squareBB & board.getBlackKing()) != 0) {
            return Optional.of("bK");
        }
        return Optional.empty();
    }

    public static int squareIndex(int rank, int file) {
        return 8 * rank + file;
    }

}
