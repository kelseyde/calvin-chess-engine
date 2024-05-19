package com.kelseyde.calvin.endgame;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;

public interface Tablebase {

    Move getTablebaseMove(Board board) throws TablebaseException;

    boolean canProbeTablebase(long timeoutMs);

}
