package com.kelseyde.calvin.board.move;

import com.kelseyde.calvin.board.piece.PieceType;

// TODO remove
public record MoveKey(int startSquare, int endSquare, PieceType promotionPieceType) {
}
