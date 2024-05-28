package com.kelseyde.calvin.board;

import com.kelseyde.calvin.generation.Attacks;
import com.kelseyde.calvin.utils.BoardUtils;

/**
 * Common bitboards stored here for general utility. Includes bitboards for ranks, files, diagonals, anti-diagonals,
 * castling masks, piece starting positions, etc.
 */
public class Bits {

    public static final long ALL_SQUARES = ~0L;
    public static final long NO_SQUARES = 0L;

    public static final long RANK_1 = 0b0000000000000000000000000000000000000000000000000000000011111111L;
    public static final long RANK_2 = Bitwise.shiftNorth(RANK_1);
    public static final long RANK_3 = Bitwise.shiftNorth(RANK_2);
    public static final long RANK_4 = Bitwise.shiftNorth(RANK_3);
    public static final long RANK_5 = Bitwise.shiftNorth(RANK_4);
    public static final long RANK_6 = Bitwise.shiftNorth(RANK_5);
    public static final long RANK_7 = Bitwise.shiftNorth(RANK_6);
    public static final long RANK_8 = Bitwise.shiftNorth(RANK_7);
    public static final long[] RANK_MASKS = {RANK_1, RANK_2, RANK_3, RANK_4, RANK_5, RANK_6, RANK_7, RANK_8};

    public static final long FILE_A = 0b0000000100000001000000010000000100000001000000010000000100000001L;
    public static final long FILE_B = Bitwise.shiftEast(FILE_A);
    public static final long FILE_C = Bitwise.shiftEast(FILE_B);
    public static final long FILE_D = Bitwise.shiftEast(FILE_C);
    public static final long FILE_E = Bitwise.shiftEast(FILE_D);
    public static final long FILE_F = Bitwise.shiftEast(FILE_E);
    public static final long FILE_G = Bitwise.shiftEast(FILE_F);
    public static final long FILE_H = Bitwise.shiftEast(FILE_G);
    public static final long[] FILE_MASKS = {FILE_A, FILE_B, FILE_C, FILE_D, FILE_E, FILE_F, FILE_G, FILE_H};

    public static final long DIAGONAL_1 =   0x1L;
    public static final long DIAGONAL_2 =   0x102L;
    public static final long DIAGONAL_3 =   0x10204L;
    public static final long DIAGONAL_4 =   0x1020408L;
    public static final long DIAGONAL_5 =   0x102040810L;
    public static final long DIAGONAL_6 =   0x10204081020L;
    public static final long DIAGONAL_7 =   0x1020408102040L;
    public static final long DIAGONAL_8 =   0x102040810204080L;
    public static final long DIAGONAL_9 =   0x204081020408000L;
    public static final long DIAGONAL_10 =  0x408102040800000L;
    public static final long DIAGONAL_11 =  0x810204080000000L;
    public static final long DIAGONAL_12 =  0x1020408000000000L;
    public static final long DIAGONAL_13 =  0x2040800000000000L;
    public static final long DIAGONAL_14 =  0x4080000000000000L;
    public static final long DIAGONAL_15 =  0x8000000000000000L;
    public static final long[] DIAGONAL_MASKS = {
                    DIAGONAL_1, DIAGONAL_2, DIAGONAL_3, DIAGONAL_4, DIAGONAL_5, DIAGONAL_6, DIAGONAL_7, DIAGONAL_8,
                    DIAGONAL_9, DIAGONAL_10, DIAGONAL_11, DIAGONAL_12, DIAGONAL_13, DIAGONAL_14, DIAGONAL_15
            };

    public static final long ANTI_DIAGONAL_MASK_1 =   0x80L;
    public static final long ANTI_DIAGONAL_MASK_2 =   0x8040L;
    public static final long ANTI_DIAGONAL_MASK_3 =   0x804020L;
    public static final long ANTI_DIAGONAL_MASK_4 =   0x80402010L;
    public static final long ANTI_DIAGONAL_MASK_5 =   0x8040201008L;
    public static final long ANTI_DIAGONAL_MASK_6 =   0x804020100804L;
    public static final long ANTI_DIAGONAL_MASK_7 =   0x80402010080402L;
    public static final long ANTI_DIAGONAL_MASK_8 =   0x8040201008040201L;
    public static final long ANTI_DIAGONAL_MASK_9 =   0x4020100804020100L;
    public static final long ANTI_DIAGONAL_MASK_10 =  0x2010080402010000L;
    public static final long ANTI_DIAGONAL_MASK_11 =  0x1008040201000000L;
    public static final long ANTI_DIAGONAL_MASK_12 =  0x804020100000000L;
    public static final long ANTI_DIAGONAL_MASK_13 =  0x402010000000000L;
    public static final long ANTI_DIAGONAL_MASK_14 =  0x201000000000000L;
    public static final long ANTI_DIAGONAL_MASK_15 =  0x100000000000000L;
    public static final long[] ANTI_DIAGONAL_MASKS = {
                    ANTI_DIAGONAL_MASK_1, ANTI_DIAGONAL_MASK_2, ANTI_DIAGONAL_MASK_3, ANTI_DIAGONAL_MASK_4, ANTI_DIAGONAL_MASK_5,
                    ANTI_DIAGONAL_MASK_6, ANTI_DIAGONAL_MASK_7, ANTI_DIAGONAL_MASK_8, ANTI_DIAGONAL_MASK_9, ANTI_DIAGONAL_MASK_10,
                    ANTI_DIAGONAL_MASK_11, ANTI_DIAGONAL_MASK_12, ANTI_DIAGONAL_MASK_13, ANTI_DIAGONAL_MASK_14, ANTI_DIAGONAL_MASK_15
            };

