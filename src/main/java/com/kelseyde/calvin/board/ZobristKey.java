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
public class ZobristKey {

    /**
     * Three-dimensional array of all possible combinations of squares + piece types + colours
     * (64 * 6 * 2) = 768 possible combinations.
     */
    public static final long[][][] PIECE_SQUARE_HASH = new long[64][2][6];

    /**
     * There are 16 possible combinations of castling rights for both players.
     */
    public static final long[] CASTLING_RIGHTS = new long[16];

    /**
     * On what file en passant is possible (0 = no en passant possible).
     */
    public static final long[] EN_PASSANT_FILE = new long[9];

    public static final long BLACK_TO_MOVE;

    private static final int WHITE = 0;
    private static final int BLACK = 1;

    static {

        Random random = new Random();

        for (int square = 0; square < 64; square++) {
            for (int pieceIndex : Arrays.stream(PieceType.values()).map(PieceType::getIndex).toList()) {
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
            if (((board.getWhitePawns() >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][PieceType.PAWN.getIndex()];
            }
            else if (((board.getBlackPawns() >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][PieceType.PAWN.getIndex()];
            }
            else if (((board.getWhiteKnights() >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][PieceType.KNIGHT.getIndex()];
            }
            else if (((board.getBlackKnights() >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][PieceType.KNIGHT.getIndex()];
            }
            else if (((board.getWhiteBishops() >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][PieceType.BISHOP.getIndex()];
            }
            else if (((board.getBlackBishops() >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][PieceType.BISHOP.getIndex()];
            }
            else if (((board.getWhiteRooks() >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][PieceType.ROOK.getIndex()];
            }
            else if (((board.getBlackRooks() >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][PieceType.ROOK.getIndex()];
            }
            else if (((board.getWhiteQueens() >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][PieceType.QUEEN.getIndex()];
            }
            else if (((board.getBlackQueens() >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][PieceType.QUEEN.getIndex()];
            }
            else if (((board.getWhiteKing() >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][WHITE][PieceType.KING.getIndex()];
            }
            else if (((board.getBlackKing() >>> square) & 1) == 1) {
                key ^= PIECE_SQUARE_HASH[square][BLACK][PieceType.KING.getIndex()];
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

    public static long updateKey(long key, boolean isWhite, int startSquare, int endSquare, PieceType oldType, PieceType newType,
                          PieceType capturedType, int oldCastlingRights, int newCastlingRights, int oldEnPassantFile, int newEnPassantFile) {

        key ^= PIECE_SQUARE_HASH[startSquare][isWhite ? 0 : 1][oldType.getIndex()];
        if (capturedType != null) {
            key ^= PIECE_SQUARE_HASH[endSquare][isWhite ? 1 : 0][capturedType.getIndex()];
        }
        key ^= PIECE_SQUARE_HASH[endSquare][isWhite ? 0 : 1][newType.getIndex()];
        key ^= CASTLING_RIGHTS[oldCastlingRights];
        key ^= CASTLING_RIGHTS[newCastlingRights];
        key ^= EN_PASSANT_FILE[oldEnPassantFile + 1];
        key ^= EN_PASSANT_FILE[newEnPassantFile + 1];
        key ^= BLACK_TO_MOVE;
        return key;

    }

}
