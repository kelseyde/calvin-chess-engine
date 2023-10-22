package com.kelseyde.calvin.board.bitboard;

/**
 * Common bitboards stored here for general utility. Includes bitboards for ranks, files, diagonals, anti-diagonals,
 * castling masks, piece starting positions, etc.
 */
public class Bits {

    public static final long RANK_1 = 0b0000000000000000000000000000000000000000000000000000000011111111L;
    public static final long RANK_2 = BitboardUtils.shiftNorth(RANK_1);
    public static final long RANK_3 = BitboardUtils.shiftNorth(RANK_2);
    public static final long RANK_4 = BitboardUtils.shiftNorth(RANK_3);
    public static final long RANK_5 = BitboardUtils.shiftNorth(RANK_4);
    public static final long RANK_6 = BitboardUtils.shiftNorth(RANK_5);
    public static final long RANK_7 = BitboardUtils.shiftNorth(RANK_6);
    public static final long RANK_8 = BitboardUtils.shiftNorth(RANK_7);
    public static final long[] RANK_MASKS = {RANK_1, RANK_2, RANK_3, RANK_4, RANK_5, RANK_6, RANK_7, RANK_8};

    public static final long FILE_A = 0b0000000100000001000000010000000100000001000000010000000100000001L;
    public static final long FILE_B = BitboardUtils.shiftEast(FILE_A);
    public static final long FILE_C = BitboardUtils.shiftEast(FILE_B);
    public static final long FILE_D = BitboardUtils.shiftEast(FILE_C);
    public static final long FILE_E = BitboardUtils.shiftEast(FILE_D);
    public static final long FILE_F = BitboardUtils.shiftEast(FILE_E);
    public static final long FILE_G = BitboardUtils.shiftEast(FILE_F);
    public static final long FILE_H = BitboardUtils.shiftEast(FILE_G);
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

}
