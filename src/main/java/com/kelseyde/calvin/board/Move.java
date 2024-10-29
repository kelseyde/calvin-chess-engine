package com.kelseyde.calvin.board;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents a single chess move. The move is encoded as a 16-bit integer. Bits 0 - 5 represent the start square,
 * bits 6 - 11 represent the end square, and bits 12 - 15 represent special move flags.
 *
 * @see <a href="https://www.chessprogramming.org/Encoding_Moves">Chess Programming Wiki</a>.
 */
public record Move(short value) {

    public static class MoveFlag {
        public static final short NONE              = 0b0000;
        public static final short EN_PASSANT        = 0b0001;
        public static final short CASTLE            = 0b0010;
        public static final short PAWN_DOUBLE_PUSH  = 0b0011;
        public static final short PROMO_QUEEN       = 0b0100;
        public static final short PROMO_KNIGHT      = 0b0101;
        public static final short PROMO_ROOK        = 0b0110;
        public static final short PROMO_BISHOP      = 0b0111;
    }

    public static final int FROM_MASK = 0b0000000000111111;
    public static final int TO_MASK = 0b0000111111000000;

    public Move(int from, int to) {
        this((short) (from | to << 6));
    }

    public Move(int from, int to, int flag) {
        this((short) (from | (to << 6) | (flag << 12)));
    }

    public int from() {
        return value & FROM_MASK;
    }

    public int to() {
        return (value & TO_MASK) >>> 6;
    }

    public Piece promoPiece() {
        return switch (value >>> 12) {
            case MoveFlag.PROMO_QUEEN -> Piece.QUEEN;
            case MoveFlag.PROMO_ROOK -> Piece.ROOK;
            case MoveFlag.PROMO_BISHOP -> Piece.BISHOP;
            case MoveFlag.PROMO_KNIGHT -> Piece.KNIGHT;
            default -> null;
        };
    }

    public boolean isPromotion() {
        return (value >>> 12) >= MoveFlag.PROMO_QUEEN;
    }

    public boolean isEnPassant() {
        return (value >>> 12) == MoveFlag.EN_PASSANT;
    }

    public boolean isCastling() {
        return (value >>> 12) == MoveFlag.CASTLE;
    }

    public boolean isPawnDoubleMove() {
        return (value >>> 12) == MoveFlag.PAWN_DOUBLE_PUSH;
    }

    /**
     * Checks if this move matches another move, excluding the special move flag.
     */
    public boolean matches(Move move) {
        if (move == null) return false;
        boolean squareMatch = from() == move.from() && to() == move.to();
        boolean promotionMatch = Optional.ofNullable(promoPiece())
                .map(piece -> piece.equals(move.promoPiece()))
                .orElse(true);
        return squareMatch && promotionMatch;
    }

    /**
     * Generate a {@link Move} from combined algebraic notation (e.g. "e2e4"), as used in the UCI protocol.
     * Special case promotion: "a2a1q" - values 'q' | 'b' | 'r' | 'n'
     */
    public static Move fromUCI(String uci) {
        int from = Bits.Square.fromNotation(uci.substring(0, 2));
        int to = Bits.Square.fromNotation(uci.substring(2, 4));

        int flag = MoveFlag.NONE;
        if (uci.length() == 5) {
            String pieceCode = uci.substring(4, 5);
            Piece promotionPieceType = Arrays.stream(Piece.values())
                    .filter(entry -> entry.code().equalsIgnoreCase(pieceCode))
                    .findAny().orElseThrow();
            flag = Piece.promoFlag(promotionPieceType);
        }
        return new Move(from, to, flag);
    }

    public static Move fromUCI(String uci, int flag) {
        int from = Bits.Square.fromNotation(uci.substring(0, 2));
        int to = Bits.Square.fromNotation(uci.substring(2, 4));
        return new Move(from, to, flag);
    }

    public static String toUCI(Move move) {
        if (move == null) return "-";
        String notation = Bits.Square.toNotation(move.from()) + Bits.Square.toNotation(move.to());
        if (move.promoPiece() != null) {
            notation += move.promoPiece().code();
        }
        return notation;
    }

}
