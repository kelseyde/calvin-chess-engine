package com.kelseyde.calvin.board;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum PieceType {
    PAWN("p", 0),
    KNIGHT("n", 1),
    BISHOP("b", 2),
    ROOK("r", 3),
    QUEEN("q", 4),
    KING("k", 5);

    private final String pieceCode;
    private final int index;

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
