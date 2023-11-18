package com.kelseyde.calvin.search;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.search.transposition.TranspositionTable;
import com.kelseyde.calvin.tuning.SearchResult;
import com.kelseyde.calvin.utils.BoardUtils;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

/**
 * Implementation of {@link Search} that uses a parallel search strategy called 'Lazy SMP'. The idea is to have multiple
 * threads searching the same position simultaneously, but sharing a transposition table, so that each thread benefits
 * from the work of the others. Each thread is simply a {@link Searcher} that runs its own iterative deepening loop.
 *
 * @see <a href="https://www.chessprogramming.org/Lazy_SMP">Chess Programming Wiki</a>
 */
public class ParallelSearcher implements Search {

    private Board board;

    private final TranspositionTable transpositionTable;

    private final List<Searcher> searchers;

    public ParallelSearcher(Board board, int threadCount) {
        this.board = board;
        this.transpositionTable = new TranspositionTable();
        this.searchers = IntStream.range(0, threadCount)
                .mapToObj(i -> new Searcher(BoardUtils.copy(board), transpositionTable))
                .toList();
    }

    @Override
    public void init(Board board) {
        this.board = board;
    }

    @Override
    public void setPosition(Board board) {
        this.board = board;
        searchers.forEach(searcher -> searcher.setPosition(BoardUtils.copy(board)));
    }

    @Override
    public SearchResult search(Duration duration) {
        try {
            setPosition(board);
            List<CompletableFuture<SearchResult>> threads = searchers.stream()
                    .map(searcher -> CompletableFuture.supplyAsync(() -> searcher.search(duration)))
                    .toList();
            SearchResult result = (SearchResult) CompletableFuture.anyOf(threads.toArray(CompletableFuture[]::new)).get();
            threads.forEach(thread -> thread.cancel(true));
            return result;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearHistory() {
        if (transpositionTable != null) {
            transpositionTable.clear();
        }
        searchers.forEach(Searcher::clearHistory);
    }

    @Override
    public void logStatistics() {

    }

}
