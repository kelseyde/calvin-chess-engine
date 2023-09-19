package com.kelseyde.calvin.board.piece;

import lombok.Getter;

public enum PieceType {
    PAWN("P"),
    KNIGHT("N"),
    BISHOP("B"),
    ROOK("R"),
    QUEEN("Q"),
    KING("K");

    @Getter
    final String pieceCode;

    PieceType(String pieceCode) {
        this.pieceCode = pieceCode;
    }

    public static PieceType fromChar(char pieceChar) {
        return switch (Character.toUpperCase(pieceChar)) {
            case 'K' -> PieceType.KING;
            case 'Q' -> PieceType.QUEEN;
            case 'R' -> PieceType.ROOK;
            case 'B' -> PieceType.BISHOP;
            case 'N' -> PieceType.KNIGHT;
            case 'P' -> PieceType.PAWN;
            default -> throw new IllegalArgumentException(String.format("%s is not a valid piece character!", pieceChar));
        };
    }

    public char toChar() {
        return switch (this) {
            case KING -> 'K';
            case QUEEN -> 'Q';
            case ROOK -> 'R';
            case BISHOP -> 'B';
            case KNIGHT -> 'N';
            case PAWN -> 'P';
        };
    }

}
