package com.kelseyde.calvin.board.engine;

import com.kelseyde.calvin.board.PieceType;

import java.util.Map;

public class PieceMaterialValues {

    private static final Map<PieceType, Integer> MATERIAL_VALUES = Map.of(
            PieceType.PAWN, 10,
            PieceType.KNIGHT, 30,
            PieceType.BISHOP, 30,
            PieceType.ROOK, 50,
            PieceType.QUEEN, 90,
            PieceType.KING, 1000
    );

    public static int get(PieceType pieceType) {
        return MATERIAL_VALUES.get(pieceType);
    }

}
