package com.kelseyde.calvin.generation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;

public interface MoveGeneration {

    Move[] generateMoves(Board board);

    Move[] generateMoves(Board board, MoveFilter filter);

    boolean isCheck(Board board, boolean white);

    enum MoveFilter {
        ALL,
        NOISY,
        QUIET,
        CAPTURES_ONLY,
    }

}
