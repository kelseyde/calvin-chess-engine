package com.kelseyde.calvin.board;

public class Castling {

    public static class Standard {

        // Masks for the squares that must be unoccupied for legal castling (standard only)
        public static final long WHITE_QUEENSIDE_TRAVEL_MASK = 0x000000000000000EL;
        public static final long BLACK_QUEENSIDE_TRAVEL_MASK = WHITE_QUEENSIDE_TRAVEL_MASK << (7 * 8);
        public static final long WHITE_KINGSIDE_TRAVEL_MASK = 0x0000000000000060L;
        public static final long BLACK_KINGSIDE_TRAVEL_MASK = WHITE_KINGSIDE_TRAVEL_MASK << (7 * 8);
        // Masks for the squares that must not be attacked for legal castling (standard only)
        public static final long WHITE_QUEENSIDE_SAFE_MASK = 0x000000000000001CL;
        public static final long BLACK_QUEENSIDE_SAFE_MASK = WHITE_QUEENSIDE_SAFE_MASK << (7 * 8);
        public static final long WHITE_KINGSIDE_SAFE_MASK = WHITE_QUEENSIDE_SAFE_MASK << 2;
        public static final long BLACK_KINGSIDE_SAFE_MASK = WHITE_KINGSIDE_SAFE_MASK << (7 * 8);

    }

    public static final int INITIAL_CASTLING_RIGHTS = 0b1111;
    public static final int CLEAR_WHITE_CASTLING_MASK = 0b1100;
    public static final int CLEAR_BLACK_CASTLING_MASK = 0b0011;
    public static final int CLEAR_WHITE_KINGSIDE_MASK = 0b1110;
    public static final int CLEAR_BLACK_KINGSIDE_MASK = 0b1011;
    public static final int CLEAR_WHITE_QUEENSIDE_MASK = 0b1101;
    public static final int CLEAR_BLACK_QUEENSIDE_MASK = 0b0111;

    public static int rookFrom(boolean kingside, boolean white) {
        if (kingside) {
            return white ? 7 : 63;
        } else {
            return white ? 0 : 56;
        }
    }

    public static int rookTo(boolean kingside, boolean white) {
        if (kingside) {
            return white ? 5 : 61;
        } else {
            return white ? 3 : 59;
        }
    }


    public static int getRook(int rights, boolean white, boolean kingside) {
        int colourShift = white ? 0 : 16;
        int kingsideShift = kingside ? 0 : 8;
        return (rights >>> (colourShift + kingsideShift)) & 0xFF;
    }

    public static int setRook(int rights, boolean white, boolean kingside, int sq) {
        int colourShift = white ? 0 : 16;
        int kingsideShift = kingside ? 0 : 8;
        int mask = 0xFF << (colourShift + kingsideShift);
        return (rights & ~mask) | (sq << (colourShift + kingsideShift));
    }


    public static void main(String[] args) {
        int rights = 0;
        rights = setRook(rights, true, true, 7);
        int sq = getRook(rights, true, true);
        System.out.println(sq);
        rights = setRook(rights, true, false, -1);
        sq = getRook(rights, true, false);
        System.out.println(sq);
    }


}
