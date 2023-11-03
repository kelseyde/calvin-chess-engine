package com.kelseyde.calvin.board;

import lombok.EqualsAndHashCode;

import java.util.Optional;

/**
 * Represents a single chess move.
 * All the information required to represent a move can be encoded in 16 bits.
 * <p>
 * bit 0 - 5: start square (0 - 63)
 * bit 6 - 11: end square (0 - 63
 * bit 12 - 15: special moves (promotion, castling, pawn double moves, en passant)
 *
 * @see <a href="https://www.chessprogramming.org/Encoding_Moves">Chess Programming Wiki</a>
 * <p>
 * Largely inspired by Sebastian Lague's Chess Coding Adventure:
 * @see <a href="https://github.com/SebLague/Chess-Coding-Adventure">Chess Coding Adventure</a>
 */
@EqualsAndHashCode
public class Move {

    public static final short NO_FLAG = 0b0000;
    public static final short EN_PASSANT_FLAG = 0b0001;
    public static final short CASTLE_FLAG = 0b0010;
    public static final short PAWN_DOUBLE_MOVE_FLAG = 0b0011;
    public static final short PROMOTE_TO_QUEEN_FLAG = 0b0100;
    public static final short PROMOTE_TO_KNIGHT_FLAG = 0b0101;
    public static final short PROMOTE_TO_ROOK_FLAG = 0b0110;
    public static final short PROMOTE_TO_BISHOP_FLAG = 0b0111;

    public static final int START_SQUARE_MASK = 0b0000000000111111;
    public static final int END_SQUARE_MASK = 0b0000111111000000;

    private final short value;

    public Move(int startSquare, int endSquare) {
        this.value = (short) (startSquare | endSquare << 6);
    }

    public Move(int startSquare, int endSquare, short flag) {
        this.value = (short) (startSquare | endSquare << 6 | flag << 12);
    }

    public int getStartSquare() {
        return value & START_SQUARE_MASK;
    }

    public int getEndSquare() {
        return (value & END_SQUARE_MASK) >>> 6;
    }

    public PieceType getPromotionPieceType() {
        return switch (value >>> 12) {
            case PROMOTE_TO_QUEEN_FLAG -> PieceType.QUEEN;
            case PROMOTE_TO_ROOK_FLAG -> PieceType.ROOK;
            case PROMOTE_TO_BISHOP_FLAG -> PieceType.BISHOP;
            case PROMOTE_TO_KNIGHT_FLAG -> PieceType.KNIGHT;
            default -> null;
        };
    }

    public static short getPromotionFlag(PieceType pieceType) {
        if (pieceType == null) {
            return NO_FLAG;
        }
        return switch (pieceType) {
            case QUEEN -> PROMOTE_TO_QUEEN_FLAG;
            case ROOK -> PROMOTE_TO_ROOK_FLAG;
            case BISHOP -> PROMOTE_TO_BISHOP_FLAG;
            case KNIGHT -> PROMOTE_TO_KNIGHT_FLAG;
            default -> NO_FLAG;
        };
    }

    public boolean isPromotion() {
        return (value >>> 12) >= PROMOTE_TO_QUEEN_FLAG;
    }

    public boolean isEnPassant() {
        return (value >>> 12) == EN_PASSANT_FLAG;
    }

    public boolean isCastling() {
        return (value >>> 12) == CASTLE_FLAG;
    }

    public boolean isPawnDoubleMove() {
        return (value >>> 12) == PAWN_DOUBLE_MOVE_FLAG;
    }

    public boolean matches(Move move) {
        boolean squareMatch = getStartSquare() == move.getStartSquare() && getEndSquare() == move.getEndSquare();
        boolean promotionMatch = Optional.ofNullable(getPromotionPieceType())
                .map(piece -> piece.equals(move.getPromotionPieceType()))
                .orElse(true);
        return squareMatch && promotionMatch;
    }

}
