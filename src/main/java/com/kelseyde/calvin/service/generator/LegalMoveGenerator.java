package com.kelseyde.calvin.service.generator;

import com.kelseyde.calvin.model.board.Board;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.model.piece.PieceType;

import java.util.Set;

public interface LegalMoveGenerator {

    PieceType getPieceType();

    Set<Move> generateLegalMoves(Board board, int startSquare);

}
