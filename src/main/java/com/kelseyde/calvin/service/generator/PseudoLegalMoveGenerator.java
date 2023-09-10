package com.kelseyde.calvin.service.generator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Game;
import com.kelseyde.calvin.model.Move;
import com.kelseyde.calvin.model.PieceType;

import java.util.Set;

public interface PseudoLegalMoveGenerator {

    PieceType getPieceType();

    Set<Move> generateLegalMoves(Game game, int startSquare);

}
