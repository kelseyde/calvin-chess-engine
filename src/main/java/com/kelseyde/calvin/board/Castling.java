package com.kelseyde.calvin.board;

public class Castling {

    // Constants to represent shifts and encoding limits
    public static final int NO_ROOK = 64;
    private static final int SQUARE_MASK = 0x7F; // Mask to allow 7 bits, covering 0-64 range
    private static final int WK_SHIFT = 21; // White kingside rook (uppermost)
    private static final int WQ_SHIFT = 14; // White queenside rook
    private static final int BK_SHIFT = 7;  // Black kingside rook
    private static final int BQ_SHIFT = 0;  // Black queenside rook (lowermost)

    public static int rookFrom(boolean kingside, boolean white) {
        // Standard starting position for rooks
        if (kingside) {
            return white ? 7 : 63;
        } else {
            return white ? 0 : 56;
        }
    }

    public static int rookTo(boolean kingside, boolean white) {
        // Standard castling destination for rooks
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

    public static int startpos() {
        // Starting castling rights (standard chess only)
        return from(0, 7, 56, 63);
    }

    public static int from(int wk, int wq, int bk, int bq) {
        // Constructs castling rights from the starting rook squares
        return (encode(wk) << WK_SHIFT) | (encode(wq) << WQ_SHIFT) |
                (encode(bk) << BK_SHIFT) | (encode(bq) << BQ_SHIFT);
    }

    public static int getRook(int rights, boolean kingside, boolean white) {
        // Gets the starting rook square for the given side
        int shift = shift(kingside, white);
        return decode((rights >> shift) & SQUARE_MASK);
    }

    public static int setRook(int rights, boolean kingside, boolean white, int sq) {
        // Sets the starting rook square for the given side
        int shift = shift(kingside, white);
        rights &= ~(SQUARE_MASK << shift); // Clear the bits at the target shift
        rights |= (encode(sq) << shift);    // Set the encoded square at the shift
        return rights;
    }

    public static int clearRook(int rights, boolean kingside, boolean white) {
        // Unsets the starting rook square for the given side
        return setRook(rights, kingside, white, NO_ROOK);
    }

    public static int clearSide(int rights, boolean white) {
        // Unsets the starting rook squares for the given side
        return clearRook(clearRook(rights, true, white), false, white);
    }

    public static boolean kingsideAllowed(int rights, boolean white) {
        // Checks if kingside castling is allowed for the given side
        int shift = shift(true, white);
        return decode((rights >> shift) & SQUARE_MASK) != NO_ROOK;
    }

    public static boolean queensideAllowed(int rights, boolean white) {
        // Checks if queenside castling is allowed for the given side
        int shift = shift(false, white);
        return decode((rights >> shift) & SQUARE_MASK) != NO_ROOK;
    }

    public static int encode(int sq) {
        // Encodes the rook square to the castling rights (0-64 range, with 64 representing no rook)
        return (sq >= 0 && sq <= 64) ? sq : NO_ROOK;
    }

    public static int decode(int mask) {
        // Decodes the castling rights to the rook square (0-64 range, with 64 representing no rook)
        return (mask & SQUARE_MASK) <= 64 ? mask & SQUARE_MASK : NO_ROOK;
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
