package com.kelseyde.calvin.board.piece;

import java.util.Map;

/**
 * The relative material value of each piece type, measured in centipawns (hundredths of a pawn). Using the widely popular
 * values suggested by Tomasz Michniewski in 1995, although there are alternative rankings.
 * @see <a href="https://www.chessprogramming.org/Point_Value">Chess Programming Wiki</a>
 */
public class PieceValues {

    private static final Map<PieceType, Integer> MATERIAL_VALUES = Map.of(
            PieceType.PAWN, 100,
            PieceType.KNIGHT, 320,
            PieceType.BISHOP, 330,
            PieceType.ROOK, 500,
            PieceType.QUEEN, 900,
            PieceType.KING, 10000
    );

    public static int get(PieceType pieceType) {
        return MATERIAL_VALUES.get(pieceType);
    }

}
