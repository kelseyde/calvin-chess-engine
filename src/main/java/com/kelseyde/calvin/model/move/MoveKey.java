package com.kelseyde.calvin.model.move;

import com.kelseyde.calvin.model.PieceType;

// TODO remove
public record MoveKey(int startSquare, int endSquare, PieceType promotionPieceType) {
}
