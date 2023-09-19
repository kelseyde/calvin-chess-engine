package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.PieceType;
import com.kelseyde.calvin.board.move.Move;

import java.util.Set;

public interface PseudoLegalMoveGenerator {

    PieceType getPieceType();

    Set<Move> generatePseudoLegalMoves(Board board);

}