    public static final long KINGSIDE_MASK = FILE_F | FILE_G | FILE_H;
    public static final long QUEENSIDE_MASK = FILE_A | FILE_B | FILE_C;

    public static final long WHITE_HALF_MASK = 0x00000000FFFFFFFFL; // Bits 0 to 31
    public static final long BLACK_HALF_MASK = 0xFFFFFFFF00000000L; // Bits 32 to 63

    // Masks for the squares that must be unoccupied for legal castling
    public static final long WHITE_QUEENSIDE_CASTLE_TRAVEL_MASK = 0x000000000000000EL;
    public static final long WHITE_KINGSIDE_CASTLE_TRAVEL_MASK = 0x0000000000000060L;
    public static final long BLACK_QUEENSIDE_CASTLE_TRAVEL_MASK = WHITE_QUEENSIDE_CASTLE_TRAVEL_MASK << (7 * 8);
    public static final long BLACK_KINGSIDE_CASTLE_TRAVEL_MASK = WHITE_KINGSIDE_CASTLE_TRAVEL_MASK  << (7 * 8);

    // Masks for the squares that must not be attacked for legal castling
    public static final long WHITE_QUEENSIDE_CASTLE_SAFE_MASK = 0x000000000000001CL;
    public static final long WHITE_KINGSIDE_CASTLE_SAFE_MASK = WHITE_QUEENSIDE_CASTLE_SAFE_MASK << 2;
    public static final long BLACK_QUEENSIDE_CASTLE_SAFE_MASK = WHITE_QUEENSIDE_CASTLE_SAFE_MASK << (7 * 8);
    public static final long BLACK_KINGSIDE_CASTLE_SAFE_MASK = WHITE_KINGSIDE_CASTLE_SAFE_MASK  << (7 * 8);

    // Starting positions for the white pieces
    public static final long WHITE_PAWNS_START = 0b0000000000000000000000000000000000000000000000001111111100000000L;
    public static final long WHITE_KNIGHTS_START = 0b0000000000000000000000000000000000000000000000000000000001000010L;
    public static final long WHITE_BISHOPS_START = 0b0000000000000000000000000000000000000000000000000000000000100100L;
    public static final long WHITE_ROOKS_START = 0b0000000000000000000000000000000000000000000000000000000010000001L;
    public static final long WHITE_QUEENS_START = 0b0000000000000000000000000000000000000000000000000000000000001000L;
    public static final long WHITE_KING_START = 0b0000000000000000000000000000000000000000000000000000000000010000L;

    // Starting positions for the black pieces
    public static final long BLACK_PAWNS_START = 0b0000000011111111000000000000000000000000000000000000000000000000L;
    public static final long BLACK_KNIGHTS_START = 0b0100001000000000000000000000000000000000000000000000000000000000L;
    public static final long BLACK_BISHOPS_START = 0b0010010000000000000000000000000000000000000000000000000000000000L;
    public static final long BLACK_ROOKS_START = 0b1000000100000000000000000000000000000000000000000000000000000000L;
    public static final long BLACK_QUEENS_START = 0b0000100000000000000000000000000000000000000000000000000000000000L;
    public static final long BLACK_KING_START = 0b0001000000000000000000000000000000000000000000000000000000000000L;

    public static final int INITIAL_CASTLING_RIGHTS = 0b1111;
    public static final int CLEAR_WHITE_CASTLING_MASK = 0b1100;
    public static final int CLEAR_BLACK_CASTLING_MASK = 0b0011;
    public static final int CLEAR_WHITE_KINGSIDE_MASK = 0b1110;
    public static final int CLEAR_BLACK_KINGSIDE_MASK = 0b1011;
    public static final int CLEAR_WHITE_QUEENSIDE_MASK = 0b1101;
    public static final int CLEAR_BLACK_QUEENSIDE_MASK = 0b0111;

    public static final long[] WEST_FILE_MASK = generateWestFileMask();
    public static final long[] EAST_FILE_MASK = generateEastFileMask();
    public static final long[] ADJACENT_FILE_MASK = generateAdjacentFileMask();
    public static final long[] TRIPLE_FILE_MASK = generateTripleFileMask();

    public static final long[] WHITE_PASSED_PAWN_MASK = generatePassedPawnMask(true);
    public static final long[] BLACK_PASSED_PAWN_MASK = generatePassedPawnMask(false);

    public static final long[] WHITE_PROTECTED_PAWN_MASK = generateProtectedPawnMask(true);
    public static final long[] BLACK_PROTECTED_PAWN_MASK = generateProtectedPawnMask(false);

