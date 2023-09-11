package com.kelseyde.calvin.service.generator;

import com.kelseyde.calvin.model.game.Game;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.move.Move;

import java.util.Set;

public interface PseudoLegalMoveGenerator {

    PieceType getPieceType();

    Set<Move> generateLegalMoves(Game game, int startSquare);

}
