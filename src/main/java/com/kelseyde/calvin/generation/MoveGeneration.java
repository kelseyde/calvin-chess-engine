package com.kelseyde.calvin.generation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.MoveList;

import java.util.List;

public interface MoveGeneration {

    MoveList generateMoves(Board board);

    MoveList generateMoves(Board board, MoveFilter filter);

    boolean isCheck(Board board, boolean isWhite);

    enum MoveFilter {
        ALL,
        CAPTURES_AND_CHECKS,
        CAPTURES_ONLY
    }

}
