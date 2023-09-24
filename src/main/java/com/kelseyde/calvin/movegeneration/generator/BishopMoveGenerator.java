package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.piece.PieceType;

public class BishopMoveGenerator extends SlidingMoveGenerator {

    @Override
    public PieceType getPieceType() {
        return PieceType.BISHOP;
    }

    @Override
    protected long getSliders(Board board, boolean isWhite) {
        return isWhite ? board.getWhiteBishops() : board.getBlackBishops();
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
