package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.PieceType;

import java.util.List;

public interface PseudoLegalMoveGenerator {

    PieceType getPieceType();

    List<Move> generatePseudoLegalMoves(Board board, boolean capturesOnly);

    long generateAttackMaskFromSquare(Board board, int square, boolean isWhite);

}
