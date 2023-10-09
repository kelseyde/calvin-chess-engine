package com.kelseyde.calvin.board;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum PieceType {
    PAWN("P", 0),
    KNIGHT("N", 1),
    BISHOP("B", 2),
    ROOK("R", 3),
    QUEEN("Q", 4),
    KING("K", 5);

    @Getter final String pieceCode;
    @Getter final int index;

    PieceType(String pieceCode, int index) {
        this.pieceCode = pieceCode;
        this.index = index;
    }

    public static List<Integer> indices() {
        return Arrays.stream(PieceType.values())
                .map(PieceType::getIndex)
                .toList();
    }

    public static PieceType fromPieceCode(String pieceCode) {
        return Arrays.stream(PieceType.values())
                .filter(pieceType -> pieceType.getPieceCode().equalsIgnoreCase(pieceCode))
                .findAny()
                .orElseThrow();
    }

}
