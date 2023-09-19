package com.kelseyde.calvin.service.game.generator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.PieceType;

public class RookMoveGenerator extends SlidingMoveGenerator {

    @Override
    public PieceType getPieceType() {
        return PieceType.ROOK;
    }

    @Override
    protected long getPieceBitboard(Board board) {
        return board.isWhiteToMove() ? board.getWhiteRooks() : board.getBlackRooks();
    }

    @Override
    protected boolean isOrthogonal() {
        return true;
    }

    @Override
    protected boolean isDiagonal() {
        return false;
    }

}
