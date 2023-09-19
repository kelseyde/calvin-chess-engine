package com.kelseyde.calvin.board.bitboard;

import com.kelseyde.calvin.board.bitboard.BitBoardUtil;

public class BitBoardConstants {

    public static final long RANK_1 = 0b0000000000000000000000000000000000000000000000000000000011111111L;
    public static final long RANK_2 = BitBoardUtil.shiftNorth(RANK_1);
    public static final long RANK_3 = BitBoardUtil.shiftNorth(RANK_2);
    public static final long RANK_4 = BitBoardUtil.shiftNorth(RANK_3);
    public static final long RANK_5 = BitBoardUtil.shiftNorth(RANK_4);
    public static final long RANK_6 = BitBoardUtil.shiftNorth(RANK_5);
    public static final long RANK_7 = BitBoardUtil.shiftNorth(RANK_6);
    public static final long RANK_8 = BitBoardUtil.shiftNorth(RANK_7);
    public static final long[] RANK_MASKS = {RANK_1, RANK_2, RANK_3, RANK_4, RANK_5, RANK_6, RANK_7, RANK_8};

    public static final long FILE_A = 0b0000000100000001000000010000000100000001000000010000000100000001L;
    public static final long FILE_B = BitBoardUtil.shiftEast(FILE_A);
    public static final long FILE_C = BitBoardUtil.shiftEast(FILE_B);
    public static final long FILE_D = BitBoardUtil.shiftEast(FILE_C);
    public static final long FILE_E = BitBoardUtil.shiftEast(FILE_D);
    public static final long FILE_F = BitBoardUtil.shiftEast(FILE_E);
    public static final long FILE_G = BitBoardUtil.shiftEast(FILE_F);
    public static final long FILE_H = BitBoardUtil.shiftEast(FILE_G);
    public static final long[] FILE_MASKS = {FILE_A, FILE_B, FILE_C, FILE_D, FILE_E, FILE_F, FILE_G, FILE_H};

    public static final long[] DIAGONAL_MASKS = {
                    0x1L, 0x102L, 0x10204L, 0x1020408L, 0x102040810L, 0x10204081020L, 0x1020408102040L,
                    0x102040810204080L, 0x204081020408000L, 0x408102040800000L, 0x810204080000000L,
                    0x1020408000000000L, 0x2040800000000000L, 0x4080000000000000L, 0x8000000000000000L
            };
    public static final long[] ANTI_DIAGONAL_MASKS = {
                    0x80L, 0x8040L, 0x804020L, 0x80402010L, 0x8040201008L, 0x804020100804L, 0x80402010080402L,
                    0x8040201008040201L, 0x4020100804020100L, 0x2010080402010000L, 0x1008040201000000L,
                    0x804020100000000L, 0x402010000000000L, 0x201000000000000L, 0x100000000000000L
            };

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
