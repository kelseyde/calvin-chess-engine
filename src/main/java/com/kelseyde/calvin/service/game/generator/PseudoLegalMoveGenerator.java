package com.kelseyde.calvin.service.game.generator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.move.Move;

import java.util.Set;

public interface PseudoLegalMoveGenerator {

    PieceType getPieceType();

    Set<Move> generatePseudoLegalMoves(Board board, int startSquare);

    default Move.MoveBuilder moveBuilder() {
        return Move.builder()
                .pieceType(getPieceType());
    }

}
