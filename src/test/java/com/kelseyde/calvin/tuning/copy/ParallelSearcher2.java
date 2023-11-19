package com.kelseyde.calvin.tuning.copy;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.utils.BoardUtils;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public class ParallelSearcher2 implements Search {

    private Board board;

    private final TranspositionTable2 transpositionTable;

    private final List<Searcher2> searchers;

    public ParallelSearcher2(Board board, int threadCount) {
        this.board = board;
        this.transpositionTable = new TranspositionTable2();
        this.searchers = IntStream.range(0, threadCount)
                .mapToObj(i -> new Searcher2(BoardUtils.copy(board), transpositionTable))
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
            SearchResult result = selectResult(threads).get();
            threads.forEach(thread -> thread.cancel(true));
            return result;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Combines the {@link SearchResult} results of the different threads and selects a final result to use.
     * Simply selects the result from the thread which searched to the greatest depth.
     */
    private CompletableFuture<SearchResult> selectResult(List<CompletableFuture<SearchResult>> threads) {
        CompletableFuture<SearchResult> collector = CompletableFuture.completedFuture(new SearchResult(0, null, 0));
        for (CompletableFuture<SearchResult> thread : threads) {
            collector = collector.thenCombine(thread, (thread1, thread2) -> thread1.depth() > thread2.depth() ? thread1 : thread2);
        }
        return collector;
    }

    @Override
    public void clearHistory() {
        if (transpositionTable != null) {
            transpositionTable.clear();
        }
        searchers.forEach(Searcher2::clearHistory);
    }

    @Override
    public void logStatistics() {

    }

}
