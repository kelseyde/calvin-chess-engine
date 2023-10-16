package com.kelseyde.calvin.movegeneration;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;

import java.util.List;

public interface MoveGeneration {

    List<Move> generateMoves(Board board, boolean capturesOnly);

    boolean isCheck(Board board, boolean isWhite);

}
