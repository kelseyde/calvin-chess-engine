package com.kelseyde.calvin.model;

public class BitBoards {

    public static final BitBoard EMPTY_BOARD = new BitBoard(0L);
    public static final BitBoard INDEX_BOARD = new BitBoard(1L);

    public static final BitBoard RANK_1 = new BitBoard(0b0000000000000000000000000000000000000000000000000000000011111111L);
    public static final BitBoard RANK_2 = RANK_1.shiftNorth();
    public static final BitBoard RANK_3 = RANK_2.shiftNorth();
    public static final BitBoard RANK_4 = RANK_3.shiftNorth();
    public static final BitBoard RANK_5 = RANK_4.shiftNorth();
    public static final BitBoard RANK_6 = RANK_5.shiftNorth();
    public static final BitBoard RANK_7 = RANK_6.shiftNorth();
    public static final BitBoard RANK_8 = RANK_7.shiftNorth();

    public static final BitBoard FILE_A = new BitBoard(0b0000000100000001000000010000000100000001000000010000000100000001L);
    public static final BitBoard FILE_B = FILE_A.shiftEast();
    public static final BitBoard FILE_C = FILE_B.shiftEast();
    public static final BitBoard FILE_D = FILE_C.shiftEast();
    public static final BitBoard FILE_E = FILE_D.shiftEast();
    public static final BitBoard FILE_F = FILE_E.shiftEast();
    public static final BitBoard FILE_G = FILE_F.shiftEast();
    public static final BitBoard FILE_H = FILE_G.shiftEast();

    public static final BitBoard WHITE_PAWNS_START = new BitBoard(0b0000000000000000000000000000000000000000000000001111111100000000L);
    public static final BitBoard WHITE_KNIGHTS_START = new BitBoard(0b0000000000000000000000000000000000000000000000000000000001000010L);
    public static final BitBoard WHITE_BISHOPS_START = new BitBoard(0b0000000000000000000000000000000000000000000000000000000000100100L);
    public static final BitBoard WHITE_ROOKS_START = new BitBoard(0b0000000000000000000000000000000000000000000000000000000010000001L);
    public static final BitBoard WHITE_QUEENS_START = new BitBoard(0b0000000000000000000000000000000000000000000000000000000000010000L);
    public static final BitBoard WHITE_KING_START = new BitBoard(0b0000000000000000000000000000000000000000000000000000000000001000L);

    public static final BitBoard BLACK_PAWNS_START = new BitBoard(0b0000000011111111000000000000000000000000000000000000000000000000L);
    public static final BitBoard BLACK_KNIGHTS_START = new BitBoard(0b0100001000000000000000000000000000000000000000000000000000000000L);
    public static final BitBoard BLACK_BISHOPS_START = new BitBoard(0b0010010000000000000000000000000000000000000000000000000000000000L);
    public static final BitBoard BLACK_ROOKS_START = new BitBoard(0b1000000100000000000000000000000000000000000000000000000000000000L);
    public static final BitBoard BLACK_QUEENS_START = new BitBoard(0b0001000000000000000000000000000000000000000000000000000000000000L);
    public static final BitBoard BLACK_KING_START = new BitBoard(0b0000100000000000000000000000000000000000000000000000000000000000L);

}
