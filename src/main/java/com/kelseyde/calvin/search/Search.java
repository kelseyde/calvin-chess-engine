package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.transposition.TranspositionTable;

import java.time.Duration;

/**
 * Search for the best move/evaluation (encapsulated in a {@link SearchResult}) within a give time limit.
 * See {@link Searcher} for a concrete implementation, using an iterative deepening approach.
 */
public interface Search {

    /**
     * Set the position of the {@link Board}.
     */
    void setPosition(Board board);

    /**
     * Set the maximum number of nodes that should be searched.
     */
    void setNodeLimit(int nodeLimit);

    /**
     * Set the size of the {@link TranspositionTable}.
     */
    void setHashSize(int hashSizeMb);

    /**
     * Set the number of threads to use while searching.
     */
    void setThreadCount(int threadCount);

    /**
     * Search the current position for the best move.
     * @param duration How long to search for
     * @return a {@link SearchResult} containing the best move and the current eval.
     */
    SearchResult search(Duration duration);

    /**
     * @return the {@link TranspositionTable} used by the search algorithm.
     */
    TranspositionTable getTranspositionTable();

    /**
     * Clear any cached search information (transposition table, history/killer tables etc.)
     */
    void clearHistory();

    /**
     * Print the current search statistics.
     */
    void logStatistics();

}
