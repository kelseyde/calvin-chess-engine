package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.PieceType;

/**
 * The relative material value of each piece type, measured in centipawns (hundredths of a pawn). Using the widely popular
 * values suggested by Tomasz Michniewski in 1995, although there are alternative rankings.
 *
 * @see <a href="https://www.chessprogramming.org/Point_Value">Chess Programming Wiki</a>
 */
public class PieceValues {

    public static final int[] SIMPLE_VALUES = new int[] { 100, 320, 330, 500, 900, 0 };
    public static final int[] MIDDLEGAME_VALUES = new int[] { 82, 337, 365, 477, 1025, 0 };
    public static final int[] ENDGAME_VALUES = new int[] { 94, 281, 297, 512,  936,  0 };

    public static final int BISHOP_PAIR_BONUS = 50;

    public static int valueOf(PieceType pieceType) {
        return SIMPLE_VALUES[pieceType.getIndex()];
    }

}
