package com.kelseyde.calvin.service.game.drawcalculator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.DrawType;
import com.kelseyde.calvin.model.Game;
import lombok.Getter;

public class InsufficientMaterialCalculator implements DrawCalculator {

    @Getter
    private final DrawType drawType = DrawType.INSUFFICIENT_MATERIAL;

    @Override
    public boolean isDraw(Game game) {
        Board board = game.getBoard();

        if (board.getWhitePawns() != 0 || board.getWhiteRooks() != 0 || board.getWhiteQueens() != 0
            || board.getBlackPawns() != 0 || board.getBlackRooks() != 0 || board.getBlackQueens() != 0) {
            return false;
        }
        long whitePieces = board.getWhiteKnights() | board.getWhiteBishops();
        long blackPieces = board.getBlackKnights() |  board.getBlackBishops();

        return (Long.bitCount(whitePieces) == 0 || Long.bitCount(whitePieces) == 1)
                && (Long.bitCount(blackPieces) == 0 || Long.bitCount(blackPieces) == 1);
    }

}
