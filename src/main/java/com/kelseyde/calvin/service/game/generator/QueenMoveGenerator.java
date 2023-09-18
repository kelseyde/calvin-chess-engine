package com.kelseyde.calvin.service.game.generator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.PieceType;

public class QueenMoveGenerator extends SlidingMoveGenerator {

    @Override
    public PieceType getPieceType() {
        return PieceType.QUEEN;
    }

    @Override
    protected long getPieceBitboard(Board board) {
        return board.getTurn().isWhite() ? board.getWhiteQueens() : board.getBlackQueens();
    }

    @Override
    protected boolean isOrthogonal() {
        return true;
    }

    @Override
    protected boolean isDiagonal() {
        return true;
    }

}
