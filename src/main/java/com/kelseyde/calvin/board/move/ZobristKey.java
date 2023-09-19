package com.kelseyde.calvin.board.move;

import com.kelseyde.calvin.board.piece.PieceType;

import java.util.Random;

/**
 * Utility class for generating Zobrist keys, which are 64-bit values that (almost uniquely) represent a chess position.
 * It is used to quickly identify positions that have already been examined by the move generator/evaluator, cutting
 * down on a lot of double-work.
 * @see <a href="https://www.chessprogramming.org/Zobrist_Hashing">Chess Programming Wiki</a>
 */
public class ZobristKey {

    // TODO finish, replace BoardMetadata

    /**
     * Three-dimensional array of all possible combinations of squares + piece types + colours
     * (64 * 6 * 2) = 768 possible combinations.
     */
    private static final long[][][] PIECE_SQUARE_HASH = new long[64][6][2];

    private static final long[] CASTLING_RIGHTS = new long[16];

    private static long enPassantTarget;

    private static long sideToMove;

    private static final int white = 1;
    private static final int black = 0;

    static {

        Random random = new Random();

        for (int squareIndex = 0; squareIndex < 64; squareIndex++) {

            for (int pieceIndex : PieceType.indices()) {

                PIECE_SQUARE_HASH[squareIndex][pieceIndex][white] = random.nextLong();
                PIECE_SQUARE_HASH[squareIndex][pieceIndex][black] = random.nextLong();
            }
        }

        for (int i = 0; i < CASTLING_RIGHTS.length; i++) {
            CASTLING_RIGHTS[i] = random.nextLong();
        }

    }

}
