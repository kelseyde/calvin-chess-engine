package com.kelseyde.calvin.service.game.generator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.PieceType;

public class BishopMoveGenerator extends SlidingMoveGenerator {

    @Override
    public PieceType getPieceType() {
        return PieceType.BISHOP;
    }

    @Override
    protected long getPieceBitboard(Board board) {
        return board.isWhiteToMove() ? board.getWhiteBishops() : board.getBlackBishops();
    }

    @Override
    protected boolean isOrthogonal() {
        return false;
    }

    @Override
    protected boolean isDiagonal() {
        return true;
    }

}
