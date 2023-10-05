package com.kelseyde.calvin.evaluation.material;

import com.kelseyde.calvin.board.piece.PieceType;

/**
 * The relative material value of each piece type, measured in centipawns (hundredths of a pawn). Using the widely popular
 * values suggested by Tomasz Michniewski in 1995, although there are alternative rankings.
 *
 * @see <a href="https://www.chessprogramming.org/Point_Value">Chess Programming Wiki</a>
 */
public class PieceValues {
    public static final int PAWN = 100;
    public static final int KNIGHT = 320;
    public static final int BISHOP = 330;
    public static final int ROOK = 500;
    public static final int QUEEN = 900;
    public static final int KING = 10000;

    public static final int BISHOP_PAIR = 50;

    public static int valueOf(PieceType pieceType) {
        return switch (pieceType) {
            case PAWN -> PAWN;
            case KNIGHT -> KNIGHT;
            case BISHOP -> BISHOP;
            case ROOK -> ROOK;
            case QUEEN -> QUEEN;
            case KING -> KING;
        };
    }

}
