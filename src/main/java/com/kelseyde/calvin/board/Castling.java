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

    // Constants to represent shifts and encoding limits
    private static final int NO_ROOK = 64;
    private static final int SQUARE_MASK = 0x3F; // Mask to keep only 6 bits for square 0-63
    private static final int WK_SHIFT = 18; // White kingside rook (uppermost)
    private static final int WQ_SHIFT = 12; // White queenside rook
    private static final int BK_SHIFT = 6;  // Black kingside rook
    private static final int BQ_SHIFT = 0;  // Black queenside rook (lowermost)

    public static int rookFrom(boolean kingside, boolean white) {
        // TODO update for 960
        if (kingside) {
            return white ? 7 : 63;
        } else {
            return white ? 0 : 56;
        }
    }

    public static int rookTo(boolean kingside, boolean white) {
        // TODO update for 960
        if (kingside) {
            return white ? 5 : 61;
        } else {
            return white ? 3 : 59;
        }
    }

    public static int empty() {
        // All rooks set to NO_ROOK, meaning no castling rights
        return (NO_ROOK << WK_SHIFT) | (NO_ROOK << WQ_SHIFT) | (NO_ROOK << BK_SHIFT) | (NO_ROOK << BQ_SHIFT);
    }

    public static int from(int wk, int wq, int bk, int bq) {
        // Constructs castling rights from the starting rook squares
        return (encode(wk) << WK_SHIFT) | (encode(wq) << WQ_SHIFT) |
                (encode(bk) << BK_SHIFT) | (encode(bq) << BQ_SHIFT);
    }

    public static int setRook(int rights, boolean kingside, boolean white, int sq) {
        // Sets the starting rook square for the given side
        int shift = shift(kingside, white);
        rights &= ~(SQUARE_MASK << shift); // Clear the bits at the target shift
        rights |= (encode(sq) << shift);    // Set the encoded square at the shift
        return rights;
    }

    public static boolean kingsideAllowed(int rights, boolean white) {
        // Checks if kingside castling is allowed for the given side
        int shift = shift(true, white);
        int square = decode((rights >> shift) & SQUARE_MASK);
        System.out.println("sq: " + square);
        return decode((rights >> shift) & SQUARE_MASK) != NO_ROOK;
    }

    public static boolean queensideAllowed(int rights, boolean white) {
        // Checks if queenside castling is allowed for the given side
        int shift = shift(false, white);
        return decode((rights >> shift) & SQUARE_MASK) != NO_ROOK;
    }

    private static int encode(int sq) {
        // Encodes the rook square to the castling rights (0-64 range, with 64 representing no rook)
        return sq == NO_ROOK ? NO_ROOK : (sq & SQUARE_MASK);
    }

    private static int decode(int mask) {
        // Decodes the castling rights to the rook square (0-64 range, with 64 representing no rook)
        return mask == NO_ROOK ? NO_ROOK : (mask & SQUARE_MASK);
    }

    private static int shift(boolean kingside, boolean white) {
        // Helper to determine the shift based on side and color
        if (white) {
            return kingside ? WK_SHIFT : WQ_SHIFT;
        } else {
            return kingside ? BK_SHIFT : BQ_SHIFT;
        }
    }


}
