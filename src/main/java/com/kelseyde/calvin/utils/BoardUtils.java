package com.kelseyde.calvin.utils;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.GameState;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.bitboard.Bits;

import java.util.ArrayDeque;
import java.util.Set;
import java.util.stream.Collectors;

public class BoardUtils {

    // All the possible move 'vectors' for a sliding piece, i.e., the offsets for the directions in which a sliding
    // piece is permitted to move. Bishops will use only the diagonal vectors, rooks only the orthogonal vectors, while
    // queens will use both.
    public static final Set<Integer> DIAGONAL_MOVE_VECTORS = Set.of(-9, -7, 7, 9);
    public static final Set<Integer> ORTHOGONAL_MOVE_VECTORS = Set.of(-8, -1, 1, 8);

    // The following sets are exceptions to the initial rule, in scenarios where the sliding piece is placed on the a or h-files.
    // These exceptions prevent the piece from 'wrapping' around to the other side of the board.
    public static final Set<Integer> A_FILE_OFFSET_EXCEPTIONS = Set.of(-9, -1, 7);
    public static final Set<Integer> H_FILE_OFFSET_EXCEPTIONS = Set.of(-7, 1, 9);

    public static int getFile(int sq) {
        return sq & 0b000111;
    }

    public static int getRank(int sq) {
        return sq >>> 3;
    }

    public static int getDiagonal(int sq) {
        long bb = 1L << sq;
        if ((bb & Bits.DIAGONAL_1) != 0) return 0;
        if ((bb & Bits.DIAGONAL_2) != 0) return 1;
        if ((bb & Bits.DIAGONAL_3) != 0) return 2;
        if ((bb & Bits.DIAGONAL_4) != 0) return 3;
        if ((bb & Bits.DIAGONAL_5) != 0) return 4;
        if ((bb & Bits.DIAGONAL_6) != 0) return 5;
        if ((bb & Bits.DIAGONAL_7) != 0) return 6;
        if ((bb & Bits.DIAGONAL_8) != 0) return 7;
        if ((bb & Bits.DIAGONAL_9) != 0) return 8;
        if ((bb & Bits.DIAGONAL_10) != 0) return 9;
        if ((bb & Bits.DIAGONAL_11) != 0) return 10;
        if ((bb & Bits.DIAGONAL_12) != 0) return 11;
        if ((bb & Bits.DIAGONAL_13) != 0) return 12;
        if ((bb & Bits.DIAGONAL_14) != 0) return 13;
        if ((bb & Bits.DIAGONAL_15) != 0) return 14;
        throw new IllegalArgumentException("Illegal square coordinate " + sq);
    }

    public static int getAntiDiagonal(int sq) {
        long bb = 1L << sq;
        if ((bb & Bits.ANTI_DIAGONAL_MASK_1) != 0) return 0;
        if ((bb & Bits.ANTI_DIAGONAL_MASK_2) != 0) return 1;
        if ((bb & Bits.ANTI_DIAGONAL_MASK_3) != 0) return 2;
        if ((bb & Bits.ANTI_DIAGONAL_MASK_4) != 0) return 3;
        if ((bb & Bits.ANTI_DIAGONAL_MASK_5) != 0) return 4;
        if ((bb & Bits.ANTI_DIAGONAL_MASK_6) != 0) return 5;
        if ((bb & Bits.ANTI_DIAGONAL_MASK_7) != 0) return 6;
        if ((bb & Bits.ANTI_DIAGONAL_MASK_8) != 0) return 7;
        if ((bb & Bits.ANTI_DIAGONAL_MASK_9) != 0) return 8;
        if ((bb & Bits.ANTI_DIAGONAL_MASK_10) != 0) return 9;
        if ((bb & Bits.ANTI_DIAGONAL_MASK_11) != 0) return 10;
        if ((bb & Bits.ANTI_DIAGONAL_MASK_12) != 0) return 11;
        if ((bb & Bits.ANTI_DIAGONAL_MASK_13) != 0) return 12;
        if ((bb & Bits.ANTI_DIAGONAL_MASK_14) != 0) return 13;
        if ((bb & Bits.ANTI_DIAGONAL_MASK_15) != 0) return 14;
        throw new IllegalArgumentException("Illegal square coordinate " + sq);
    }

    public static int squareIndex(int rank, int file) {
        return 8 * rank + file;
    }

    public static boolean isValidIndex(int squareIndex) {
        return squareIndex >= 0 && squareIndex < 64;
    }

    public static int getColourIndex(boolean isWhite) {
        return isWhite ? 1 : 0;
    }

    public static boolean isAligned(int sq1, int sq2, int sq3) {
        int rank1 = getRank(sq1);
        int rank2 = getRank(sq2);
        int rank3 = getRank(sq3);
        if (rank1 == rank2 && rank2 == rank3) {
            return true;
        }
        int file1 = getFile(sq1);
        int file2 = getFile(sq2);
        int file3 = getFile(sq3);
        if (file1 == file2 && file2 == file3) {
            return true;
        }
        int diagonal1 = getDiagonal(sq1);
        int diagonal2 = getDiagonal(sq2);
        int diagonal3 = getDiagonal(sq3);
        if (diagonal1 == diagonal2 && diagonal2 == diagonal3) {
            return true;
        }
        int antiDiagonal1 = getAntiDiagonal(sq1);
        int antiDiagonal2 = getAntiDiagonal(sq2);
        int antiDiagonal3 = getAntiDiagonal(sq3);
        return antiDiagonal1 == antiDiagonal2 && antiDiagonal2 == antiDiagonal3;
    }

    public static Board copy(Board board) {
        Board newBoard = new Board();
        newBoard.setWhitePawns(board.getWhitePawns());
        newBoard.setWhiteKnights(board.getWhiteKnights());
        newBoard.setWhiteBishops(board.getWhiteBishops());
        newBoard.setWhiteRooks(board.getWhiteRooks());
        newBoard.setWhiteQueens(board.getWhiteQueens());
        newBoard.setWhiteKing(board.getWhiteKing());
        newBoard.setBlackPawns(board.getBlackPawns());
        newBoard.setBlackKnights(board.getBlackKnights());
        newBoard.setBlackBishops(board.getBlackBishops());
        newBoard.setBlackRooks(board.getBlackRooks());
        newBoard.setBlackQueens(board.getBlackQueens());
        newBoard.setBlackKing(board.getBlackKing());
        newBoard.setWhitePieces(board.getWhitePieces());
        newBoard.setBlackPieces(board.getBlackPieces());
        newBoard.setOccupied(board.getOccupied());
        newBoard.setWhiteToMove(board.isWhiteToMove());
        newBoard.setGameState(board.getGameState().copy());
        newBoard.setGameStateHistory(board.getGameStateHistory().stream().map(GameState::copy).collect(Collectors.toCollection(ArrayDeque::new)));
        newBoard.setMoveHistory(board.getMoveHistory().stream().map(move -> new Move(move.getValue())).collect(Collectors.toCollection(ArrayDeque::new)));
        newBoard.recalculatePieces();
        return newBoard;
    }

}