    public static final long[] WHITE_FORWARD_ADJACENT_MASK = generateForwardAdjacentMask(true);
    public static final long[] BLACK_FORWARD_ADJACENT_MASK = generateForwardAdjacentMask(false);

    public static final long[] INNER_RING_MASK = generateInnerRingMask();
    public static final long[] OUTER_RING_MASK = generateOuterRingMask();

    public static final long[] WHITE_KING_SAFETY_ZONE = generateKingSafetyZone(true);
    public static final long[] BLACK_KING_SAFETY_ZONE = generateKingSafetyZone(false);

    private static long[] generateWestFileMask() {
        long[] westFileMasks = new long[8];
        for (int i = 0; i < 8; i++) {
            westFileMasks[i] = i > 0 ? Bits.FILE_A << (i - 1) : 0;
        }
        return westFileMasks;
    }

    private static long[] generateEastFileMask() {
        long[] westFileMasks = new long[8];
        for (int i = 0; i < 8; i++) {
            westFileMasks[i] = i < 7 ? Bits.FILE_A << (i + 1) : 0;
        }
        return westFileMasks;
    }

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

    private static long[] generatePassedPawnMask(boolean white) {
        long[] passedPawnMask = new long[64];
        for (int square = 0; square < 64; square++) {
            int file = BoardUtils.getFile(square);
            int rank = BoardUtils.getRank(square);

            long fileMask = Bits.FILE_MASKS[file];
            long tripleFileMask = fileMask | ADJACENT_FILE_MASK[file];

            long forwardMask = white ? ~(Long.MAX_VALUE >>> (64 - 8 * (rank + 1))) : ((1L << 8 * rank) - 1);
            passedPawnMask[square] = tripleFileMask & forwardMask;
        }
        return passedPawnMask;
    }

    private static long[] generateProtectedPawnMask(boolean white) {
        long[] pawnProtectionMask = new long[64];
        for (int square = 0; square < 64; square++) {
            long squareBB = 1L << square;
            pawnProtectionMask[square] = white ?
                    Bitwise.shiftSouthEast(squareBB) | Bitwise.shiftSouthWest(squareBB) :
                    Bitwise.shiftNorthEast(squareBB) | Bitwise.shiftNorthWest(squareBB);
        }
        return pawnProtectionMask;
    }

    private static long[] generateForwardAdjacentMask(boolean white) {

        long[] forwardAdjacentMasks = new long[64];

        for (int square = 0; square < 64; square++) {
            int file = BoardUtils.getFile(square);
            int rank = BoardUtils.getRank(square);

            long adjacentMask = ADJACENT_FILE_MASK[file];
            long forwardMask = white ? ~(Long.MAX_VALUE >>> (64 - 8 * (rank + 1))) : ((1L << 8 * rank) - 1);
            forwardAdjacentMasks[square] = forwardMask & adjacentMask;
        }

        return forwardAdjacentMasks;

    }

    private static long[] generateInnerRingMask() {
        long[] innerRinkMasks = new long[64];
        for (int square = 0; square < 64; square++) {
            long squareBB = 1L << square;
            long mask = Bitwise.shiftNorth(squareBB) | Bitwise.shiftNorthEast(squareBB) | Bitwise.shiftEast(squareBB)
                    | Bitwise.shiftSouthEast(squareBB) | Bitwise.shiftSouth(squareBB) | Bitwise.shiftSouthWest(squareBB)
                    | Bitwise.shiftWest(squareBB) | Bitwise.shiftNorthWest(squareBB);
            innerRinkMasks[square] = mask;
        }
        return innerRinkMasks;
    }

    private static long[] generateOuterRingMask() {
        long[] outerRingMasks = new long[64];
        for (int square = 0; square < 64; square++) {
            long squareBB = 1L << square;
            long mask = Bitwise.shiftNorth(Bitwise.shiftNorth(squareBB)) | Bitwise.shiftNorthEast(Bitwise.shiftNorthEast(squareBB))
                    | Bitwise.shiftEast(Bitwise.shiftEast(squareBB)) | Bitwise.shiftSouthEast(Bitwise.shiftSouthEast(squareBB))
                    | Bitwise.shiftSouth(Bitwise.shiftSouth(squareBB)) | Bitwise.shiftSouthWest(Bitwise.shiftSouthWest(squareBB))
                    | Bitwise.shiftWest(Bitwise.shiftWest(squareBB)) | Bitwise.shiftNorthWest(Bitwise.shiftNorthWest(squareBB))
                    | Attacks.knightAttacks(square);
            outerRingMasks[square] = mask;
        }
        return outerRingMasks;
    }

    private static long[] generateKingSafetyZone(boolean white) {
        long[] kingSafetyZones = new long[64];
        for (int square = 0; square < 64; square++) {
            long innerRingMask = INNER_RING_MASK[square];
            long kingSafetyZone = white ?
                    innerRingMask | Bitwise.shiftNorth(innerRingMask) :
                    innerRingMask | Bitwise.shiftSouth(innerRingMask);
            kingSafetyZones[square] = kingSafetyZone;
        }
        return kingSafetyZones;
    }

}
