package com.kelseyde.calvin.search.moveordering;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;

import java.util.List;

/**
 * In order for a chess engine's search algorithm to perform well, the best moves need to be searched first. We don't know
 * for certain the best move at the beginning of the search, but there are several heuristics we can use to approximate
 * which moves are likely to be good and therefore searching early.
 *
 * @see <a href="https://www.chessprogramming.org/Move_Ordering">Chess Programming Wiki</a>
 */
public interface MoveOrdering {

    List<Move> orderMoves(Board board, List<Move> moves, Move previousBestMove, int depth);

    int scoreMove(Board board, Move move, Move previousBestMove, int depth);

    void addKillerMove(int ply, Move newKiller);

    void addHistoryMove(int plyRemaining, Move historyMove, boolean isWhite);

    void clear();

}
