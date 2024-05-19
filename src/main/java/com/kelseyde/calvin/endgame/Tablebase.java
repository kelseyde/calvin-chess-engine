package com.kelseyde.calvin.endgame;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;

/**
 * Endgame tablebases are pre-calculated endgame evaluation tables generated by retrograde analysis. They record the
 * ultimate result of the game (i.e. a win for white, a win for black, or a draw) and the number of moves required to
 * achieve that result.
 * </p>
 * Tablebases cover every possible configuration of pieces and side-to-move possible up to a certain number of pieces
 * remaining. The current largest tablebases cover up to seven pieces remaining.
 * </p>
 * @see <a href="https://www.chessprogramming.org/Endgame_Tablebases">Chess Programming Wiki</a>
 */
public interface Tablebase {

    Move getTablebaseMove(Board board) throws TablebaseException;

    boolean canProbeTablebase(long timeoutMs);

}