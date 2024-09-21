package com.kelseyde.calvin.board;

import java.util.Arrays;
import java.util.Random;

/**
 * Utility class for generating Zobrist keys, which are 64-bit values that (almost uniquely) represent a chess position.
 * It is used to quickly identify positions that have already been examined by the move generator/evaluator, cutting
 * down on a lot of double-work.
 *
 * @see <a href="https://www.chessprogramming.org/Zobrist_Hashing">Chess Programming Wiki</a>
 */
public class Zobrist {

    private static final long[][][] PIECE_SQUARE_HASH = new long[64][2][6];
    private static final long[] CASTLING_RIGHTS = new long[16];
    private static final long[] EN_PASSANT_FILE = new long[9];
    private static final long BLACK_TO_MOVE;
    private static final int WHITE = 0;
    private static final int BLACK = 1;

    static {

        Random random = new Random();
        for (int square = 0; square < 64; square++) {
            for (int pieceIndex : Arrays.stream(Piece.values()).map(Piece::index).toList()) {
                PIECE_SQUARE_HASH[square][WHITE][pieceIndex] = random.nextLong();
                PIECE_SQUARE_HASH[square][BLACK][pieceIndex] = random.nextLong();
            }
        }
        for (int i = 0; i < CASTLING_RIGHTS.length; i++) {
            CASTLING_RIGHTS[i] = random.nextLong();
        }
        for (int i = 0; i < EN_PASSANT_FILE.length; i++) {
            EN_PASSANT_FILE[i] = random.nextLong();
        }
        BLACK_TO_MOVE = random.nextLong();

    }

    public static long generateKey(Board board) {

        long key = 0L;

        for (int square = 0; square < 64; square++) {
            if (((board.getPawns(true) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][Piece.PAWN.index()];
            }
            else if (((board.getPawns(false) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][Piece.PAWN.index()];
            }
            else if (((board.getKnights(true) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][Piece.KNIGHT.index()];
            }
            else if (((board.getKnights(false) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][Piece.KNIGHT.index()];
            }
            else if (((board.getBishops(true) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][Piece.BISHOP.index()];
            }
            else if (((board.getBishops(false) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][Piece.BISHOP.index()];
            }
            else if (((board.getRooks(true) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][Piece.ROOK.index()];
            }
            else if (((board.getRooks(false) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][Piece.ROOK.index()];
            }
            else if (((board.getQueens(true) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][Piece.QUEEN.index()];
            }
            else if (((board.getQueens(false) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][Piece.QUEEN.index()];
            }
            else if (((board.getKing(true) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][Piece.KING.index()];
            }
            else if (((board.getKing(false) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][Piece.KING.index()];
            }
        }

        int enPassantFile = board.getState().getEnPassantFile() + 1;
        key ^= EN_PASSANT_FILE[enPassantFile];

        if (board.isWhite()) {
            key ^= BLACK_TO_MOVE;
        }

        key ^= CASTLING_RIGHTS[board.getState().getRights()];

        return key;
    }

    public static long generatePawnKey(Board board) {
        long key = 0L;
        for (int square = 0; square < 64; square++) {
            if (((board.getPawns(true) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][Piece.PAWN.index()];
            }
            else if (((board.getPawns(false) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][Piece.PAWN.index()];
            }
        }
        return key;
    }

    public static long updatePiece(long key, int from, int to, Piece pieceType, boolean white) {
        return key ^ PIECE_SQUARE_HASH[from][Colour.index(white)][pieceType.index()]
                   ^ PIECE_SQUARE_HASH[to][Colour.index(white)][pieceType.index()];
    }

    public static long updatePiece(long key, int square, Piece pieceType, boolean white) {
        return key ^ PIECE_SQUARE_HASH[square][Colour.index(white)][pieceType.index()];
    }

    public static long updateCastlingRights(long key, int oldCastlingRights, int newCastlingRights) {
        key ^= CASTLING_RIGHTS[oldCastlingRights];
        key ^= CASTLING_RIGHTS[newCastlingRights];
        return key;
    }

    public static long updateEnPassantFile(long key, int oldEnPassantFile, int newEnPassantFile) {
        key ^= EN_PASSANT_FILE[oldEnPassantFile + 1];
        key ^= EN_PASSANT_FILE[newEnPassantFile + 1];
        return key;
    }

    public static long updateSideToMove(long key) {
        return key ^ BLACK_TO_MOVE;
    }

    public static long updateKeyAfterNullMove(long key, int oldEnPassantFile) {
        key ^= EN_PASSANT_FILE[oldEnPassantFile + 1];
        key ^= EN_PASSANT_FILE[0];
        key ^= BLACK_TO_MOVE;
        return key;
    }

}
