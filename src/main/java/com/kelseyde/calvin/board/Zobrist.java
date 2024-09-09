package com.kelseyde.calvin.board;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.Random;

/**
 * Utility class for generating Zobrist keys, which are 64-bit values that (almost uniquely) represent a chess position.
 * It is used to quickly identify positions that have already been examined by the move generator/evaluator, cutting
 * down on a lot of double-work.
 *
 * @see <a href="https://www.chessprogramming.org/Zobrist_Hashing">Chess Programming Wiki</a>
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Zobrist {

    static final long[][][] PIECE_SQUARE_HASH = new long[64][2][6];
    static final long[] CASTLING_RIGHTS = new long[16];
    static final long[] EN_PASSANT_FILE = new long[9];
    static final long BLACK_TO_MOVE;
    static final int WHITE = 0;
    static final int BLACK = 1;

    static {

        Random random = new Random();
        for (int square = 0; square < 64; square++) {
            for (int pieceIndex : Arrays.stream(Piece.values()).map(Piece::getIndex).toList()) {
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
                key ^= PIECE_SQUARE_HASH[square][WHITE][Piece.PAWN.getIndex()];
            } else if (((board.getPawns(false) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][Piece.PAWN.getIndex()];
            } else if (((board.getKnights(true) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][Piece.KNIGHT.getIndex()];
            } else if (((board.getKnights(false) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][Piece.KNIGHT.getIndex()];
            } else if (((board.getBishops(true) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][Piece.BISHOP.getIndex()];
            } else if (((board.getBishops(false) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][Piece.BISHOP.getIndex()];
            } else if (((board.getRooks(true) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][Piece.ROOK.getIndex()];
            } else if (((board.getRooks(false) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][Piece.ROOK.getIndex()];
            } else if (((board.getQueens(true) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][Piece.QUEEN.getIndex()];
            } else if (((board.getQueens(false) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][Piece.QUEEN.getIndex()];
            } else if (((board.getKing(true) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][Piece.KING.getIndex()];
            } else if (((board.getKing(false) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][Piece.KING.getIndex()];
            }
        }

        int enPassantFile = board.getGameState().getEnPassantFile() + 1;
        key ^= EN_PASSANT_FILE[enPassantFile];

        if (board.isWhiteToMove()) {
            key ^= BLACK_TO_MOVE;
        }

        key ^= CASTLING_RIGHTS[board.getGameState().getCastlingRights()];

        return key;
    }

    public static long generatePawnKey(Board board) {
        long key = 0L;
        for (int square = 0; square < 64; square++) {
            if (((board.getPawns(true) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][Piece.PAWN.getIndex()];
            } else if (((board.getPawns(false) >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][Piece.PAWN.getIndex()];
            }
        }
        return key;
    }

    public static long updatePiece(long key, int startSquare, int endSquare, Piece pieceType, boolean white) {
        return key ^ PIECE_SQUARE_HASH[startSquare][white ? 0 : 1][pieceType.getIndex()]
                ^ PIECE_SQUARE_HASH[endSquare][white ? 0 : 1][pieceType.getIndex()];
    }

    public static long updatePiece(long key, int square, Piece pieceType, boolean white) {
        return key ^ PIECE_SQUARE_HASH[square][white ? 0 : 1][pieceType.getIndex()];
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
