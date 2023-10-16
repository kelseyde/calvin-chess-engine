package com.kelseyde.calvin.search;

import java.time.Duration;

/**
 * Search for the best move/evaluation (encapsulated in a {@link SearchResult}) within a give time limit.
 * See {@link Searcher} for a concrete implementation, using an iterative deepening approach.
 */
public interface Search {

    SearchResult search(Duration duration);

    void clearHistory();

    void logStatistics();

}
