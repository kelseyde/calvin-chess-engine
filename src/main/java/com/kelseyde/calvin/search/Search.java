package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;

import java.time.Duration;

/**
 * Search for the best move/evaluation (encapsulated in a {@link SearchResult}) within a give time limit.
 * See {@link Searcher} for a concrete implementation, using an iterative deepening approach.
 */
public interface Search {

    /**
     * Initialise the searcher with a new {@link Board}.
     */
    void init(Board board);

    /**
     * Set the position of the {@link Board}.
     */
    void setPosition(Board board);

    /**
     * Search the current position for the best move.
     * @param duration How long to search for
     * @return a {@link SearchResult} containing the best move and the current eval.
     */
    SearchResult search(Duration duration);

    /**
     * Clear any cached search information (transposition table, history/killer tables etc.)
     */
    void clearHistory();

    /**
     * Print the current search statistics.
     */
    void logStatistics();

    /**
     * Configure the size of the transposition table.
     */
    void setHashSize(int hashSizeMb);

    /**
     * Configure the number of cores to use for parallel search.
     */
    void setCores(int cores);

}
