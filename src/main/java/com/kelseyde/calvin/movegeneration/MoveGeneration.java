package com.kelseyde.calvin.movegeneration;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;

import java.util.List;

public interface MoveGeneration {

    List<Move> generateMoves(Board board);

    List<Move> generateMoves(Board board, MoveFilter filter);

    boolean isCheck(Board board, boolean isWhite);

    enum MoveFilter {
        ALL,
        CAPTURES_AND_CHECKS,
        CAPTURES_ONLY
    }

}
