package com.kelseyde.calvin.evaluation.pawnstructure;

import com.kelseyde.calvin.board.bitboard.BitboardUtils;
import com.kelseyde.calvin.board.bitboard.Bits;
import com.kelseyde.calvin.utils.BoardUtils;

public class PawnBits {

    public static final long[] ADJACENT_FILE_MASK = generateAdjacentFileMask();
    public static final long[] TRIPLE_FILE_MASK = generateTripleFileMask();

    public static final long[] WHITE_PASSED_PAWN_MASK = generatePassedPawnMask(true);
    public static final long[] BLACK_PASSED_PAWN_MASK = generatePassedPawnMask(false);

    public static final long[] WHITE_PROTECTED_PAWN_MASK = generateProtectedPawnMask(true);
    public static final long[] BLACK_PROTECTED_PAWN_MASK = generateProtectedPawnMask(false);

    private static long[] generateAdjacentFileMask() {
        long[] adjacentFileMasks = new long[8];
        for (int i = 0; i < 8; i++) {
            long left = i > 0 ? Bits.FILE_A << (i - 1) : 0;
            long right = i < 7 ? Bits.FILE_A << (i + 1) : 0;
            adjacentFileMasks[i] = left | right;
        }
        return adjacentFileMasks;
    }

    private static long[] generateTripleFileMask() {

        long[] tripleFileMasks = new long[8];
        for (int i = 0; i < 8; i++) {
            long fileMask = Bits.FILE_MASKS[i];
            long adjacentFileMask = ADJACENT_FILE_MASK[i];
            tripleFileMasks[i] = fileMask | adjacentFileMask;
        }
        return tripleFileMasks;

    }

    private static long[] generatePassedPawnMask(boolean isWhite) {
        long[] passedPawnMask = new long[64];
        for (int square = 0; square < 64; square++) {
            int file = BoardUtils.getFile(square);
            int rank = BoardUtils.getRank(square);

            long fileMask = Bits.FILE_MASKS[file];
            long tripleFileMask = fileMask | ADJACENT_FILE_MASK[file];

            long forwardMask = isWhite ? ~(Long.MAX_VALUE >>> (64 - 8 * (rank + 1))) : ((1L << 8 * rank) - 1);
            passedPawnMask[square] = tripleFileMask & forwardMask;
        }
        return passedPawnMask;
    }

    private static long[] generateProtectedPawnMask(boolean isWhite) {
        long[] pawnProtectionMask = new long[64];
        for (int square = 0; square < 64; square++) {
            long squareBB = 1L << square;
            pawnProtectionMask[square] = isWhite ?
                    BitboardUtils.shiftSouthEast(squareBB) | BitboardUtils.shiftSouthWest(squareBB) :
                    BitboardUtils.shiftNorthEast(squareBB) | BitboardUtils.shiftNorthWest(squareBB);
        }
        return pawnProtectionMask;
    }

}
